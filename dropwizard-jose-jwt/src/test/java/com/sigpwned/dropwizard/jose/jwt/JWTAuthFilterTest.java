/*-
 * =================================LICENSE_START==================================
 * dropwizard-jose-jwt
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.dropwizard.jose.jwt;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.security.KeyStore;
import java.security.Principal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sigpwned.dropwizard.jose.jwt.factory.DefaultJWTFactory;
import com.sigpwned.dropwizard.jose.jwt.tool.keygen.KeygenTool;
import com.sigpwned.dropwizard.jose.jwt.tool.keygen.KeygenToolConfiguration;
import com.sigpwned.dropwizard.jose.jwt.util.KeyStores;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.Authorizer;
import io.dropwizard.auth.UnauthorizedHandler;

public class JWTAuthFilterTest {
  public class ExamplePrincipal implements Principal {
    @Override
    public String getName() {
      return "John Smith";
    }
  }

  public File keyStoreFile;
  public KeyStore keyStore;
  public JWKSet jwks;

  public static final String PASSWORD = "password";

  public static final String QUERY_PARAMETER_NAME = "queryToken";

  public static final String COOKIE_PARAMETER_NAME = "cookieToken";

  public static final JWTClaimsSet NONE = new JWTClaimsSet.Builder().build();


  @Before
  public void setupJWTFactoryTest() throws Exception {
    keyStoreFile = File.createTempFile("keystore.", ".p12");

    KeygenToolConfiguration ktc = new KeygenToolConfiguration();
    ktc.realm = "realm";
    ktc.password = PASSWORD;
    ktc.out = new PrintStream(new FileOutputStream(keyStoreFile));

    KeygenTool.main(ktc);

    keyStore = KeyStores.loadKeyStore(keyStoreFile, PASSWORD);

    jwks = JWKSet.load(keyStore, null);
  }

  @After
  public void cleanupJWTFactoryTest() {
    keyStoreFile.delete();
  }

  public static final String ISSUER = "issuer";

  @Test
  public void shouldRejectUnauthorizedRequest() throws IOException {
    @SuppressWarnings("unchecked")
    Authenticator<SignedJWT, ExamplePrincipal> authenticator = mock(Authenticator.class);

    @SuppressWarnings("unchecked")
    Authorizer<ExamplePrincipal> authorizer = mock(Authorizer.class);

    JWTAuthFilter<ExamplePrincipal> unit = JWTAuthFilter.<ExamplePrincipal>builder()
        .setAuthenticator(authenticator).setAuthorizer(authorizer).setIssuer(ISSUER)
        .setQueryParameterName(QUERY_PARAMETER_NAME).setCookieParameterName(COOKIE_PARAMETER_NAME)
        .setSigningAlgorithm(JWTFactory.DEFAULT_SIGNING_ALGORITHM).setJWKs(jwks)
        .setUnauthorizedHandler(new UnauthorizedHandler() {
          @Override
          public Response buildResponse(String prefix, String realm) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
          }
        }).buildAuthFilter();

    MultivaluedHashMap<String, String> queryParameters = new MultivaluedHashMap<>();

    Map<String, Cookie> cookieParameters = new HashMap<>();

    UriInfo uriInfo = mock(UriInfo.class);
    when(uriInfo.getQueryParameters()).thenReturn(queryParameters);

    ContainerRequestContext request = mock(ContainerRequestContext.class);
    when(request.getCookies()).thenReturn(cookieParameters);
    when(request.getUriInfo()).thenReturn(uriInfo);

    WebApplicationException problem;
    try {
      unit.filter(request);
      problem = null;
    } catch (WebApplicationException e) {
      problem = e;
    }

    assertThat(problem, not(nullValue()));

    assertThat(problem.getResponse().getStatus(), is(HttpURLConnection.HTTP_UNAUTHORIZED));
  }

  @Test
  public void shouldRespectAuthorizationHeader() throws Exception {
    final SignedJWT jwt = new DefaultJWTFactory(jwks, ISSUER, Duration.ofHours(1L)).create(NONE);

    final ExamplePrincipal principal = new ExamplePrincipal();

    @SuppressWarnings("unchecked")
    Authenticator<SignedJWT, ExamplePrincipal> authenticator = mock(Authenticator.class);
    when(authenticator.authenticate(argThat(SignedJWTMatcher.is(jwt))))
        .thenReturn(Optional.of(principal));

    @SuppressWarnings("unchecked")
    Authorizer<ExamplePrincipal> authorizer = mock(Authorizer.class);

    JWTAuthFilter<ExamplePrincipal> unit = JWTAuthFilter.<ExamplePrincipal>builder()
        .setAuthenticator(authenticator).setAuthorizer(authorizer).setIssuer(ISSUER)
        .setQueryParameterName(QUERY_PARAMETER_NAME).setCookieParameterName(COOKIE_PARAMETER_NAME)
        .setSigningAlgorithm(JWTFactory.DEFAULT_SIGNING_ALGORITHM).setJWKs(jwks)
        .setUnauthorizedHandler(new UnauthorizedHandler() {
          @Override
          public Response buildResponse(String prefix, String realm) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
          }
        }).buildAuthFilter();

    MultivaluedHashMap<String, String> queryParameters = new MultivaluedHashMap<>();

    Map<String, Cookie> cookieParameters = new HashMap<>();

    UriInfo uriInfo = mock(UriInfo.class);
    when(uriInfo.getQueryParameters()).thenReturn(queryParameters);

    ContainerRequestContext request = mock(ContainerRequestContext.class);
    when(request.getCookies()).thenReturn(cookieParameters);
    when(request.getUriInfo()).thenReturn(uriInfo);
    when(request.getHeaderString(HttpHeaders.AUTHORIZATION))
        .thenReturn(JWTAuthFilter.DEFAULT_PREFIX + " " + jwt.serialize());

    unit.filter(request);
  }

  @Test
  public void shouldRespectQueryParameter() throws Exception {
    final SignedJWT jwt = new DefaultJWTFactory(jwks, ISSUER, Duration.ofHours(1L)).create(NONE);

    final ExamplePrincipal principal = new ExamplePrincipal();

    @SuppressWarnings("unchecked")
    Authenticator<SignedJWT, ExamplePrincipal> authenticator = mock(Authenticator.class);
    when(authenticator.authenticate(argThat(SignedJWTMatcher.is(jwt))))
        .thenReturn(Optional.of(principal));

    @SuppressWarnings("unchecked")
    Authorizer<ExamplePrincipal> authorizer = mock(Authorizer.class);

    JWTAuthFilter<ExamplePrincipal> unit = JWTAuthFilter.<ExamplePrincipal>builder()
        .setAuthenticator(authenticator).setAuthorizer(authorizer).setIssuer(ISSUER)
        .setQueryParameterName(QUERY_PARAMETER_NAME).setCookieParameterName(COOKIE_PARAMETER_NAME)
        .setSigningAlgorithm(JWTFactory.DEFAULT_SIGNING_ALGORITHM).setJWKs(jwks)
        .setUnauthorizedHandler(new UnauthorizedHandler() {
          @Override
          public Response buildResponse(String prefix, String realm) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
          }
        }).buildAuthFilter();

    MultivaluedHashMap<String, String> queryParameters = new MultivaluedHashMap<>();
    queryParameters.putSingle(QUERY_PARAMETER_NAME, jwt.serialize());

    Map<String, Cookie> cookieParameters = new HashMap<>();

    UriInfo uriInfo = mock(UriInfo.class);
    when(uriInfo.getQueryParameters()).thenReturn(queryParameters);

    ContainerRequestContext request = mock(ContainerRequestContext.class);
    when(request.getCookies()).thenReturn(cookieParameters);
    when(request.getUriInfo()).thenReturn(uriInfo);

    unit.filter(request);
  }

  @Test
  public void shouldRespectCookieParameter() throws Exception {
    final SignedJWT jwt = new DefaultJWTFactory(jwks, ISSUER, Duration.ofHours(1L)).create(NONE);

    final ExamplePrincipal principal = new ExamplePrincipal();

    @SuppressWarnings("unchecked")
    Authenticator<SignedJWT, ExamplePrincipal> authenticator = mock(Authenticator.class);
    when(authenticator.authenticate(argThat(SignedJWTMatcher.is(jwt))))
        .thenReturn(Optional.of(principal));

    @SuppressWarnings("unchecked")
    Authorizer<ExamplePrincipal> authorizer = mock(Authorizer.class);

    JWTAuthFilter<ExamplePrincipal> unit = JWTAuthFilter.<ExamplePrincipal>builder()
        .setAuthenticator(authenticator).setAuthorizer(authorizer).setIssuer(ISSUER)
        .setQueryParameterName(QUERY_PARAMETER_NAME).setCookieParameterName(COOKIE_PARAMETER_NAME)
        .setSigningAlgorithm(JWTFactory.DEFAULT_SIGNING_ALGORITHM).setJWKs(jwks)
        .setUnauthorizedHandler(new UnauthorizedHandler() {
          @Override
          public Response buildResponse(String prefix, String realm) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
          }
        }).buildAuthFilter();

    MultivaluedHashMap<String, String> queryParameters = new MultivaluedHashMap<>();

    Map<String, Cookie> cookieParameters = new HashMap<>();
    cookieParameters.put(COOKIE_PARAMETER_NAME, new Cookie(COOKIE_PARAMETER_NAME, jwt.serialize()));

    UriInfo uriInfo = mock(UriInfo.class);
    when(uriInfo.getQueryParameters()).thenReturn(queryParameters);

    ContainerRequestContext request = mock(ContainerRequestContext.class);
    when(request.getCookies()).thenReturn(cookieParameters);
    when(request.getUriInfo()).thenReturn(uriInfo);

    unit.filter(request);
  }

  @Test
  public void shouldReadQueryParameterFirst() throws Exception {
    final SignedJWT jwt1 = new DefaultJWTFactory(jwks, ISSUER, Duration.ofHours(1L)).create(NONE);
    final SignedJWT jwt2 = new DefaultJWTFactory(jwks, ISSUER, Duration.ofHours(1L)).create(NONE);
    final SignedJWT jwt3 = new DefaultJWTFactory(jwks, ISSUER, Duration.ofHours(1L)).create(NONE);

    final ExamplePrincipal principal = new ExamplePrincipal();

    @SuppressWarnings("unchecked")
    Authenticator<SignedJWT, ExamplePrincipal> authenticator = mock(Authenticator.class);
    when(authenticator.authenticate(argThat(SignedJWTMatcher.is(jwt1))))
        .thenReturn(Optional.of(principal));
    when(authenticator.authenticate(argThat(SignedJWTMatcher.is(jwt2))))
        .thenReturn(Optional.empty());
    when(authenticator.authenticate(argThat(SignedJWTMatcher.is(jwt3))))
        .thenReturn(Optional.empty());

    @SuppressWarnings("unchecked")
    Authorizer<ExamplePrincipal> authorizer = mock(Authorizer.class);

    JWTAuthFilter<ExamplePrincipal> unit = JWTAuthFilter.<ExamplePrincipal>builder()
        .setAuthenticator(authenticator).setAuthorizer(authorizer).setIssuer(ISSUER)
        .setQueryParameterName(QUERY_PARAMETER_NAME).setCookieParameterName(COOKIE_PARAMETER_NAME)
        .setSigningAlgorithm(JWTFactory.DEFAULT_SIGNING_ALGORITHM).setJWKs(jwks)
        .setUnauthorizedHandler(new UnauthorizedHandler() {
          @Override
          public Response buildResponse(String prefix, String realm) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
          }
        }).buildAuthFilter();

    MultivaluedHashMap<String, String> queryParameters = new MultivaluedHashMap<>();
    queryParameters.putSingle(QUERY_PARAMETER_NAME, jwt1.serialize());

    Map<String, Cookie> cookieParameters = new HashMap<>();
    cookieParameters.put(COOKIE_PARAMETER_NAME,
        new Cookie(COOKIE_PARAMETER_NAME, jwt2.serialize()));

    UriInfo uriInfo = mock(UriInfo.class);
    when(uriInfo.getQueryParameters()).thenReturn(queryParameters);

    ContainerRequestContext request = mock(ContainerRequestContext.class);
    when(request.getCookies()).thenReturn(cookieParameters);
    when(request.getUriInfo()).thenReturn(uriInfo);
    when(request.getHeaderString(HttpHeaders.AUTHORIZATION))
        .thenReturn(JWTAuthFilter.DEFAULT_PREFIX + " " + jwt3.serialize());

    unit.filter(request);
  }

  @Test
  public void shouldReadCookieParameterSecond() throws Exception {
    final SignedJWT jwt1 = new DefaultJWTFactory(jwks, ISSUER, Duration.ofHours(1L)).create(NONE);
    final SignedJWT jwt2 = new DefaultJWTFactory(jwks, ISSUER, Duration.ofHours(1L)).create(NONE);

    final ExamplePrincipal principal = new ExamplePrincipal();

    @SuppressWarnings("unchecked")
    Authenticator<SignedJWT, ExamplePrincipal> authenticator = mock(Authenticator.class);
    when(authenticator.authenticate(argThat(SignedJWTMatcher.is(jwt1))))
        .thenReturn(Optional.of(principal));
    when(authenticator.authenticate(argThat(SignedJWTMatcher.is(jwt2))))
        .thenReturn(Optional.empty());

    @SuppressWarnings("unchecked")
    Authorizer<ExamplePrincipal> authorizer = mock(Authorizer.class);

    JWTAuthFilter<ExamplePrincipal> unit = JWTAuthFilter.<ExamplePrincipal>builder()
        .setAuthenticator(authenticator).setAuthorizer(authorizer).setIssuer(ISSUER)
        .setQueryParameterName(QUERY_PARAMETER_NAME).setCookieParameterName(COOKIE_PARAMETER_NAME)
        .setSigningAlgorithm(JWTFactory.DEFAULT_SIGNING_ALGORITHM).setJWKs(jwks)
        .setUnauthorizedHandler(new UnauthorizedHandler() {
          @Override
          public Response buildResponse(String prefix, String realm) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
          }
        }).buildAuthFilter();

    MultivaluedHashMap<String, String> queryParameters = new MultivaluedHashMap<>();

    Map<String, Cookie> cookieParameters = new HashMap<>();
    cookieParameters.put(COOKIE_PARAMETER_NAME,
        new Cookie(COOKIE_PARAMETER_NAME, jwt1.serialize()));

    UriInfo uriInfo = mock(UriInfo.class);
    when(uriInfo.getQueryParameters()).thenReturn(queryParameters);

    ContainerRequestContext request = mock(ContainerRequestContext.class);
    when(request.getCookies()).thenReturn(cookieParameters);
    when(request.getUriInfo()).thenReturn(uriInfo);
    when(request.getHeaderString(HttpHeaders.AUTHORIZATION))
        .thenReturn(JWTAuthFilter.DEFAULT_PREFIX + " " + jwt2.serialize());

    unit.filter(request);
  }

  private static class SignedJWTMatcher implements ArgumentMatcher<SignedJWT> {
    public static SignedJWTMatcher is(SignedJWT target) {
      return new SignedJWTMatcher(target);
    }

    private final SignedJWT target;

    public SignedJWTMatcher(SignedJWT target) {
      this.target = target;
    }

    /**
     * @return the target
     */
    public SignedJWT getTarget() {
      return target;
    }

    @Override
    public boolean matches(SignedJWT argument) {
      if (argument == null)
        return false;
      if (argument == getTarget())
        return true;
      return Objects.equals(argument.serialize(), getTarget().serialize());
    }
  }
}
