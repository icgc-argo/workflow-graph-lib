package org.icgc_argo.workflow_graph_lib.workflow.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.net.ssl.SSLContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.icgc_argo.workflow_graph_lib.exceptions.DeadLetterQueueableException;
import org.icgc_argo.workflow_graph_lib.exceptions.RequeueableException;
import org.icgc_argo.workflow_graph_lib.schema.AnalysisFile;
import org.icgc_argo.workflow_graph_lib.schema.GraphEvent;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.test.StepVerifier;

@Slf4j
class SimpleQueryWithEventTests {
  private static final String GRAPHQL_PATH = "/graphql";
  private static MockWebServer mockWebServer;

  private static final GraphEvent MOCK_GRAPH_EVENT =
      new GraphEvent(
          UUID.randomUUID().toString(),
          "NON_EXIST_ANALYSIS_ID",
          "anAnalysisState",
          "anAnalysisType",
          "aStudyId",
          "aStrategy",
          List.of("Donor1"),
          List.of(new AnalysisFile("aFileDataType")));

  @BeforeEach
  public void beforeEach() {
    mockWebServer = new MockWebServer();
  }

  @AfterEach
  @SneakyThrows
  public void afterEach() {
    mockWebServer.close();
  }

  @Test
  @SneakyThrows
  public void test_http200_has_no_error() {
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody("{ \"data\": [] }"));

    val url = mockWebServer.url(GRAPHQL_PATH).url().toString();
    val rdpcClient = new RdpcClient(url);

    val mono = rdpcClient.simpleQueryWithEvent("", MOCK_GRAPH_EVENT);

    StepVerifier.create(mono).expectNext(Map.of("data", List.of())).expectComplete().verify();
  }

  @Test
  @SneakyThrows
  public void test_http401_is_requeueable() {
    mockWebServer.enqueue(new MockResponse().setResponseCode(401));

    val url = mockWebServer.url(GRAPHQL_PATH).url().toString();
    val rdpcClient = new RdpcClient(url);

    val mono = rdpcClient.simpleQueryWithEvent("", MOCK_GRAPH_EVENT);

    StepVerifier.create(mono).expectError(RequeueableException.class).verify();
  }

  @Test
  @SneakyThrows
  public void test_network_ssl_exception_handshake_timeout_is_DLQ() {
    // Setup mockWebServer HTTPS/SSL config to generate "SSLException: handshake timeout" in Webclient
    val tunnelProxy = true;
    val sslSocketFactory = SSLContext.getDefault().getSocketFactory();
    mockWebServer.useHttps(sslSocketFactory, tunnelProxy);

    val url = mockWebServer.url(GRAPHQL_PATH).url().toString();
    val rdpcClient = new RdpcClient(url);

    val mono = rdpcClient.simpleQueryWithEvent("", MOCK_GRAPH_EVENT);

    StepVerifier.create(mono).expectError(DeadLetterQueueableException.class).verify();
  }
}
