/*-
 * =================================LICENSE_START==================================
 * dropwizard-jose-jwt-example-webapp
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
