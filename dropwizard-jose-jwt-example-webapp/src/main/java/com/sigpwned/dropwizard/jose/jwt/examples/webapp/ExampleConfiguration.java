package com.sigpwned.dropwizard.jose.jwt.examples.webapp;

import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sigpwned.dropwizard.jose.jwt.JWTBundleConfiguration;
import com.sigpwned.dropwizard.jose.jwt.JWTConfiguration;
import io.dropwizard.core.Configuration;

public class ExampleConfiguration extends Configuration implements JWTBundleConfiguration {
  @Valid
  private JWTConfiguration jwts;

  /**
   * @return the jwts
   */
  public JWTConfiguration getJwts() {
    return jwts;
  }

  /**
   * @param jwts the jwts to set
   */
  public void setJwts(JWTConfiguration jwts) {
    this.jwts = jwts;
  }

  @Override
  @JsonIgnore
  public JWTConfiguration getJWTConfiguration() {
    return getJwts();
  }
}
