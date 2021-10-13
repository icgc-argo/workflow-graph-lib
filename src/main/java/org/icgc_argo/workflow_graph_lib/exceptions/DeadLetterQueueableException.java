package org.icgc_argo.workflow_graph_lib.exceptions;

import com.pivotal.rabbitmq.stream.Transaction;

/** Exceptions that are involuntary and not retryable (ie. dynamic code errors) */
public class DeadLetterQueueableException extends GraphException {
  public DeadLetterQueueableException() {
    super();
  }

  public DeadLetterQueueableException(String exception) {
    super(exception);
  }

  public DeadLetterQueueableException(Exception exception) {
    super(exception);
  }

  public DeadLetterQueueableException(String exception, Transaction<?> tx) {
    super(exception, tx);
  }

  public DeadLetterQueueableException(Exception exception, Transaction<?> tx) {
    super(exception, tx);
  }
}
