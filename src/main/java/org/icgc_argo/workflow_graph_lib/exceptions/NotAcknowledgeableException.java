package org.icgc_argo.workflow_graph_lib.exceptions;

/** Exceptions that are voluntary and retryable (ie. filter fail) */
public abstract class NotAcknowledgeableException extends RuntimeException {
  public NotAcknowledgeableException(String exception) {
    super(exception);
  }

  public NotAcknowledgeableException() {
    super();
  }
}
