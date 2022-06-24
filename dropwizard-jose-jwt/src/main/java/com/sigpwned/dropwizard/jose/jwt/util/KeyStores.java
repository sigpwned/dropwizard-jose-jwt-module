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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.Base64;
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
    if (!path.isFile())
      throw new FileNotFoundException(path.getPath());
    return loadKeyStore(type, ByteSource.fromFile(path), password, provider);
  }


  /**
   * Attempts to load a key store using the given type and provider. If the given provider fails,
   * then other providers will be attempted. The path is interpreted in the following way:
   * 
   * (1) If the value contains "://", then it is treated as a URL, and the data is loaded from the
   * contents of the URL.
   * 
   * (2) If the value is the path of a local file, then the data is loaded from the contents of that
   * file.
   * 
   * (3) If the value is the path of a local resource relative to the current thread's
   * {@link ClassLoader}, then the data is loaded from the contents of that resource.
   * 
   * (4) If the value is base64-encoded, then it is treated as the data itself.
   * 
   * Otherwise, an {@link IOException} is thrown.
   * 
   * @throws IOException if an I/O error occurs, or if the given path cannot be resolved to a key
   *         store
   */
  public static KeyStore loadKeyStore(String type, String keyStorePath, String password,
      String provider) throws IOException {
    ByteSource keyStoreBytes;
    if (keyStorePath.contains("://")) {
      keyStoreBytes = ByteSource.fromUrl(new URL(keyStorePath));
    } else if (new File(keyStorePath).isFile()) {
      keyStoreBytes = ByteSource.fromFile(new File(keyStorePath));
    } else if (Thread.currentThread().getContextClassLoader().getResource(keyStorePath) != null) {
      keyStoreBytes = ByteSource
          .fromUrl(Thread.currentThread().getContextClassLoader().getResource(keyStorePath));
    } else {
      try {
        keyStoreBytes = ByteSource.fromBytes(Base64.getDecoder().decode(keyStorePath));
      } catch (IllegalArgumentException e) {
        // This means the input was not a URL, file, resource, or base64 literal.
        throw new IOException("Failed to load data from given keyStorePath");
      }
    }
    return loadKeyStore(type, keyStoreBytes, password, provider);
  }

  /**
   * Attempts to load a key store using the given type and provider. If the given provider fails,
   * then other providers will be attempted.
   */
  public static KeyStore loadKeyStore(String type, ByteSource bytes, String password,
      String provider) throws IOException {
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
      try (InputStream inputStream = bytes.getBytes()) {
        result.load(inputStream, password.toCharArray());
      }
    } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
      throw new IOException("Failed to load key store", e);
    }

    return result;
  }
}
