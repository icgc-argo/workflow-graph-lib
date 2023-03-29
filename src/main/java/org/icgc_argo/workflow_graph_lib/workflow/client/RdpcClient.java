package org.icgc_argo.workflow_graph_lib.workflow.client;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.icgc_argo.workflow_graph_lib.polyglot.GraalvmLock.LOCK;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Error;
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
import org.icgc_argo.workflow_graph_lib.graphql.client.*;
import org.icgc_argo.workflow_graph_lib.graphql.client.fragment.AnalysisDetailsForGraphEvent;
import org.icgc_argo.workflow_graph_lib.graphql.client.type.RequestEngineParameters;
import org.icgc_argo.workflow_graph_lib.schema.AnalysisFile;
import org.icgc_argo.workflow_graph_lib.schema.AnalysisSample;
import org.icgc_argo.workflow_graph_lib.schema.GraphEvent;
import org.icgc_argo.workflow_graph_lib.utils.RecordToFlattenedMap;
import org.icgc_argo.workflow_graph_lib.workflow.client.oauth.ClientCredentials;
import org.icgc_argo.workflow_graph_lib.workflow.client.oauth.OAuthInterceptor;
import org.icgc_argo.workflow_graph_lib.workflow.client.oauth.OAuthManager;
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

  private OAuthManager oAuthManager;

  public RdpcClient(@NonNull String url) {
    this(url, 60);
  }

  public RdpcClient(@NonNull String url, long timeout) {
    val okHttp =
        new OkHttpClient.Builder()
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .callTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .build();

    this.client = ApolloClient.builder().serverUrl(url).okHttpClient(okHttp).build();
  }

  public RdpcClient(
      @NonNull String url, long timeout, @NonNull ClientCredentials clientCredentials) {
    this.oAuthManager = new OAuthManager(clientCredentials);
    val oauthInterceptor = new OAuthInterceptor(this.oAuthManager);

    val okHttp =
        new OkHttpClient.Builder()
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .callTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .addInterceptor(oauthInterceptor)
            .build();

    this.client = ApolloClient.builder().serverUrl(url).okHttpClient(okHttp).build();
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
   * Custom logic to handle graphQL specific errors that don't appear as HTTP Error Codes This is
   * dirty as errors from GraphQL are not strongly typed.
   */
  private void handleGraphQLError(MonoSink<?> sink, Error error) {
    if (error.getMessage().toLowerCase().contains("access")) {
      sinkError(sink, EX_NOT_AUTHORIZED, RequeueableException.class);
    } else {
      sinkError(
          sink,
          format("Cannot handle error from GraphQL %s", error.getMessage()),
          DeadLetterQueueableException.class);
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
  private static RequestEngineParameters engineParamsAdapter(
      @NonNull org.icgc_argo.workflow_graph_lib.workflow.model.WorkflowEngineParams params) {
    val builder = RequestEngineParameters.builder();
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

    val analysisSamples =
        analysis.getDonors().orElseGet(Collections::emptyList).stream()
            .flatMap(
                donor ->
                    donor.getSpecimens().orElseGet(Collections::emptyList).stream()
                        .flatMap(
                            specimen ->
                                specimen.getSamples().orElseGet(Collections::emptyList).stream()
                                    .map(
                                        sample ->
                                            AnalysisSample.newBuilder()
                                                .setSampleId(sample.getSampleId().orElse(""))
                                                .setSubmitterSampleId(
                                                    sample.getSubmitterSampleId().orElse(""))
                                                .setSubmitterDonorId(
                                                    donor.getSubmitterDonorId().orElse(""))
                                                .setDonorId(donor.getDonorId().orElse(""))
                                                .setSubmitterSpecimenId(
                                                    specimen.getSubmitterSpecimenId().orElse(""))
                                                .setSpecimenId(specimen.getSpecimenId().orElse(""))
                                                .setTumourNormalDesignation(
                                                    specimen
                                                        .getTumourNormalDesignation()
                                                        .orElse(""))
                                                .build())))
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
            analysisSamples,
            files));
  }

  /**
   * Start a new workflow run
   *
   * @param runRequest Run Request describing all information to run the workflow
   * @return Returns a Mono of the Workflow RunId
   */
  public Mono<String> startRun(RunRequest runRequest) {

   // synchronized (LOCK) {
      log.debug("Lock acquired -D : "+runRequest.getWorkflowParams().toString());
      log.error("Lock acquired -E : "+runRequest.getWorkflowParams().toString());
      log.trace("Lock acquired -T : "+runRequest.getWorkflowParams().toString());
      log.warn("Lock acquired -W : "+runRequest.getWorkflowParams().toString());
      log.info("Lock acquired -I : "+runRequest.getWorkflowParams().toString());
      return Mono.create(
          sink -> {
            log.info("sink context is: "+sink.currentContext().toString());
            log.info("sink is: "+sink.toString());
            val mutationBuilder =
                StartRunMutation.builder()
                    .workflowUrl(runRequest.getWorkflowUrl())
                    .workflowParams(runRequest.getWorkflowParams());
            if (runRequest.getWorkflowEngineParams() != null) {
              mutationBuilder.workflowEngineParams(
                  engineParamsAdapter(runRequest.getWorkflowEngineParams()));
            }


            log.info(" ... calling mutate: "+runRequest.getWorkflowParams().toString());
            client
                .mutate(mutationBuilder.build())
                .enqueue(           
                    new ApolloCall.Callback<>() {
                      @Override
                      public void onResponse(
                          @NotNull Response<Optional<StartRunMutation.Data>> response) {
                        //synchronized (LOCK){
                          if (response.hasErrors()) {
                          log.info(" ... processing MUTATION response for: "+runRequest.getWorkflowParams().toString());
                          log.info(" ... MUTATION response : "+response.toString());
                          handleGraphQLError(sink, response.getErrors().get(0));
                        } else {
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
                                                                  DeadLetterQueueableException
                                                                      .class)),
                                              () ->
                                                  sinkError(
                                                      sink,
                                                      "Empty Response from API.",
                                                      RequeueableException.class)),
                                  () ->
                                      sinkError(
                                          sink, "No Response from API.", RequeueableException.class));
                        }
                        log.info("Release LOCK from response processor");
                    //  }
                      }

                      @Override
                      public void onFailure(@NotNull ApolloException e) {
                        log.trace("ApolloException thrown");
                        handleApolloException(sink, e);
                      }
                    });
            log.info("RELEASING LOCK -I : "+runRequest.getWorkflowParams().toString());
          });
   // }
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
                        if (response.hasErrors()) {
                          handleGraphQLError(sink, response.getErrors().get(0));
                        } else {
                          response
                              .getData()
                              .ifPresentOrElse(
                                  data ->
                                      data.getRuns()
                                          .getContent()
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
                                                                  format(
                                                                      "Run %s not found.", runId),
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
                      }

                      @Override
                      public void onFailure(@NotNull ApolloException e) {
                        log.trace("ApolloException thrown");
                        handleApolloException(sink, e);
                      }
                    }));
  }

  public Mono<Optional<GetWorkflowInfoForRestartQuery.Run>> findWorkflowByRunIdAndRepo(
      String runId, String repository) {
    return Mono.create(
        sink ->
            client
                .query(new GetWorkflowInfoForRestartQuery(runId, repository))
                .enqueue(
                    new ApolloCall.Callback<>() {
                      @Override
                      public void onResponse(
                          @NotNull
                              Response<Optional<GetWorkflowInfoForRestartQuery.Data>> response) {
                        if (response.hasErrors()) {
                          handleGraphQLError(sink, response.getErrors().get(0));
                        } else {
                          sink.success(
                              response
                                  .getData()
                                  .flatMap(
                                      data ->
                                          data.getResult()
                                              .getRuns()
                                              .flatMap(contents -> contents.stream().findFirst())));
                        }
                      }

                      @Override
                      public void onFailure(@NotNull ApolloException e) {
                        sink.error(new Exception("Failed to fetch run by ID!"));
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
                        if (response.hasErrors()) {
                          handleGraphQLError(sink, response.getErrors().get(0));
                        } else {
                          response
                              .getData()
                              .ifPresentOrElse(
                                  data ->
                                      data.getAnalyses()
                                          .getContent()
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
                                                                              "Analysis could not be converted to GraphEvent",
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
                      }

                      @Override
                      public void onFailure(@NotNull ApolloException e) {
                        log.trace("ApolloException thrown");
                        handleApolloException(sink, e);
                      }
                    }));
  }

  /**
   * Creates a GraphEvent from RunID
   *
   * @param runId The runId of the workflow as a String
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
                        if (response.hasErrors()) {
                          handleGraphQLError(sink, response.getErrors().get(0));
                        } else {
                          response
                              .getData()
                              .ifPresentOrElse(
                                  data ->
                                      data.getRuns()
                                          .getContent()
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
                                                                                  .map(
                                                                                      Optional::get)
                                                                                  .collect(
                                                                                      toList())),
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
                                                                  format(
                                                                      "Run %s not found.", runId),
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
        .header(
            "Authorization",
            oAuthManager != null ? format("Bearer %s", oAuthManager.getToken()) : "")
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
        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
        // onErrorMap catches thrown exceptions that are not GraphExceptions (e.g. NetworkTimeout,
        // SSLException) and wraps them with DeadLetterQueueableException
        .onErrorMap(
            throwable -> !(throwable instanceof GraphException),
            throwable -> new DeadLetterQueueableException(throwable.toString()));
  }
}
