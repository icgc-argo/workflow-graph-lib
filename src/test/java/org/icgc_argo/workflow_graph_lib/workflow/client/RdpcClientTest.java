package org.icgc_argo.workflow_graph_lib.workflow.client;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Slf4j
@Disabled("Useful for testing from IDE.")
class RdpcClientTest {

  private static final String rdpcUrl = "fill me in!";

  @Test
  public void TestWorkflowStatus() {
    val rdpcClient = new RdpcClient(rdpcUrl);
    log.info(
        rdpcClient
            .getWorkflowStatus("fill me in!")
            .doOnNext(s -> System.out.println(s))
            .block());
  }
}
