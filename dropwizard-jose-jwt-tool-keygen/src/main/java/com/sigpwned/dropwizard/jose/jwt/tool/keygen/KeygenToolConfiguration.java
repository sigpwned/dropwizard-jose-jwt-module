/*-
 * =================================LICENSE_START==================================
 * dropwizard-jwt-tool-keygen
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
package com.sigpwned.dropwizard.jose.jwt.tool.keygen;

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.OptionParameter;

@Configurable
public class KeygenToolConfiguration {
  @OptionParameter(shortName = "r", longName = "realm", required = true)
  public String realm;

  @OptionParameter(shortName = "p", longName = "password", required = true)
  public String password;

  @OptionParameter(shortName = "e", longName = "expirationMonths", required = false)
  public int expirationMonths = 12;
}
