package org.icgc_argo.workflow_graph_lib.exceptions;

/** Exceptions that are involuntary and retryable (ie. network issues) */
public class RequeueableException extends GraphException {
  public RequeueableException() {
    super();
  }

  public RequeueableException(String exception) {
    super(exception);
  }

  public RequeueableException(Exception exception) {
    super(exception);
  }
}
