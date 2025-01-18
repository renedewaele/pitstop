package com.example.app.refdata.mapbox;

import com.example.app.refdata.api.query.GetLocationName;
import io.fluxcapacitor.common.serialization.JsonUtils;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.HandleQuery;
import io.fluxcapacitor.javaclient.tracking.handling.LocalHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@LocalHandler
public class MapboxHandler {

    @HandleQuery
    String handle(GetLocationName query) {
        String result = FluxCapacitor.queryAndWait(new GetMapboxData("/search/geocode/v6/reverse", Map.of(
                "longitude", query.getLongitude().toPlainString(),
                "latitude", query.getLatitude().toPlainString())));
        var json = JsonUtils.readTree(result);
        return json.withArray("features").path(0).path("properties").path("name").textValue();
    }

}
