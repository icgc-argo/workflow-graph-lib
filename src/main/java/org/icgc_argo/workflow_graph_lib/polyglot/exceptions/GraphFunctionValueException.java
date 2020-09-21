package org.icgc_argo.workflow_graph_lib.polyglot.exceptions;

public class GraphFunctionValueException extends RuntimeException {
  public GraphFunctionValueException(String exception) {
    super(exception);
  }

  public GraphFunctionValueException() {
    super();
  }
}
