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
package com.sigpwned.dropwizard.jose.jwt.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.security.Key;
import java.security.KeyStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.sigpwned.dropwizard.jose.jwt.tool.keygen.KeygenTool;
import com.sigpwned.dropwizard.jose.jwt.tool.keygen.KeygenToolConfiguration;

public class KeyStoresTest {
  public File keyStoreFile;
  public KeyStore keyStore;

  public static final String PASSWORD = "password";

  public static final String KEY_ALIAS = "alias";

  public static final String KEY_STORE_TYPE = "alias";

  @Before
  public void setupJWTFactoryTest() throws Exception {
    keyStoreFile = File.createTempFile("keystore.", ".p12");

    KeygenToolConfiguration ktc = new KeygenToolConfiguration();
    ktc.realm = "realm";
    ktc.password = PASSWORD;
    ktc.out = new PrintStream(new FileOutputStream(keyStoreFile));
    ktc.keyAlias = KEY_ALIAS;

    KeygenTool.main(ktc);
  }

  @After
  public void cleanupJWTFactoryTest() {
    keyStoreFile.delete();
  }

  @Test
  public void shouldReadKeyStoreProperly() throws Exception {
    KeyStore keyStore = KeyStores.loadKeyStore(KeygenTool.KEY_STORE_TYPE, keyStoreFile, PASSWORD);

    Key key = keyStore.getKey(KEY_ALIAS, null);

    assertThat(key.getAlgorithm(), is(KeygenTool.KEY_ALGORITHM));
  }
}
