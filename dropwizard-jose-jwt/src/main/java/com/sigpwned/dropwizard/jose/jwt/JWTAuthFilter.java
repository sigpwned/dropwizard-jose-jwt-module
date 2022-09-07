/*-
 * =================================LICENSE_START==================================
 * dropwizard-jwt
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

import static java.util.Objects.requireNonNull;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import java.util.Set;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.Authorizer;
import io.dropwizard.auth.UnauthorizedHandler;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;

/**
 * This class uses the realm as the issuer.
 */
public class JWTAuthFilter<P extends Principal> extends AuthFilter<SignedJWT, P> {
  private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthFilter.class);

  /**
   * The default query parameter for passing in a token
   */
  public static final String DEFAULT_QUERY_PARAMETER_NAME = "token";

  /**
   * The default query parameter for passing in a token
   */
  public static final String DEFAULT_COOKIE_PARAMETER_NAME = "token";

  /**
   * The default value of the prefix, which is the leading value of the Authorization header
   */
  public static final String DEFAULT_PREFIX = "Bearer";

  public static <P extends Principal> JWTAuthFilter.Builder<P> builder() {
    return new JWTAuthFilter.Builder<>();
  }

  /**
   * Builder for {@link OAuthCredentialAuthFilter}.
   * <p>
   * An {@link Authenticator} must be provided during the building process.
   * </p>
   *
   * @param <P> the type of the principal
   */
  public static class Builder<P extends Principal>
      extends AuthFilterBuilder<SignedJWT, P, JWTAuthFilter<P>> {
    private String issuer;
    private JWSAlgorithm signingAlgorithm;
    private JWKSource<SecurityContext> jwkSource;
    private String queryParameterName;
    private String cookieParameterName;

    private Builder() {
      setPrefix(DEFAULT_PREFIX);
      queryParameterName = DEFAULT_QUERY_PARAMETER_NAME;
      cookieParameterName = DEFAULT_COOKIE_PARAMETER_NAME;
    }

    /**
     * In general, the issuer should be the domain name of the application. By convention, the
     * issuer should also match the authentication realm.
     * 
     * @param issuer the issuer to set
     */
    public Builder<P> setIssuer(String issuer) {
      this.issuer = issuer;
      return this;
    }

    /**
     * @param signingAlgorithm the signingAlgorithm to set
     */
    public Builder<P> setSigningAlgorithm(JWSAlgorithm signingAlgorithm) {
      this.signingAlgorithm = signingAlgorithm;
      return this;
    }

    /**
     * @param jwkSource the jwkSource to set
     */
    public Builder<P> setJWKSource(JWKSource<SecurityContext> jwkSource) {
      this.jwkSource = jwkSource;
      return this;
    }

    /**
     * This sets the underlying jwkSource. If this method is called, then there is no need to call
     * {@link #setJWKSource(JWKSource)}.
     * 
     * @param jwks the jwkSource to set
     */
    public Builder<P> setJWKs(JWKSet jwks) {
      return setJWKSource(new ImmutableJWKSet<>(jwks));
    }

    /**
     * @param queryParameterName the queryParameterName to set
     */
    public Builder<P> setQueryParameterName(String queryParameterName) {
      this.queryParameterName = queryParameterName;
      return this;
    }

    /**
     * @param cookieParameterName the cookieParameterName to set
     */
    public Builder<P> setCookieParameterName(String cookieParameterName) {
      this.cookieParameterName = cookieParameterName;
      return this;
    }

    @Override
    public Builder<P> setRealm(String realm) {
      return (Builder<P>) super.setRealm(realm);
    }

    @Override
    public Builder<P> setPrefix(String prefix) {
      return (Builder<P>) super.setPrefix(prefix);
    }

    @Override
    public Builder<P> setAuthorizer(Authorizer<P> authorizer) {
      return (Builder<P>) super.setAuthorizer(authorizer);
    }

    @Override
    public Builder<P> setAuthenticator(Authenticator<SignedJWT, P> authenticator) {
      return (Builder<P>) super.setAuthenticator(authenticator);
    }

    @Override
    public Builder<P> setUnauthorizedHandler(UnauthorizedHandler unauthorizedHandler) {
      return (Builder<P>) super.setUnauthorizedHandler(unauthorizedHandler);
    }

    @Override
    protected JWTAuthFilter<P> newInstance() {
      requireNonNull(issuer, "issuer is not set");
      requireNonNull(signingAlgorithm, "signingAlgorithm is not set");
      requireNonNull(jwkSource, "jwkSource is not set");

      return new JWTAuthFilter<>(issuer, signingAlgorithm, jwkSource, queryParameterName,
          cookieParameterName);
    }
  }

  private static class Authorization {
    public static Authorization fromString(String s) {
      int index = s.indexOf(' ');
      if (index == -1)
        throw new IllegalArgumentException("no method");

      String method = s.substring(0, index).strip();
      String credentials = s.substring(index + 1, s.length()).strip();

      return of(method, credentials);
    }

    public static Authorization of(String method, String credentials) {
      return new Authorization(method, credentials);
    }

    private final String method;
    private final String credentials;

    public Authorization(String method, String credentials) {
      this.method = method;
      this.credentials = credentials;
    }

    /**
     * @return the method
     */
    public String getMethod() {
      return method;
    }

    /**
     * @return the credentials
     */
    public String getCredentials() {
      return credentials;
    }

    @Override
    public String toString() {
      return getMethod() + " " + getCredentials();
    }
  }

  /**
   * An optional query parameter to pass the JWT.
   * 
   * @see #DEFAULT_QUERY_PARAMETER_NAME
   */
  private final String queryParameterName;

  /**
   * An optional cookie parameter to pass the JWT.
   * 
   * @see #DEFAULT_COOKIE_PARAMETER_NAME
   */
  private final String cookieParameterName;

  private final JWTProcessor<SecurityContext> processor;

  public JWTAuthFilter(String issuer, JWSAlgorithm signingAlgorithm, JWKSet jwks) {
    this(issuer, signingAlgorithm, new ImmutableJWKSet<>(jwks));
  }

  public JWTAuthFilter(String issuer, JWSAlgorithm signingAlgorithm,
      JWKSource<SecurityContext> jwkSource) {
    this(issuer, signingAlgorithm, jwkSource, DEFAULT_QUERY_PARAMETER_NAME,
        DEFAULT_COOKIE_PARAMETER_NAME);
  }

  @SuppressWarnings("unchecked")
  public JWTAuthFilter(String issuer, JWSAlgorithm signingAlgorithm,
      JWKSource<SecurityContext> jwkSource, String queryParameterName, String cookieParameterName) {
    if (issuer == null)
      throw new NullPointerException();
    if (signingAlgorithm == null)
      throw new NullPointerException();
    if (jwkSource == null)
      throw new NullPointerException();

    // We're going to create our processor manually
    ConfigurableJWTProcessor<SecurityContext> p = new DefaultJWTProcessor<>();

    // We don't need to check our type since we don't need to distinguish between JOSE types and,
    // nothing other than a JWT will pass validation, but the framework forces us to attach a type
    // to the object, so let's just check for it.
    p.setJWETypeVerifier(DefaultJOSEObjectTypeVerifier.JWT);

    // Tell the processor to use our default algorithms, and to look for keys in the given source.
    p.setJWSKeySelector(new JWSVerificationKeySelector<>(signingAlgorithm, jwkSource));

    // We need to validate some claim values:
    // - iss: We always set to realm
    //
    // We also need to validate the presence of some other claims:
    // - iat: Issued at, which we always set
    // - exp: Expires at, which we always set
    // - jti: JWT ID, which we always set
    p.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
        new JWTClaimsSet.Builder().issuer(issuer).build(), Set.of("iat", "exp", "jti")));

    this.processor = p;
    this.queryParameterName = queryParameterName;
    this.cookieParameterName = cookieParameterName;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    String credentials;

    // Try to read a JWT from the query parameter first, if we have one
    credentials = Optional
        .ofNullable(requestContext.getUriInfo().getQueryParameters().getFirst(queryParameterName))
        .orElse(null);

    // Try to read a JWT from the cookie parameter next, if we have one
    if (credentials == null && cookieParameterName != null) {
      credentials = Optional.ofNullable(requestContext.getCookies().get(cookieParameterName))
          .map(Cookie::getValue).orElse(null);
    }

    // Try to read a JWT from the authentication header last, if we have one
    if (credentials == null) {
      try {
        credentials = Optional.ofNullable(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
            .map(Authorization::fromString).filter(a -> a.getMethod().equalsIgnoreCase(prefix))
            .map(Authorization::getCredentials).orElse(null);
      } catch (IllegalArgumentException e) {
        // No problem. This just isn't valid authentication.
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Failed to parse authorization", e);
        credentials = null;
      }
    }

    // Treat the credentials as a JWT and try to extract claims from them
    SignedJWT signedJwt = null;
    if (credentials != null) {
      try {
        JWT jwt = JWTParser.parse(credentials);
        if (jwt instanceof SignedJWT) {
          signedJwt = (SignedJWT) jwt;

          // Make sure our JWT is properly signed
          processor.process(signedJwt, null);

          // Make sure the claims can be parsed
          signedJwt.getJWTClaimsSet();
        }
      } catch (Exception e) {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Failed to process JWT claims", e);
        signedJwt = null;
      }
    }

    // See if the application accepts our claims, which may be null. If not, fail as unauthorized.
    if (!authenticate(requestContext, signedJwt, javax.ws.rs.core.SecurityContext.BASIC_AUTH)) {
      throw unauthorizedHandler.buildException(prefix, realm);
    }
  }
}
