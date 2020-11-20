package org.icgc_argo.workflow_graph_lib.workflow.client;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.exception.ApolloHttpException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.OkHttpClient;
import org.icgc_argo.workflow_graph_lib.exceptions.DeadLetterQueueableException;
import org.icgc_argo.workflow_graph_lib.exceptions.GraphException;
import org.icgc_argo.workflow_graph_lib.exceptions.RequeueableException;
import org.icgc_argo.workflow_graph_lib.graphql.client.GetAnalysisForGraphEventQuery;
import org.icgc_argo.workflow_graph_lib.graphql.client.GetWorkflowStateQuery;
import org.icgc_argo.workflow_graph_lib.graphql.client.PublishedAnalysesForGraphEventQuery;
import org.icgc_argo.workflow_graph_lib.graphql.client.StartRunMutation;
import org.icgc_argo.workflow_graph_lib.graphql.client.fragment.AnalysisDetailsForGraphEvent;
import org.icgc_argo.workflow_graph_lib.graphql.client.type.WorkflowEngineParams;
import org.icgc_argo.workflow_graph_lib.schema.AnalysisFile;
import org.icgc_argo.workflow_graph_lib.schema.GraphEvent;
import org.icgc_argo.workflow_graph_lib.utils.RecordToFlattenedMap;
import org.icgc_argo.workflow_graph_lib.workflow.model.RunRequest;
import org.icgc_argo.workflow_graph_lib.workflow.model.SimpleQuery;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

@Slf4j
public class RdpcClient {

  // Exception Messages
  private static final String EX_BAD_REQUEST = "Bad Request talking to API.";
  private static final String EX_NOT_AUTHENTICATED = "Not Authenticated to talk to API.";
  private static final String EX_NOT_AUTHORIZED = "Not Authorized to talk to API.";
  private static final String EX_MUTATION_CONFLICT = "Conflict trying to mutate.";
  private static final String EX_4XX_ERROR = "API throwing 4xx error.";
  private static final String EX_5XX_ERROR = "API throwing 5xx error.";

  /** State */
  private final ApolloClient client;

  public RdpcClient(@NonNull String url) {
    this(url, 60);
  }

  public RdpcClient(@NonNull String url, long timeout) {
    val okHttpBuilder = new OkHttpClient.Builder();
    okHttpBuilder.connectTimeout(timeout, TimeUnit.SECONDS);
    okHttpBuilder.callTimeout(timeout, TimeUnit.SECONDS);
    okHttpBuilder.readTimeout(timeout, TimeUnit.SECONDS);
    okHttpBuilder.writeTimeout(timeout, TimeUnit.SECONDS);

    this.client = ApolloClient.builder().serverUrl(url).okHttpClient(okHttpBuilder.build()).build();
  }

  /**
   * Handles ApolloException on the failure callback. This is where we can transform HTTP status
   * codes into exceptions.
   *
   * @param sink Sink corresponding to the Mono being published by client.
   * @param e ApolloException that may actually be an ApolloHttpException.
   */
  private static void handleApolloException(MonoSink<?> sink, ApolloException e) {
    if (e instanceof ApolloHttpException) {
      log.trace("ApolloHttpException thrown... checking status code");
      val errorCode = ((ApolloHttpException) e).code();
      if (errorCode == 400) {
        sinkError(sink, EX_BAD_REQUEST, DeadLetterQueueableException.class);
      } else if (errorCode == 401) {
        sinkError(sink, EX_NOT_AUTHENTICATED, RequeueableException.class);
      } else if (errorCode == 403) {
        sinkError(sink, EX_NOT_AUTHORIZED, RequeueableException.class);
      } else if (errorCode == 409) {
        sinkError(sink, EX_MUTATION_CONFLICT, DeadLetterQueueableException.class);
      } else if (errorCode >= 500) {
        sinkError(sink, EX_5XX_ERROR, RequeueableException.class);
      }
    } else {
      // Not HTTP Exception, DLQ it.
      log.trace("ApolloException thrown");
      sinkError(sink, e, DeadLetterQueueableException.class);
    }
  }

