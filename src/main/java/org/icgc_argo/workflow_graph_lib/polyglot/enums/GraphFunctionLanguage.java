package org.icgc_argo.workflow_graph_lib.polyglot.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GraalVM Polyglot languages available for use in Workflow Graph
 */
public enum GraphFunctionLanguage {
  /**
   * Javascript
   */
  @JsonProperty("js")
  JS("js"),

  /**
   * Python
   */
  @JsonProperty("python")
  PYTHON("python");

  private final String text;

  GraphFunctionLanguage(final String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }
}
