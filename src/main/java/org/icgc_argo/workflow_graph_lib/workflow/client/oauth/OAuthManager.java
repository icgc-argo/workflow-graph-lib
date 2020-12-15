package org.icgc_argo.workflow_graph_lib.workflow.client.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

@Slf4j
public class OAuthManager {

  /** Dependencies */
  private final ClientCredentials clientCredentials;

  public OAuthManager(ClientCredentials clientCredentials) {
    this.clientCredentials = clientCredentials;

    // Setup initial state
    this.publicKey = getPublicKey();
    this.token = requestNewToken();
  }

  /** State */
  private String publicKey;

  private String token;

  public String getToken() {
    try {
      Jwts.parserBuilder().setSigningKey(publicKey).build().parse(token);
      return token;
    } catch (JwtException ex) {
      log.warn("Refreshing JWT for reason: {}", ex.getMessage());
      log.trace("Throwable: ", ex);
      token = requestNewToken();
      return token;
    }
  }

  public void refreshToken() {
    token = requestNewToken();
  }

  public void refreshPublicKey() {
    publicKey = getPublicKey();
  }

  @SneakyThrows
  String requestNewToken() {
    val requestBuilder = new Request.Builder();
    val formBuilder = new FormBody.Builder();

    formBuilder.add("client_id", clientCredentials.getClientId());
    formBuilder.add("client_secret", clientCredentials.getClientSecret());
    formBuilder.add("grant_type", "client_credentials");

    requestBuilder.url(clientCredentials.getTokenUri()).post(formBuilder.build());

    val client = new OkHttpClient();
    val responseString = client.newCall(requestBuilder.build()).execute().body().string();

    val mapper = new ObjectMapper();
    final Map<String, String> responseMap = mapper.readValue(responseString, Map.class);
    return responseMap.get("access_token").strip();
  }

  @SneakyThrows
  String getPublicKey() {
    val requestBuilder = new Request.Builder();
    requestBuilder.url(clientCredentials.getPublicKeyUri()).get();

    val client = new OkHttpClient();
    return client
        .newCall(requestBuilder.build())
        .execute()
        .body()
        .string()
        .replaceAll("-----BEGIN PUBLIC KEY-----\r\n", "")
        .replaceAll("\r\n-----END PUBLIC KEY-----\r\n", "")
        .strip();
  }
}
