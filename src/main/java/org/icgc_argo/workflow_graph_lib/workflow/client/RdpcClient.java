package org.icgc_argo.workflow_graph_lib.workflow.client;

import static java.lang.String.format;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.exception.ApolloHttpException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.OkHttpClient;
import org.icgc_argo.workflow_graph_lib.exceptions.DeadLetterQueueableException;
import org.icgc_argo.workflow_graph_lib.exceptions.GraphException;
import org.icgc_argo.workflow_graph_lib.exceptions.RequeueableException;
import org.icgc_argo.workflow_graph_lib.graphql.client.GetWorkflowStateQuery;
import org.icgc_argo.workflow_graph_lib.graphql.client.StartRunMutation;
import org.icgc_argo.workflow_graph_lib.graphql.client.type.WorkflowEngineParams;
import org.icgc_argo.workflow_graph_lib.workflow.model.RunRequest;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

@Slf4j
public class RdpcClient {

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
        sinkError(sink, "Bad Request talking to API.", DeadLetterQueueableException.class);
      } else if (errorCode == 401) {
        sinkError(sink, "Not Authenticated to talk to API.", RequeueableException.class);
      } else if (errorCode == 403) {
        sinkError(sink, "Not Authorized to talk to API.", RequeueableException.class);
      } else if (errorCode == 409) {
        sinkError(sink, "Conflict trying to mutate.", DeadLetterQueueableException.class);
      } else if (errorCode >= 500) {
        sinkError(sink, "API throwing 5xx error.", RequeueableException.class);
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
   * @param nestedException Nexted exception
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
}
