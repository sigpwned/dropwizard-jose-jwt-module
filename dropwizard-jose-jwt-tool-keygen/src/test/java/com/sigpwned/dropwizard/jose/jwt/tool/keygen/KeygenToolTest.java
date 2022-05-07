package com.sigpwned.dropwizard.jose.jwt.tool.keygen;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.Key;
import java.security.KeyStore;
import org.junit.Test;

public class KeygenToolTest {
  public static final String PASSWORD = "password";

  public static final String KEY_ALIAS = "alias";

  public static final String KEY_STORE_TYPE = "alias";

  @Test
  public void shouldWriteKeyStoreProperly() throws Exception {
    File keyStoreFile = File.createTempFile("keystore.", ".p12");
    try {
      KeygenToolConfiguration ktc = new KeygenToolConfiguration();
      ktc.realm = "realm";
      ktc.password = PASSWORD;
      ktc.out = new PrintStream(new FileOutputStream(keyStoreFile));
      ktc.keyAlias = KEY_ALIAS;

      KeygenTool.main(ktc);

      KeyStore keyStore = KeyStore.getInstance(KeygenTool.KEY_STORE_TYPE);

      try (InputStream inputStream = new FileInputStream(keyStoreFile)) {
        keyStore.load(inputStream, PASSWORD.toCharArray());
      }

      Key key = keyStore.getKey(KEY_ALIAS, null);

      assertThat(key.getAlgorithm(), is(KeygenTool.KEY_ALGORITHM));
    } finally {
      keyStoreFile.delete();
    }
  }
}
