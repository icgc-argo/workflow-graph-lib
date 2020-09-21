package org.icgc_argo.workflow_graph_lib.polyglot.exceptions;

public class GraphFunctionUnsupportedLanguageException extends RuntimeException {
  public GraphFunctionUnsupportedLanguageException(String exception) {
    super(exception);
  }

  public GraphFunctionUnsupportedLanguageException() {
    super();
  }
}
