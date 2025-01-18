package com.example.app.refdata.rdw;

import com.example.app.refdata.api.query.SendWebRequest;
import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.javaclient.configuration.ApplicationProperties;
import io.fluxcapacitor.javaclient.tracking.handling.Request;
import io.fluxcapacitor.javaclient.web.HttpRequestMethod;
import io.fluxcapacitor.javaclient.web.WebRequest;
import lombok.Value;

@Value
public class GetRdwData extends SendWebRequest implements Request<String> {
    String resourcePath;

    @Override
    protected WebRequest.Builder buildRequest(WebRequest.Builder requestBuilder, Sender sender) {
        return requestBuilder
                .url(ApplicationProperties.requireProperty("rdw.domain") + resourcePath)
                .method(HttpRequestMethod.GET)
                .header("X-App-Token", ApplicationProperties.requireProperty("rdw.token"));
    }
}
