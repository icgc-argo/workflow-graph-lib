package org.icgc_argo.workflow_graph_lib.polyglot.exceptions;

import org.icgc_argo.workflow_graph_lib.exceptions.DeadLetterQueueableException;

public class GraphFunctionUnsupportedLanguageException extends DeadLetterQueueableException {
  public GraphFunctionUnsupportedLanguageException(String exception) {
    super(exception);
  }

  public GraphFunctionUnsupportedLanguageException() {
    super();
  }
}
