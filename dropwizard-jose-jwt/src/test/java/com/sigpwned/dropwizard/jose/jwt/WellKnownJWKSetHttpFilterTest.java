package com.sigpwned.dropwizard.jose.jwt;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import com.nimbusds.jose.jwk.JWKSet;

public class WellKnownJWKSetHttpFilterTest {
  @Test
  public void shouldCallChainDoFilterIfNotWellKnownJwks() throws Exception {
    JWKSet jwks = new JWKSet();

    WellKnownJWKSetHttpFilter unit = new WellKnownJWKSetHttpFilter(jwks);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn("/alpha/bravo/charlie");

    HttpServletResponse response = mock(HttpServletResponse.class);

    FilterChain chain = mock(FilterChain.class);

    unit.doFilter(request, response, chain);

    verify(chain).doFilter(request, response);
  }
}
