package com.sigpwned.dropwizard.jose.jwt.example.webapp;

import java.io.IOException;
import java.util.Optional;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.Account;

/**
 * Represents a proper authentication backend
 */
public interface AccountStore {
  /**
   * Given a set of login credentials, return the corresponding account if the credentials are valid
   * or nothing otherwise.
   * 
   * @throws IOException if there is an error in communicating with the authentication backend. Note
   *         that this is different from invalid credentials.
   */
  public Optional<Account> authenticate(String username, String password) throws IOException;
}
