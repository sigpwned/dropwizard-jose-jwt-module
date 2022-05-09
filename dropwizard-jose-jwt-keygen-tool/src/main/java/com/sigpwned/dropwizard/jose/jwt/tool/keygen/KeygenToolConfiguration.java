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

import java.io.PrintStream;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import com.sigpwned.discourse.core.StandardConfigurationBase;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.OptionParameter;

@Configurable
public class KeygenToolConfiguration extends StandardConfigurationBase {
  /**
   * Currently, the default expiration period is 12 months. This may change in the future.
   */
  public static final int DEFAULT_EXPIRATION_MONTHS = 12;

  @NotEmpty
  @OptionParameter(shortName = "r", longName = "realm", required = true, description = "The authentication realm, which is typically the webapp domain. This is used to set the common name (CN) claim of the public key certificate.")
  public String realm;

  @NotEmpty
  @OptionParameter(shortName = "p", longName = "password", required = true, description = "The password used to encrypt the keystore.")
  public String password;

  @Min(1)
  @OptionParameter(shortName = "e", longName = "expirationMonths", required = false, description = "The expiration period of the generated keys in months. The default period is 12 months, or 1 year.")
  public int expirationMonths = DEFAULT_EXPIRATION_MONTHS;

  /**
   * Not configurable. For testing only.
   */
  public String keyAlias;

  // Not configurable
  public PrintStream out = System.out;
}
