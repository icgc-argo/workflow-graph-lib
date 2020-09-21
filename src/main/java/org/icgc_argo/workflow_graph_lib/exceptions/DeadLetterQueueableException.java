package org.icgc_argo.workflow_graph_lib.exceptions;

/** Exceptions that are involuntary and not retryable (ie. dynamic code errors) */
public abstract class DeadLetterQueueableException extends RuntimeException {
  public DeadLetterQueueableException(String exception) {
    super(exception);
  }

  public DeadLetterQueueableException() {
    super();
  }
}
