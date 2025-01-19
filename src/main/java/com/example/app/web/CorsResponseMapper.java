package com.example.app.web;

import io.fluxcapacitor.common.api.Metadata;
import io.fluxcapacitor.javaclient.common.serialization.DeserializingMessage;
import io.fluxcapacitor.javaclient.web.DefaultWebResponseMapper;
import io.fluxcapacitor.javaclient.web.WebRequest;
import io.fluxcapacitor.javaclient.web.WebResponse;
import io.fluxcapacitor.javaclient.web.WebResponseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CorsResponseMapper implements WebResponseMapper {
    private final WebResponseMapper defaultMapper = new DefaultWebResponseMapper();

    @Override
    public WebResponse map(Object payload, Metadata metadata) {
        var result = defaultMapper.map(payload, metadata);
        if (payload instanceof Throwable) {
            Map<String, List<String>> headers = new LinkedHashMap<>(result.getHeaders());
            headers.remove("Content-Type");
            result = result.withPayload(Map.of("error", result.getPayload())).addMetadata("headers", headers);
        }
        return addAllowOriginHeader(result);
    }

    private static WebResponse addAllowOriginHeader(WebResponse result) {
        DeserializingMessage request = DeserializingMessage.getCurrent();
        if (request == null) {
            log.warn("WebRequest not found. May give issues for CORS headers.");
            return result;
        }
        var originHeaders =
                WebRequest.getHeaders(request.getMetadata()).getOrDefault("Origin", Collections.emptyList());
        if (!originHeaders.isEmpty()) {
            return result.toBuilder()
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept-Encoding, X-Impersonation")
                    .header("Access-Control-Allow-Origin", originHeaders.stream().findFirst()
                            .orElse("https://flux.host")).build();
        }
        return result;
    }

}
