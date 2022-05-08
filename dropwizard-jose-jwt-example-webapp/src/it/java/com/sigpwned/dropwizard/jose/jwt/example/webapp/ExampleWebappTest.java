package com.sigpwned.dropwizard.jose.jwt.example.webapp;

import org.junit.ClassRule;
import org.junit.Test;
import io.dropwizard.testing.junit.DropwizardAppRule;

public class ExampleWebappTest {
  @ClassRule
  @SuppressWarnings("deprecation")
  public static final DropwizardAppRule<ExampleConfiguration> RULE =
      new DropwizardAppRule<>(ExampleWebapp.class, "config.yml");

  @Test
  public void smokeTest() {}
  
  @Test
  public void foo() {
    
  }
}
