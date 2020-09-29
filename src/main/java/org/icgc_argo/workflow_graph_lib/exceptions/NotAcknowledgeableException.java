package org.icgc_argo.workflow_graph_lib.exceptions;

/** Exceptions that are voluntary and retryable (ie. filter fail) */
public class NotAcknowledgeableException extends GraphException {
  public NotAcknowledgeableException() {
    super();
  }

  public NotAcknowledgeableException(String exception) {
    super(exception);
  }

  public NotAcknowledgeableException(Exception exception) {
    super(exception);
  }
}
