package org.icgc_argo.workflow_graph_lib.workflow.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleQuery {
  @JsonProperty("query")
  private String query;

  @JsonProperty("variables")
  private Map<String, Object> variables;
}
