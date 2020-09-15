package org.icgc_argo.workflow_graph_lib.polyglot.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GraphFunctionLanguage {
  @JsonProperty("js")
  JS("js"),

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
