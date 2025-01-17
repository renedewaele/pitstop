package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentId;
import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.javaclient.common.Message;
import io.fluxcapacitor.javaclient.modeling.AssertLegal;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import lombok.Value;

@Value
public class EscalateIncident implements IncidentUpdate {
    IncidentId incidentId;

    @Apply
    Incident apply(Incident incident, Message message) {
        return incident.toBuilder().escalated(true).build();
    }
}
