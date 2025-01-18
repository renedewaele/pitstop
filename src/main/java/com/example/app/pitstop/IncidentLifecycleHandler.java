package com.example.app.pitstop;

import com.example.app.pitstop.api.command.AcceptOffer;
import com.example.app.pitstop.api.command.CloseIncident;
import com.example.app.pitstop.api.command.EscalateIncident;
import com.example.app.pitstop.api.command.IncidentUpdate;
import com.example.app.pitstop.api.command.ReportIncident;
import io.fluxcapacitor.javaclient.tracking.handling.HandleEvent;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static io.fluxcapacitor.javaclient.FluxCapacitor.cancelSchedule;
import static io.fluxcapacitor.javaclient.FluxCapacitor.scheduleCommand;

@Component
public class IncidentLifecycleHandler {
    public static final Duration ESCALATE_DEADLINE = Duration.ofMinutes(15), AUTO_CLOSE_DEADLINE = Duration.ofHours(24);

    @HandleEvent
    void handle(ReportIncident update) {
        scheduleCommand(new EscalateIncident(update.getIncidentId()), "escalate-" + update.getIncidentId(),
                        ESCALATE_DEADLINE);
        scheduleCommand(new CloseIncident(update.getIncidentId()), "deadline-" + update.getIncidentId(),
                        AUTO_CLOSE_DEADLINE);
    }

    @HandleEvent(allowedClasses = {AcceptOffer.class, EscalateIncident.class})
    void handle(IncidentUpdate update) {
        cancelSchedule("escalate-" + update.getIncidentId());
    }

    @HandleEvent
    void handle(CloseIncident update) {
        cancelSchedule("escalate-" + update.getIncidentId());
        cancelSchedule("deadline-" + update.getIncidentId());
    }
}
