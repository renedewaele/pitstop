package com.example.app.pitstop.aaa;

import com.example.app.refdata.api.query.SendWebRequest;
import com.example.app.user.authentication.Sender;
import com.fasterxml.jackson.databind.JsonNode;
import io.fluxcapacitor.javaclient.configuration.ApplicationProperties;
import io.fluxcapacitor.javaclient.tracking.handling.Request;
import io.fluxcapacitor.javaclient.web.HttpRequestMethod;
import io.fluxcapacitor.javaclient.web.WebRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
public class AaaRequest extends SendWebRequest implements Request<String> {
    @NotNull HttpRequestMethod method;
    @NotBlank String resourcePath;
    Object body;

    @Override
    protected WebRequest.Builder buildRequest(WebRequest.Builder requestBuilder, Sender sender) {
        return requestBuilder
                .url(ApplicationProperties.requireProperty("aaa.domain") + resourcePath)
                .method(method)
                .payload(body)
                .header("Authorization", "Token " + ApplicationProperties.requireProperty("aaa.token"));
    }
}
