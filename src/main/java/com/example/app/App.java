package com.example.app;

import com.example.app.pitstop.api.AppSettings;
import com.example.app.pitstop.api.query.GetAppSettings;
import io.fluxcapacitor.common.serialization.JsonUtils;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.configuration.spring.FluxCapacitorSpringConfig;
import io.fluxcapacitor.javaclient.web.HandleGet;
import io.fluxcapacitor.javaclient.web.HandleOptions;
import io.fluxcapacitor.javaclient.web.WebResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.time.Duration;

@SpringBootApplication
@Import(FluxCapacitorSpringConfig.class)
@Slf4j
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
		FluxCapacitor.sendCommandAndWait(JsonUtils.fromFile("/refdata/register-operators.json"));
		log.info("Application running");
	}

	@HandleGet("/api/settings")
	AppSettings getSettings() {
		return FluxCapacitor.queryAndWait(new GetAppSettings());
	}

	@HandleOptions("*")
	WebResponse corsPreflight() {
		return WebResponse.builder()
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, HEAD, TRACE")
				.header("Access-Control-Max-Age", String.valueOf(Duration.ofDays(1).toSeconds()))
				.build();
	}
}
