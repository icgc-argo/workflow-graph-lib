package org.icgc_argo.workflow_graph_lib.polyglot.exceptions;

import org.icgc_argo.workflow_graph_lib.exceptions.DeadLetterQueueableException;

public class GraphFunctionValueException extends DeadLetterQueueableException {
  public GraphFunctionValueException(String exception) {
    super(exception);
  }

  public GraphFunctionValueException() {
    super();
  }
}
