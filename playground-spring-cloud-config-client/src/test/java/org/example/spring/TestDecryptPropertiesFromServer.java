/*
 * Created on May 27, 2018
 * @author vlads
 */
package org.example.spring;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 * This tests needs ConfigServerApplication running from project
 * playground-spring-cloud-config-server
 *
 */
public class TestDecryptPropertiesFromServer {

    private ConfigurableApplicationContext context;

	private ConfigurableEnvironment setupEnvironment(String appName,
			String springProfilesActive) {
        List<String> springArgs = new ArrayList<>();
		springArgs.add("--spring.cloud.config.name=" + appName);
        springArgs.add("--spring.profiles.active=" + springProfilesActive);
		springArgs.add("--spring.cloud.config.uri=" + "http://localhost:7777");

		context = new SpringApplicationBuilder(TestDecryptPropertiesFromServer.class)
				.bannerMode(Banner.Mode.OFF)
				.web(WebApplicationType.NONE)
                .run(springArgs.toArray(new String[0]));

        return context.getEnvironment();
    }

	@Test
	public void testDecryptedSetup() throws IOException {

		TextEncryptor encryptor = mock(TextEncryptor.class);
		when(encryptor.decrypt("bar7-p1-encrypted")).thenReturn("bar7-p1-clear-ok");

		ConfigEncryptionDelegate.setTestConfigEncryptor(encryptor);

		ConfigurableEnvironment environment = setupEnvironment("encrypt-override", "p1");

		// Verify setup
		assertEquals("foo7-p1", environment.getProperty("foo7"));

		assertEquals("bar7-p1-clear-ok", environment.getProperty("bar7"));

		verify(encryptor).decrypt("bar7-p1-encrypted");
		verifyNoMoreInteractions(encryptor);
	}

	@Test
	public void testOverrideDecrypted() throws IOException {

		TextEncryptor encryptor = mock(TextEncryptor.class);
		when(encryptor.decrypt("bar7-p1-encrypted")).thenReturn("bar7-p1-clear-ok");
		when(encryptor.decrypt("bar7-p2-encrypted")).thenReturn("bar7-p2-clear-ok");

		ConfigEncryptionDelegate.setTestConfigEncryptor(encryptor);

		ConfigurableEnvironment environment = setupEnvironment("encrypt-override",
				"p1,p2");

		// Verify setup
		assertEquals("foo7-p2", environment.getProperty("foo7"));

		// TODO Broken in Greenwich.RELEASE takes value from p1
		assertEquals("bar7-p2-clear-ok", environment.getProperty("bar7"));

		verify(encryptor).decrypt("bar7-p2-encrypted");
		verifyNoMoreInteractions(encryptor);
	}

	// TODO Broken in Greenwich.RELEASE
	@Test
	public void testOnlyDecryptIfNotOverriddenWithProfiles() throws IOException {

		TextEncryptor encryptor = mock(TextEncryptor.class);
		// use case: This may be a profile from different region...
		when(encryptor.decrypt("bar7-p1-encrypted"))
				.thenThrow(new Error("No Access to decryption Key"));

		when(encryptor.decrypt("bar7-p2-encrypted")).thenReturn("bar7-p2-clear-ok");

		ConfigEncryptionDelegate.setTestConfigEncryptor(encryptor);

		ConfigurableEnvironment environment = setupEnvironment("encrypt-override",
				"p1,p2");

		// Verify setup
		assertEquals("foo7-p2", environment.getProperty("foo7"));

		assertEquals("bar7-p2-clear-ok", environment.getProperty("bar7"));

		verify(encryptor).decrypt("bar7-p2-encrypted");
		verifyNoMoreInteractions(encryptor);
	}

    @After
    public void tearDown() {
        if (context != null) {
            context.close();
            context = null;
        }
		ConfigEncryptionDelegate.setTestConfigEncryptor(null);
    }

}
