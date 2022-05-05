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
package com.sigpwned.dropwizard.jose.jwt.tool.keygen;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import com.sigpwned.discourse.core.util.Discourse;

/**
 * Generates a KeyStore containing an RSA key suitable for use as a JWK. This implementation uses
 * BouncyCastle to ensure that certificate chains are generated as expected.
 */
public class KeygenTool {
  /**
   * NIST guidance until at least 2030
   * 
   * @see <a href=
   *      "https://en.wikipedia.org/wiki/Key_size">https://en.wikipedia.org/wiki/Key_size</a>
   */
  public static final int DEFAULT_KEY_WIDTH = 2048;

  public static final int KEY_WIDTH;
  static {
    int keyWidth = Optional.ofNullable(System.getenv("DEFAULT_JWK_KEY_WIDTH"))
        .map(Integer::parseInt).orElse(DEFAULT_KEY_WIDTH);
    if (keyWidth != 1024 && keyWidth != 2048 && keyWidth != 4096)
      throw new IllegalArgumentException("Invalid JWK key width: " + keyWidth);
    KEY_WIDTH = keyWidth;
  }

  /**
   * Recommendation for high-security applications like DNSSEC
   * 
   * @see <a href="https://en.wikipedia.org/wiki/SHA-2">https://en.wikipedia.org/wiki/SHA-2</a>
   */
  public static final int DEFAULT_HASH_LENGTH = 256;

  public static final int HASH_LENGTH;
  static {
    int hashLength = Optional.ofNullable(System.getenv("DEFAULT_JWK_HASH_LENGTH"))
        .map(Integer::parseInt).orElse(DEFAULT_HASH_LENGTH);
    if (hashLength != 256 && hashLength != 384 && hashLength != 512)
      throw new IllegalArgumentException("Invalid JWK hash length: " + hashLength);
    HASH_LENGTH = hashLength;
  }


  public static void main(String[] args) throws Exception {
    main(Discourse.configuration(KeygenToolConfiguration.class, args));
  }

  public static void main(KeygenToolConfiguration configuration) throws Exception {
    final Instant now = Instant.now();

    final String realm = configuration.realm;
    final String password = configuration.password;
    final int expirationMonths = configuration.expirationMonths;

    final BigInteger certificateSerialNumber = BigInteger.valueOf(now.toEpochMilli());
    final String keyAlias = now.atOffset(ZoneOffset.UTC).toLocalDate().toString();
    final int keyWidth = KEY_WIDTH;
    final int hashLength = HASH_LENGTH;

    final String signatureAlgorithm = "SHA" + hashLength + "WithRSA";

    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(keyWidth);
    KeyPair kp = kpg.generateKeyPair();
    RSAPublicKey pub = (RSAPublicKey) kp.getPublic();
    RSAPrivateKey priv = (RSAPrivateKey) kp.getPrivate();

    X509Certificate cert;
    try {
      X500Name dn = new X500Name("CN=" + URLEncoder.encode(realm, StandardCharsets.UTF_8));

      ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).build(priv);

      Instant startDate = now;
      Instant endDate = startDate.plus(30 * expirationMonths, ChronoUnit.DAYS);

      JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(dn,
          certificateSerialNumber, Date.from(startDate), Date.from(endDate), dn, pub);

      cert = new JcaX509CertificateConverter().getCertificate(certBuilder.build(contentSigner));
    } catch (CertificateException | OperatorCreationException e) {
      throw e;
    }

    KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
    store.load(null, password.toCharArray());

    store.setKeyEntry(keyAlias, priv, null, new Certificate[] {cert});

    store.store(System.out, password.toCharArray());
  }
}
