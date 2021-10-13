package org.icgc_argo.workflow_graph_lib.exceptions;

import com.pivotal.rabbitmq.stream.Transaction;

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

  public CommittableException(String exception, Transaction<?> tx) {
    super(exception, tx);
  }

  public CommittableException(Exception exception, Transaction<?> tx) {
    super(exception, tx);
  }
}
