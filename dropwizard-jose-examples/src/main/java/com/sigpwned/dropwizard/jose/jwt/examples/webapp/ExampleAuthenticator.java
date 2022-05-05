package com.sigpwned.dropwizard.jose.jwt.examples.webapp;

import java.text.ParseException;
import java.util.Optional;
import com.nimbusds.jwt.JWTClaimsSet;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public class ExampleAuthenticator implements Authenticator<JWTClaimsSet, Account> {
  @Override
  public Optional<Account> authenticate(JWTClaimsSet credentials) throws AuthenticationException {
    try {
      String id = credentials.getStringClaim("accountId");
      String username = credentials.getStringClaim("accountUsername");
      String name = credentials.getStringClaim("accountName");
      return Optional.of(Account.of(id, username, name));
    } catch (ParseException e) {
      // Well, that is exceptionally odd.
      return Optional.empty();
    }
  }
}
