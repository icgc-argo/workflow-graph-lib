package org.icgc_argo.workflow_graph_lib.exceptions;

/** Exceptions that are voluntary and not retryable (ie. activation function returns false) */
public class CommittableException extends GraphException {
  public CommittableException() {
    super();
  }

  public CommittableException(String exception) {
    super(exception);
  }

  public CommittableException(Exception exception) {
    super(exception);
  }
}
