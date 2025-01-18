package com.example.app.refdata.mapbox;

import com.example.app.refdata.api.query.SendWebRequest;
import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.javaclient.configuration.ApplicationProperties;
import io.fluxcapacitor.javaclient.tracking.handling.Request;
import io.fluxcapacitor.javaclient.web.HttpRequestMethod;
import io.fluxcapacitor.javaclient.web.WebRequest;
import lombok.Value;

import java.util.Map;

@Value
public class GetMapboxData extends SendWebRequest implements Request<String> {
    String resourcePath;
    Map<String, String> queryParameters;

    @Override
    protected WebRequest.Builder buildRequest(WebRequest.Builder requestBuilder, Sender sender) {
        return requestBuilder
                .url(ApplicationProperties.requireProperty("mapbox.domain") + resourcePath + queryString())
                .method(HttpRequestMethod.GET);
    }

    String queryString() {
        var sb = new StringBuilder();
        sb.append("?access_token=").append(ApplicationProperties.requireProperty("mapbox.token"));
        queryParameters.forEach((name, value) -> sb.append("&").append(name).append("=").append(value));
        return sb.toString();
    }
}
