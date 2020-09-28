package org.icgc_argo.workflow_graph_lib.exceptions;

/**
 * Abstract class for exceptions that occur in the workflow graph and should be handled by the
 * Exception spec found in the README
 */
public abstract class GraphException extends RuntimeException {
  public GraphException() {
    super();
  }

  public GraphException(String exception) {
    super(exception);
  }

  public GraphException(Exception exception) {
    super(exception);
  }
}
