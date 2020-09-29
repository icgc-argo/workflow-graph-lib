package org.icgc_argo.workflow_graph_lib.polyglot.exceptions;

import org.icgc_argo.workflow_graph_lib.exceptions.DeadLetterQueueableException;

public class GraphFunctionException extends DeadLetterQueueableException {
  public GraphFunctionException(String exception) {
    super(exception);
  }

  public GraphFunctionException() {
    super();
  }
}
