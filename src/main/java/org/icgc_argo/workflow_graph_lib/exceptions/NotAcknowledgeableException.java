package org.icgc_argo.workflow_graph_lib.exceptions;

import com.pivotal.rabbitmq.stream.Transaction;

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

  public NotAcknowledgeableException(String exception, Transaction<?> tx) {
    super(exception, tx);
  }

  public NotAcknowledgeableException(Exception exception, Transaction<?> tx) {
    super(exception, tx);
  }
}
