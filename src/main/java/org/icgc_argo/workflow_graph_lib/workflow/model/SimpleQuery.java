package org.icgc_argo.workflow_graph_lib.workflow.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class SimpleQuery {
  @JsonProperty("query")
  private String query;

  @JsonProperty("variables")
  private Map<String, Object> variables;
}
