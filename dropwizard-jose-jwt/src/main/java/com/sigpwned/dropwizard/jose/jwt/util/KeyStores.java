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
package com.sigpwned.dropwizard.jose.jwt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KeyStores {
  private KeyStores() {}

  private static final Logger LOGGER = LoggerFactory.getLogger(KeyStores.class);

  /**
   * We use PKCS12 because it is a standards-compliant (as opposed to Java-specific) keystore
   * format.
   */
  public static final String DEFAULT_TYPE = "PKCS12";

  /**
   * Same as {@code loadKeyStore(type, path, password)}.
   * 
   * @throws IOException
   * 
   * @see #loadKeyStore(String, File, String, String)
   */
  public static KeyStore loadKeyStore(File path, String password) throws IOException {
    return loadKeyStore(DEFAULT_TYPE, path, password);
  }

  /**
   * Same as {@code loadKeyStore(type, path, password)}.
   * 
   * @throws IOException
   * 
   * @see #loadKeyStore(String, File, String, String)
   */
  public static KeyStore loadKeyStore(String type, File path, String password) throws IOException {
    return loadKeyStore(type, path, password, null);
  }

  /**
   * Attempts to load a key store using the given type and provider. If the given provider fails,
   * then other providers will be attempted.
   */
  public static KeyStore loadKeyStore(String type, File path, String password, String provider)
      throws IOException {
    KeyStore result;

    try {
      if (provider == null) {
        result = KeyStore.getInstance(type);
      } else {
        try {
          result = KeyStore.getInstance(type, provider);
        } catch (KeyStoreException | NoSuchProviderException e) {
          if (LOGGER.isWarnEnabled())
            LOGGER.warn("Failed to load keystore with provider {}. Attempting other providers...",
                provider, e);
          result = KeyStore.getInstance(type);
        }
      }
      try (InputStream inputStream = new FileInputStream(path)) {
        result.load(inputStream, password.toCharArray());
      }
    } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
      throw new IOException("Failed to load key store", e);
    }

    return result;
  }
}
