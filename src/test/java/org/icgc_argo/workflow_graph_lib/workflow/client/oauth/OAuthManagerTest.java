package org.icgc_argo.workflow_graph_lib.workflow.client.oauth;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Slf4j
@Disabled("Useful for testing from IDE.")
class OAuthManagerTest {

  private final ClientCredentials clientCredentials =
      ClientCredentials.builder()
          .clientId("fill me in!")
          .clientSecret("fill me in!")
          .tokenUri("fill me in!")
          .publicKeyUri("fill me in!")
          .build();

  @Test
  public void testGettingPublicKey() {
    val oauth = new OAuthManager(clientCredentials);
    val key = oauth.getPublicKey();
    log.info(key);
  }

  @Test
  public void testNewToken() {
    val oauth = new OAuthManager(clientCredentials);
    val token = oauth.requestNewToken();
    log.info(token);
  }

  @Test
  public void testPublicMethodToken() {
    val oauth = new OAuthManager(clientCredentials);
    val token = oauth.getToken();
    log.info(token);
  }
}
