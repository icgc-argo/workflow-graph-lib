package org.icgc_argo.workflow_graph_lib.exceptions;

import com.pivotal.rabbitmq.stream.Transaction;
import java.util.Optional;

/**
 * Abstract class for exceptions that occur in the workflow graph and should be handled by the
 * Exception spec found in the README
 */
public abstract class GraphException extends RuntimeException {
  private Transaction<?> tx;

  public GraphException() {
    super();
  }

  public GraphException(String exception) {
    super(exception);
  }

  public GraphException(Exception exception) {
    super(exception);
  }

  public GraphException(String exception, Transaction<?> tx) {
    super(exception);
    this.tx = tx;
  }

  public GraphException(Exception exception, Transaction<?> tx) {
    super(exception);
    this.tx = tx;
  }

  public Optional<Transaction<?>> getTransaction() {
    return Optional.ofNullable(tx);
  }
}