  /**
   * Puts exception on Mono sink with logging and correct type of exception.
   *
   * @param sink Sink corresponding to the Mono published by client.
   * @param message String message for log and exception
   * @param exceptionType Type of GraphException
   */
  private static void sinkError(
      MonoSink<?> sink, String message, Class<? extends GraphException> exceptionType) {
    try {
      val exception = exceptionType.getDeclaredConstructor(String.class).newInstance(message);
      log.error(message);
      sink.error(exception);
    } catch (Exception e) {
      log.error("Something happened trying to map exception type.");
      throw new DeadLetterQueueableException(e);
    }
  }

  /**
   * Puts exception on Mono sink with logging and correct type of exception.
   *
   * @param sink Sink corresponding to the Mono published by client.
   * @param nestedException Nested exception
   * @param exceptionType Type of GraphException
   */
  private static void sinkError(
      MonoSink<?> sink, Exception nestedException, Class<? extends GraphException> exceptionType) {
    try {
      log.error("Encountered nested exception: {}", nestedException.getMessage());
      val exception =
          exceptionType.getDeclaredConstructor(Exception.class).newInstance(nestedException);
      sink.error(exception);
    } catch (Exception e) {
      log.error("Something happened trying to map exception type.");
      throw new DeadLetterQueueableException(e);
    }
  }

  /**
   * Adapter to convert between Models of workflow engine params for use with apollo
   *
   * @param params WorkflowEngineParams model owned by developer
   * @return WorkflowEngineParams model owned by Apollo code gen
   */
  private static WorkflowEngineParams engineParamsAdapter(
      @NonNull org.icgc_argo.workflow_graph_lib.workflow.model.WorkflowEngineParams params) {
    val builder = WorkflowEngineParams.builder();
    builder.revision(params.getRevision());
    builder.launchDir(params.getLaunchDir());
    builder.projectDir(params.getProjectDir());
    builder.workDir(params.getWorkDir());
    builder.resume(params.getResume());
    return builder.build();
  }

  /**
   * Converter to convert analysis to GraphEvent
   *
   * @param analysis Analysis from query
   * @return GraphEvent defined by GraphEvent.avsc avro schema
   */
  private static Optional<GraphEvent> analysisToGraphEventConverter(
      AnalysisDetailsForGraphEvent analysis) {

    // short circuit if can't build GraphEvent
    if (analysis == null
        || analysis.getStudyId().isEmpty()
        || analysis.getAnalysisState().isEmpty()
        || analysis.getAnalysisType().isEmpty()) {
      return Optional.empty();
    }

    val donorIds =
        analysis.getDonors().orElseGet(Collections::emptyList).stream()
            .map(d -> d.getDonorId().orElse(""))
            .collect(toList());

    String experimentalStrategy = "";
    try {
      val experiment =
          (Map<String, Object>) analysis.getExperiment().orElseGet(Collections::emptyMap);
      experimentalStrategy = experiment.getOrDefault("experimental_strategy", "").toString();
    } catch (Exception e) {
      log.error("Experiment is not map", e);
    }

    val files =
        analysis.getFiles().orElseGet(Collections::emptyList).stream()
            .map(f -> new AnalysisFile(f.getDataType().orElse("")))
            .collect(toList());

    return Optional.of(
        new GraphEvent(
            UUID.randomUUID().toString(),
            analysis.getAnalysisId(),
            analysis.getAnalysisState().get().toString(),
            analysis.getAnalysisType().get(),
            analysis.getStudyId().get(),
            experimentalStrategy,
            donorIds,
            files));
  }

