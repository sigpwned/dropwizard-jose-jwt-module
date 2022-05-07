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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.KeyStore;
import java.security.Principal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.UriInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
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

    keyStore = KeyStores.loadKeyStore(KeyStores.DEFAULT_TYPE, keyStoreFile, PASSWORD);

    jwks = JWKSet.load(keyStore, null);
  }

  @After
  public void cleanupJWTFactoryTest() {
    keyStoreFile.delete();
  }

  public static final String ISSUER = "issuer";

  @Test(expected = NotAuthorizedException.class)
  public void shouldRejectUnauthorizedRequest() throws IOException {
    @SuppressWarnings("unchecked")
    Authenticator<JWTClaimsSet, ExamplePrincipal> authenticator = mock(Authenticator.class);

    @SuppressWarnings("unchecked")
    Authorizer<ExamplePrincipal> authorizer = mock(Authorizer.class);

    JWTAuthFilter<ExamplePrincipal> unit = JWTAuthFilter.<ExamplePrincipal>builder()
        .setAuthenticator(authenticator).setAuthorizer(authorizer).setIssuer(ISSUER)
        .setQueryParameterName(QUERY_PARAMETER_NAME).setCookieParameterName(COOKIE_PARAMETER_NAME)
        .setSigningAlgorithm(JWTFactory.DEFAULT_SIGNING_ALGORITHM).setJWKs(jwks)
        .setUnauthorizedHandler(new UnauthorizedHandler() {
          @Override
          public RuntimeException buildException(String prefix, String realm) {
            return new NotAuthorizedException("token");
          }
        }).buildAuthFilter();

    MultivaluedHashMap<String, String> queryParameters = new MultivaluedHashMap<>();

    Map<String, Cookie> cookieParameters = new HashMap<>();

    UriInfo uriInfo = mock(UriInfo.class);
    when(uriInfo.getQueryParameters()).thenReturn(queryParameters);

    ContainerRequestContext request = mock(ContainerRequestContext.class);
    when(request.getCookies()).thenReturn(cookieParameters);
    when(request.getUriInfo()).thenReturn(uriInfo);

    unit.filter(request);
  }

  @Test
  public void shouldRespectAuthorizationHeader() throws Exception {
    final SignedJWT jwt = new JWTFactory(jwks, ISSUER, Duration.ofHours(1L)).create(NONE);

    final ExamplePrincipal principal = new ExamplePrincipal();

    @SuppressWarnings("unchecked")
    Authenticator<JWTClaimsSet, ExamplePrincipal> authenticator = mock(Authenticator.class);
    when(authenticator.authenticate(jwt.getJWTClaimsSet())).thenReturn(Optional.of(principal));

    @SuppressWarnings("unchecked")
    Authorizer<ExamplePrincipal> authorizer = mock(Authorizer.class);

    JWTAuthFilter<ExamplePrincipal> unit = JWTAuthFilter.<ExamplePrincipal>builder()
        .setAuthenticator(authenticator).setAuthorizer(authorizer).setIssuer(ISSUER)
        .setQueryParameterName(QUERY_PARAMETER_NAME).setCookieParameterName(COOKIE_PARAMETER_NAME)
        .setSigningAlgorithm(JWTFactory.DEFAULT_SIGNING_ALGORITHM).setJWKs(jwks)
        .setUnauthorizedHandler(new UnauthorizedHandler() {
          @Override
          public RuntimeException buildException(String prefix, String realm) {
            return new NotAuthorizedException("token");
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
    final SignedJWT jwt = new JWTFactory(jwks, ISSUER, Duration.ofHours(1L)).create(NONE);

    final ExamplePrincipal principal = new ExamplePrincipal();

    @SuppressWarnings("unchecked")
    Authenticator<JWTClaimsSet, ExamplePrincipal> authenticator = mock(Authenticator.class);
    when(authenticator.authenticate(jwt.getJWTClaimsSet())).thenReturn(Optional.of(principal));

    @SuppressWarnings("unchecked")
    Authorizer<ExamplePrincipal> authorizer = mock(Authorizer.class);

    JWTAuthFilter<ExamplePrincipal> unit = JWTAuthFilter.<ExamplePrincipal>builder()
        .setAuthenticator(authenticator).setAuthorizer(authorizer).setIssuer(ISSUER)
        .setQueryParameterName(QUERY_PARAMETER_NAME).setCookieParameterName(COOKIE_PARAMETER_NAME)
        .setSigningAlgorithm(JWTFactory.DEFAULT_SIGNING_ALGORITHM).setJWKs(jwks)
        .setUnauthorizedHandler(new UnauthorizedHandler() {
          @Override
          public RuntimeException buildException(String prefix, String realm) {
            return new NotAuthorizedException("token");
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
    final SignedJWT jwt = new JWTFactory(jwks, ISSUER, Duration.ofHours(1L)).create(NONE);

    final ExamplePrincipal principal = new ExamplePrincipal();

    @SuppressWarnings("unchecked")
    Authenticator<JWTClaimsSet, ExamplePrincipal> authenticator = mock(Authenticator.class);
    when(authenticator.authenticate(jwt.getJWTClaimsSet())).thenReturn(Optional.of(principal));

    @SuppressWarnings("unchecked")
    Authorizer<ExamplePrincipal> authorizer = mock(Authorizer.class);

    JWTAuthFilter<ExamplePrincipal> unit = JWTAuthFilter.<ExamplePrincipal>builder()
        .setAuthenticator(authenticator).setAuthorizer(authorizer).setIssuer(ISSUER)
        .setQueryParameterName(QUERY_PARAMETER_NAME).setCookieParameterName(COOKIE_PARAMETER_NAME)
        .setSigningAlgorithm(JWTFactory.DEFAULT_SIGNING_ALGORITHM).setJWKs(jwks)
        .setUnauthorizedHandler(new UnauthorizedHandler() {
          @Override
          public RuntimeException buildException(String prefix, String realm) {
            return new NotAuthorizedException("token");
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
    final SignedJWT jwt1 = new JWTFactory(jwks, ISSUER, Duration.ofHours(1L)).create(NONE);
    final SignedJWT jwt2 = new JWTFactory(jwks, ISSUER, Duration.ofHours(1L)).create(NONE);
    final SignedJWT jwt3 = new JWTFactory(jwks, ISSUER, Duration.ofHours(1L)).create(NONE);

    final ExamplePrincipal principal = new ExamplePrincipal();

    @SuppressWarnings("unchecked")
    Authenticator<JWTClaimsSet, ExamplePrincipal> authenticator = mock(Authenticator.class);
    when(authenticator.authenticate(jwt1.getJWTClaimsSet())).thenReturn(Optional.of(principal));
    when(authenticator.authenticate(jwt2.getJWTClaimsSet())).thenReturn(Optional.empty());
    when(authenticator.authenticate(jwt3.getJWTClaimsSet())).thenReturn(Optional.empty());

    @SuppressWarnings("unchecked")
    Authorizer<ExamplePrincipal> authorizer = mock(Authorizer.class);

    JWTAuthFilter<ExamplePrincipal> unit = JWTAuthFilter.<ExamplePrincipal>builder()
        .setAuthenticator(authenticator).setAuthorizer(authorizer).setIssuer(ISSUER)
        .setQueryParameterName(QUERY_PARAMETER_NAME).setCookieParameterName(COOKIE_PARAMETER_NAME)
        .setSigningAlgorithm(JWTFactory.DEFAULT_SIGNING_ALGORITHM).setJWKs(jwks)
        .setUnauthorizedHandler(new UnauthorizedHandler() {
          @Override
          public RuntimeException buildException(String prefix, String realm) {
            return new NotAuthorizedException("token");
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
    final SignedJWT jwt1 = new JWTFactory(jwks, ISSUER, Duration.ofHours(1L)).create(NONE);
    final SignedJWT jwt2 = new JWTFactory(jwks, ISSUER, Duration.ofHours(1L)).create(NONE);

    final ExamplePrincipal principal = new ExamplePrincipal();

    @SuppressWarnings("unchecked")
    Authenticator<JWTClaimsSet, ExamplePrincipal> authenticator = mock(Authenticator.class);
    when(authenticator.authenticate(jwt1.getJWTClaimsSet())).thenReturn(Optional.of(principal));
    when(authenticator.authenticate(jwt2.getJWTClaimsSet())).thenReturn(Optional.empty());

    @SuppressWarnings("unchecked")
    Authorizer<ExamplePrincipal> authorizer = mock(Authorizer.class);

    JWTAuthFilter<ExamplePrincipal> unit = JWTAuthFilter.<ExamplePrincipal>builder()
        .setAuthenticator(authenticator).setAuthorizer(authorizer).setIssuer(ISSUER)
        .setQueryParameterName(QUERY_PARAMETER_NAME).setCookieParameterName(COOKIE_PARAMETER_NAME)
        .setSigningAlgorithm(JWTFactory.DEFAULT_SIGNING_ALGORITHM).setJWKs(jwks)
        .setUnauthorizedHandler(new UnauthorizedHandler() {
          @Override
          public RuntimeException buildException(String prefix, String realm) {
            return new NotAuthorizedException("token");
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
}
