package org.icgc_argo.workflow_graph_lib.polyglot.exceptions;

public class GraphFunctionException extends RuntimeException {
  public GraphFunctionException(String exception) {
    super(exception);
  }

  public GraphFunctionException() {
    super();
  }
}