  /**
   * Start a new workflow run
   *
   * @param runRequest Run Request describing all information to run the workflow
   * @return Returns a Mono of the Workflow RunId
   */
  public Mono<String> startRun(RunRequest runRequest) {
    return Mono.create(
        sink -> {
          val mutationBuilder =
              StartRunMutation.builder()
                  .workflowUrl(runRequest.getWorkflowUrl())
                  .workflowParams(runRequest.getWorkflowParams());
          if (runRequest.getWorkflowEngineParams() != null) {
            mutationBuilder.workflowEngineParams(
                engineParamsAdapter(runRequest.getWorkflowEngineParams()));
          }

          client
              .mutate(mutationBuilder.build())
              .enqueue(
                  new ApolloCall.Callback<>() {
                    @Override
                    public void onResponse(
                        @NotNull Response<Optional<StartRunMutation.Data>> response) {
                      response
                          .getData()
                          .ifPresentOrElse(
                              data ->
                                  data.getStartRun()
                                      .ifPresentOrElse(
                                          startRun ->
                                              startRun
                                                  .getRunId()
                                                  .ifPresentOrElse(
                                                      sink::success,
                                                      () ->
                                                          sinkError(
                                                              sink,
                                                              "No runId Found in response.",
                                                              DeadLetterQueueableException.class)),
                                          () ->
                                              sinkError(
                                                  sink,
                                                  "Empty Response from API.",
                                                  RequeueableException.class)),
                              () ->
                                  sinkError(
                                      sink, "No Response from API.", RequeueableException.class));
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                      log.trace("ApolloException thrown");
                      handleApolloException(sink, e);
                    }
                  });
        });
  }

  /**
   * Get the status of a workflow
   *
   * @param runId The runId of the workflow as a String
   * @return Returns a Mono of the state of the workflow
   */
  public Mono<String> getWorkflowStatus(String runId) {
    return Mono.create(
        sink ->
            client
                .query(new GetWorkflowStateQuery(runId))
                .enqueue(
                    new ApolloCall.Callback<>() {
                      @Override
                      public void onResponse(
                          @NotNull Response<Optional<GetWorkflowStateQuery.Data>> response) {
                        response
                            .getData()
                            .ifPresentOrElse(
                                data ->
                                    data.getRuns()
                                        .ifPresentOrElse(
                                            runs ->
                                                runs.stream()
                                                    .findFirst()
                                                    .ifPresentOrElse(
                                                        run ->
                                                            run.getState()
                                                                .ifPresentOrElse(
                                                                    sink::success,
                                                                    () ->
                                                                        sinkError(
                                                                            sink,
                                                                            format(
                                                                                "Missing state for run %s.",
                                                                                runId),
                                                                            DeadLetterQueueableException
                                                                                .class)),
                                                        () ->
                                                            sinkError(
                                                                sink,
                                                                format("Run %s not found.", runId),
                                                                RequeueableException.class)),
                                            () ->
                                                sinkError(
                                                    sink,
                                                    format("Run %s not found.", runId),
                                                    RequeueableException.class)),
                                () ->
                                    sinkError(
                                        sink,
                                        format("Run %s not found.", runId),
                                        RequeueableException.class));
                      }

                      @Override
                      public void onFailure(@NotNull ApolloException e) {
                        log.trace("ApolloException thrown");
                        handleApolloException(sink, e);
                      }
                    }));
  }

  /**
   * Create a GraphEvent for a given analysisId
   *
   * @param analysisId The analysisId of the analysis to use as string
   * @return Returns a Mono of GraphEvent for the analysis
   */
  public Mono<GraphEvent> createGraphEventForAnalysis(String analysisId) {
    return Mono.create(
        sink ->
            client
                .query(new GetAnalysisForGraphEventQuery(analysisId))
                .enqueue(
                    new ApolloCall.Callback<>() {
                      @Override
                      public void onResponse(
                          @NotNull
                              Response<Optional<GetAnalysisForGraphEventQuery.Data>> response) {

                        response
                            .getData()
                            .ifPresentOrElse(
                                data ->
                                    data.getAnalyses()
                                        .ifPresentOrElse(
                                            analyses ->
                                                analyses.stream()
                                                    .findFirst()
                                                    .ifPresentOrElse(
                                                        analysis ->
                                                            analysisToGraphEventConverter(
                                                                    analysis
                                                                        .getFragments()
                                                                        .getAnalysisDetailsForGraphEvent())
                                                                .ifPresentOrElse(
                                                                    sink::success,
                                                                    () ->
                                                                        sinkError(
                                                                            sink,
                                                                            "Analysis couldn't be converted to GraphEvent",
                                                                            DeadLetterQueueableException
                                                                                .class)),
                                                        () ->
                                                            sinkError(
                                                                sink,
                                                                format(
                                                                    "Analysis %s not found.",
                                                                    analysisId),
                                                                RequeueableException.class)),
                                            () ->
                                                sinkError(
                                                    sink,
                                                    format("Analysis %s not found.", analysisId),
                                                    RequeueableException.class)),
                                () ->
                                    sinkError(
                                        sink,
                                        format("Analysis %s not found.", analysisId),
                                        RequeueableException.class));
                      }

                      @Override
                      public void onFailure(@NotNull ApolloException e) {
                        log.trace("ApolloException thrown");
                        handleApolloException(sink, e);
                      }
                    }));
  }

