package com.sigpwned.dropwizard.jose.jwt.example.webapp;

import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Cookie;
import org.junit.ClassRule;
import org.junit.Test;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;
import com.nimbusds.jwt.SignedJWT;
import com.sigpwned.dropwizard.jose.jwt.JWTAuthFilter;
import com.sigpwned.dropwizard.jose.jwt.WellKnownJWKSetHttpFilter;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.Account;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.util.Accounts;
import io.dropwizard.testing.junit.DropwizardAppRule;

@SuppressWarnings("deprecation")
public class ExampleWebappTest {
  @ClassRule
  public static final DropwizardAppRule<ExampleConfiguration> RULE =
      new DropwizardAppRule<>(ExampleWebapp.class, "config.yml");

  @Test
  public void smokeTest() {
    assertThat(true, is(true));
  }

  @Test
  public void shouldReadWellKnownJwksProperly() throws Exception {
    String service = httpGet(WellKnownJWKSetHttpFilter.WELL_KNOWN_JWKS_JSON_PATH).getBody();

    String fixture;
    try (Reader r =
        new InputStreamReader(Resources.getResource("fixtures/jwks.json").openStream())) {
      fixture = CharStreams.toString(r);
    }

    assertThat(service, is(fixture));
  }

  @Test
  public void shouldLoginAndAuthenticateSuccessfully() throws Exception {
    // These credentials come from config.yml
    final String id = "100";
    final String username = "administrator";
    final String password = "password";
    final String name = "Big Boss";

    final Account account = Account.of(id, username, name);

    HttpResponse loginResponse =
        httpPost("/v1/login", Map.of("username", username, "password", password));

    Account responseAccount =
        RULE.getObjectMapper().readValue(loginResponse.getBody(), Account.class);

    assertThat(responseAccount, is(account));

    Cookie cookie = Cookie.valueOf(loginResponse.getHeaders().get("set-cookie"));

    assertThat(cookie.getName(), is(JWTAuthFilter.DEFAULT_COOKIE_PARAMETER_NAME));

    SignedJWT jwt = SignedJWT.parse(cookie.getValue());

    Account tokenAccount = Accounts.fromClaims(jwt.getJWTClaimsSet());

    assertThat(tokenAccount, is(account));

    HttpResponse meResponse = httpGet(String.format("/v1/me?%s=%s",
        URLEncoder.encode(JWTAuthFilter.DEFAULT_QUERY_PARAMETER_NAME, StandardCharsets.UTF_8),
        URLEncoder.encode(jwt.serialize(), StandardCharsets.UTF_8)));

    Account meAccount = RULE.getObjectMapper().readValue(meResponse.getBody(), Account.class);

    assertThat(meAccount, is(account));
  }

  @Test(expected = NotAuthorizedException.class)
  public void shouldFailIfUnauthenticatedSuccessfully() throws Exception {
    httpGet("/v1/me");
  }

  private static class HttpResponse {
    public static HttpResponse of(String body, Map<String, String> headers) {
      return new HttpResponse(body, headers);
    }

    private final String body;
    private final Map<String, String> headers;

    public HttpResponse(String body, Map<String, String> headers) {
      this.body = body;
      this.headers = unmodifiableMap(headers);
    }

    /**
     * @return the body
     */
    public String getBody() {
      return body;
    }

    /**
     * @return the headers
     */
    public Map<String, String> getHeaders() {
      return headers;
    }

    @Override
    public int hashCode() {
      return Objects.hash(body, headers);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      HttpResponse other = (HttpResponse) obj;
      return Objects.equals(body, other.body) && Objects.equals(headers, other.headers);
    }

    @Override
    public String toString() {
      final int maxLen = 10;
      return "HttpResponse [body=" + body + ", headers="
          + (headers != null ? toString(headers.entrySet(), maxLen) : null) + "]";
    }

    private String toString(Collection<?> collection, int maxLen) {
      StringBuilder builder = new StringBuilder();
      builder.append("[");
      int i = 0;
      for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
        if (i > 0)
          builder.append(", ");
        builder.append(iterator.next());
      }
      builder.append("]");
      return builder.toString();
    }
  }

  private HttpResponse httpGet(String path) throws IOException {
    URL url = new URL(String.format("http://localhost:%d%s", RULE.getLocalPort(), path));

    HttpURLConnection cn = (HttpURLConnection) url.openConnection();
    try {
      if (cn.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
        throw new NotAuthorizedException("challenge");
      if (cn.getResponseCode() != HttpURLConnection.HTTP_OK)
        throw new IOException("request failed: " + cn.getResponseCode());

      String responseBody;
      try (Reader in = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)) {
        responseBody = CharStreams.toString(in);
      }

      Map<String, String> headers = new HashMap<>();
      for (Map.Entry<String, List<String>> e : cn.getHeaderFields().entrySet()) {
        if (e.getKey() != null) {
          headers.put(e.getKey().toLowerCase(), e.getValue().get(0));
        }
      }

      return HttpResponse.of(responseBody, headers);
    } finally {
      cn.disconnect();
    }
  }

  private HttpResponse httpPost(String path, Map<String, String> requestBody) throws IOException {
    URL url = new URL(String.format("http://localhost:%d%s", RULE.getLocalPort(), path));
    HttpURLConnection cn = (HttpURLConnection) url.openConnection();
    try {
      cn.setDoOutput(true);
      cn.setRequestMethod("POST");
      cn.setRequestProperty("content-type", "application/x-www-form-urlencoded");

      try (OutputStream out = cn.getOutputStream()) {
        out.write(requestBody.entrySet().stream()
            .map(e -> String.format("%s=%s", URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8),
                URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8)))
            .collect(joining("&")).getBytes(StandardCharsets.UTF_8));
      }

      if (cn.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
        throw new NotAuthorizedException("challenge");
      if (cn.getResponseCode() != HttpURLConnection.HTTP_OK)
        throw new IOException("request failed: " + cn.getResponseCode());

      String responseBody;
      try (Reader in = new InputStreamReader(cn.getInputStream(), StandardCharsets.UTF_8)) {
        responseBody = CharStreams.toString(in);
      }

      Map<String, String> headers = new HashMap<>();
      for (Map.Entry<String, List<String>> e : cn.getHeaderFields().entrySet()) {
        if (e.getKey() != null) {
          headers.put(e.getKey().toLowerCase(), e.getValue().get(0));
        }
      }

      return HttpResponse.of(responseBody, headers);
    } finally {
      cn.disconnect();
    }
  }
}
