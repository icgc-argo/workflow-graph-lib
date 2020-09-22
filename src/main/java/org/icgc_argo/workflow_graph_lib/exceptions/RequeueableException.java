package org.icgc_argo.workflow_graph_lib.exceptions;

/** Exceptions that are involuntary and retryable (ie. network issues) */
public abstract class RequeueableException extends RuntimeException {
  public RequeueableException(String exception) {
    super(exception);
  }

  public RequeueableException() {
    super();
  }
}