  /**
   * Get the status of a workflow
   *
   * @param runId The runId of the workflow as a String
   * @return Returns a Mono of the state of the workflow
   */
  public Mono<List<GraphEvent>> createGraphEventsForRun(String runId) {
    return Mono.create(
        sink ->
            client
                .query(new PublishedAnalysesForGraphEventQuery(runId))
                .enqueue(
                    new ApolloCall.Callback<>() {
                      @Override
                      public void onResponse(
                          @NotNull
                              Response<Optional<PublishedAnalysesForGraphEventQuery.Data>>
                                  response) {
                        response
                            .getData()
                            .ifPresentOrElse(
                                data ->
                                    data.getRuns()
                                        .ifPresentOrElse(
                                            runs ->
                                                runs.stream()
                                                    .findFirst()
                                                    .ifPresentOrElse(
                                                        run ->
                                                            run.getProducedAnalyses()
                                                                .ifPresentOrElse(
                                                                    producedAnalyses ->
                                                                        sink.success(
                                                                            producedAnalyses
                                                                                .stream()
                                                                                .map(
                                                                                    producedAnalyse ->
                                                                                        analysisToGraphEventConverter(
                                                                                            producedAnalyse
                                                                                                .getFragments()
                                                                                                .getAnalysisDetailsForGraphEvent()))
                                                                                .map(Optional::get)
                                                                                .collect(toList())),
                                                                    () ->
                                                                        sinkError(
                                                                            sink,
                                                                            format(
                                                                                "No produced analyses for run %s.",
                                                                                runId),
                                                                            DeadLetterQueueableException
                                                                                .class)),
                                                        () ->
                                                            sinkError(
                                                                sink,
                                                                format("Run %s not found.", runId),
                                                                RequeueableException.class)),
                                            () ->
                                                sinkError(
                                                    sink,
                                                    format("Run %s not found.", runId),
                                                    RequeueableException.class)),
                                () ->
                                    sinkError(
                                        sink,
                                        format("Run %s not found.", runId),
                                        RequeueableException.class));
                      }

                      @Override
                      public void onFailure(@NotNull ApolloException e) {
                        log.trace("ApolloException thrown");
                        handleApolloException(sink, e);
                      }
                    }));
  }

  /**
   * Execute a dynamic GQL query with a GraphEvent that is flattened and made available as query
   * vars
   *
   * @param query The dynamic query to execute
   * @param event The event that will be flattened and made available to the query as query vars
   * @return Returns a Mono with the query response
   */
  public Mono<Map<String, Object>> simpleQueryWithEvent(String query, GraphEvent event) {
    val data = RecordToFlattenedMap.from(event);

    return WebClient.create()
        .post()
        .uri(client.getServerUrl().uri())
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new SimpleQuery(query, data)))
        .retrieve()
        .onRawStatus(
            status -> status == 400,
            clientResponse -> {
              throw new DeadLetterQueueableException(EX_BAD_REQUEST);
            })
        .onRawStatus(
            status -> status == 401,
            clientResponse -> {
              throw new RequeueableException(EX_NOT_AUTHENTICATED);
            })
        .onRawStatus(
            status -> status == 403,
            clientResponse -> {
              throw new RequeueableException(EX_NOT_AUTHORIZED);
            })
        .onStatus(
            HttpStatus::is4xxClientError,
            clientResponse -> {
              throw new DeadLetterQueueableException(EX_4XX_ERROR);
            })
        .onStatus(
            HttpStatus::is5xxServerError,
            clientResponse -> {
              throw new RequeueableException(EX_5XX_ERROR);
            })
        .bodyToMono(new ParameterizedTypeReference<>() {});
  }
}
