package com.example.app;

import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.test.TestFixture;
import io.fluxcapacitor.javaclient.test.spring.FluxCapacitorTestConfig;
import io.fluxcapacitor.javaclient.web.HttpRequestMethod;
import io.fluxcapacitor.javaclient.web.WebRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {App.class, FluxCapacitorTestConfig.class})
class AppContextTest {
	@Autowired TestFixture testFixture;

	@Autowired
	FluxCapacitor fluxCapacitor;

	@Test
	void getApiSettings() {
		testFixture.whenGet("/api/settings").expectSuccessfulResult();
	}

	@Test
	void testCorsPreflight() {
		testFixture.whenWebRequest(WebRequest.builder().method(HttpRequestMethod.OPTIONS)
										   .url("/api/incidents").build())
				.expectSuccessfulResult()
				.andThen()
				.whenWebRequest(WebRequest.builder().method(HttpRequestMethod.OPTIONS)
										.url("/api/incidents/whatever").build())
				.expectSuccessfulResult();
	}

}
