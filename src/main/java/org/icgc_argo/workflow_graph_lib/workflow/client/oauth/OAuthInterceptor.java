package org.icgc_argo.workflow_graph_lib.workflow.client.oauth;

import static java.lang.String.format;

import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import okhttp3.Interceptor;
import okhttp3.Response;

@RequiredArgsConstructor
public class OAuthInterceptor implements Interceptor {

  private final OAuthManager oauthManager;

  @Override
  public Response intercept(@NonNull Chain chain) throws IOException {
    val headerValue = format("Bearer %s", oauthManager.getToken());
    val request = chain.request().newBuilder().addHeader("Authorization", headerValue).build();
    return chain.proceed(request);
  }
}
