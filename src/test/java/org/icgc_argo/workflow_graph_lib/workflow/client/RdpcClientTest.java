package org.icgc_argo.workflow_graph_lib.workflow.client;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc_argo.workflow_graph_lib.workflow.client.oauth.ClientCredentials;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Slf4j
@Disabled("Useful for testing from IDE.")
class RdpcClientTest {

  private static final String rdpcUrl = "fill me in!";

  @Test
  public void TestWorkflowStatusNoAuth() {
    val rdpcClient = new RdpcClient(rdpcUrl);
    log.info(
        rdpcClient.getWorkflowStatus("fill me in!").doOnNext(s -> System.out.println(s)).block());
  }

  @Test
  public void TestWorkflowStatusWithOAuth() {
    val clientCredentials =
        ClientCredentials.builder()
            .clientId("fill me in!")
            .clientSecret("fill me in!")
            .tokenUri("fill me in!")
            .publicKeyUri("fill me in!")
            .build();

    val rdpcClient = new RdpcClient(rdpcUrl, 60L, clientCredentials);
    log.info(
        rdpcClient.getWorkflowStatus("fill me in!").doOnNext(s -> System.out.println(s)).block());

    log.info(
        rdpcClient.getWorkflowStatus("fill me in!").doOnNext(s -> System.out.println(s)).block());

    log.info(
        rdpcClient.getWorkflowStatus("fill me in!").doOnNext(s -> System.out.println(s)).block());
  }
}
