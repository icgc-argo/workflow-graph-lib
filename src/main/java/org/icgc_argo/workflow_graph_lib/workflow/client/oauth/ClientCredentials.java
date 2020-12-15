package org.icgc_argo.workflow_graph_lib.workflow.client.oauth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientCredentials {

  private final String clientId;
  private final String clientSecret;
  private final String tokenUri;
  private final String publicKeyUri;
}
