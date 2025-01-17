package com.example.app.pitstop;

import com.example.app.pitstop.api.command.CloseIncident;
import com.example.app.pitstop.api.command.ReportIncident;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.HandleEvent;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class IncidentLifecycleHandler {
    public static final Duration INCIDENT_DEADLINE = Duration.ofHours(24);

    @HandleEvent
    void handle(ReportIncident update) {
        FluxCapacitor.scheduleCommand(
                new CloseIncident(update.getIncidentId()), "deadline-%s".formatted(update.getIncidentId()), INCIDENT_DEADLINE);
    }

    @HandleEvent
    void handle(CloseIncident update) {
        FluxCapacitor.cancelSchedule("deadline-%s".formatted(update.getIncidentId()));
    }
}
