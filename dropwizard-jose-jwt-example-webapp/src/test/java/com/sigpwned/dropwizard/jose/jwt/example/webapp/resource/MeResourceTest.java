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
package com.sigpwned.dropwizard.jose.jwt.example.webapp.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.Test;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.Account;
import jakarta.ws.rs.core.SecurityContext;

public class MeResourceTest {
  @Test
  public void shouldReturnSecurityPrincipal() {
    final Account account = Account.of("id", "username", "name");

    final SecurityContext security = mock(SecurityContext.class);
    when(security.getUserPrincipal()).thenReturn(account);

    MeResource unit = new MeResource();
    unit.context = security;

    Account me = unit.getMe();

    assertThat(me, is(account));
  }
}
