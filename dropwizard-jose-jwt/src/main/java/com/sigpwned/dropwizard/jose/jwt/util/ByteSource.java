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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * A re-readable stream of bytes
 */
@FunctionalInterface
public interface ByteSource {
  /**
   * Creates a new {@code ByteSource} from the given {@link URL}.
   */
  public static ByteSource fromUrl(URL url) {
    return () -> url.openStream();
  }

  /**
   * Creates a new {@code ByteSource} from the given {@link File}.
   */
  public static ByteSource fromFile(File file) {
    return () -> new FileInputStream(file);
  }

  /**
   * Creates a new {@code ByteSource} from the given byte array.
   */
  public static ByteSource fromBytes(byte[] buf) {
    return () -> new ByteArrayInputStream(buf);
  }

  /**
   * Creates a new {@link InputStream} of the underlying bytes
   */
  public InputStream getBytes() throws IOException;
}
