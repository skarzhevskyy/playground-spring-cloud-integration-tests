package org.example.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 * Allow to configure TextEncryptor per test that is required to be initialized in
 * Bootstrap.
 * 
 * While the future of proper Bootstrap API is discussed here
 * https://github.com/spring-projects/spring-boot/issues/15704
 *
 */
@Configuration
public class ConfigEncryptionDelegate {

	private static TextEncryptor testConfigEncryptor;

	public static void setTestConfigEncryptor(TextEncryptor testConfigEncryptor) {
		ConfigEncryptionDelegate.testConfigEncryptor = testConfigEncryptor;
	}

	@Bean
	TextEncryptor testsTextEncryptor() {
		assert testConfigEncryptor != null : "test forgot to setup TextEncryptor";
		return testConfigEncryptor;
	}
}
