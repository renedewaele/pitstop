package com.example.app.pitstop.api.query;

import com.example.app.pitstop.IncidentLifecycleHandler;
import com.example.app.pitstop.api.AppSettings;
import io.fluxcapacitor.javaclient.tracking.handling.HandleQuery;
import io.fluxcapacitor.javaclient.tracking.handling.Request;
import lombok.Value;

@Value
public class GetAppSettings implements Request<AppSettings> {

    @HandleQuery
    AppSettings handle() {
        return AppSettings.builder().escalationDelay(IncidentLifecycleHandler.ESCALATE_DEADLINE).build();
    }
}
