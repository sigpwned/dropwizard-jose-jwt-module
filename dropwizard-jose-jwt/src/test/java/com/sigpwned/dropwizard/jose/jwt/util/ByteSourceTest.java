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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import com.google.common.io.ByteStreams;

public class ByteSourceTest {
  public static final String MESSAGE = "Hello, world!";

  @Test
  public void fromUrlTest() throws IOException {
    String message;

    File tmp = File.createTempFile("message.", ".txt");
    try {
      try (OutputStream out = new FileOutputStream(tmp)) {
        out.write(MESSAGE.getBytes(StandardCharsets.UTF_8));
      }

      try (InputStream in = ByteSource.fromUrl(tmp.toURI().toURL()).getBytes()) {
        message = new String(ByteStreams.toByteArray(in), StandardCharsets.UTF_8);
      }


    } finally {
      tmp.delete();
    }

    assertThat(message, is(MESSAGE));
  }

  @Test
  public void fromFileTest() throws IOException {
    String message;

    File tmp = File.createTempFile("message.", ".txt");
    try {
      try (OutputStream out = new FileOutputStream(tmp)) {
        out.write(MESSAGE.getBytes(StandardCharsets.UTF_8));
      }

      try (InputStream in = ByteSource.fromFile(tmp).getBytes()) {
        message = new String(ByteStreams.toByteArray(in), StandardCharsets.UTF_8);
      }


    } finally {
      tmp.delete();
    }

    assertThat(message, is(MESSAGE));
  }

  @Test
  public void fromResourceTest() throws IOException {
    String message;

    try (InputStream in = ByteSource
        .fromUrl(Thread.currentThread().getContextClassLoader().getResource("ByteSourceTest.txt"))
        .getBytes()) {
      message = new String(ByteStreams.toByteArray(in), StandardCharsets.UTF_8);
    }

    assertThat(message, is(MESSAGE));
  }

  @Test
  public void fromByteArrayTest() throws IOException {
    String message;

    try (InputStream in =
        ByteSource.fromBytes(MESSAGE.getBytes(StandardCharsets.UTF_8)).getBytes()) {
      message = new String(ByteStreams.toByteArray(in), StandardCharsets.UTF_8);
    }

    assertThat(message, is(MESSAGE));
  }
}
