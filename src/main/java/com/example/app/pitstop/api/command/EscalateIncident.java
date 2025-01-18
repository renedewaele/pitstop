package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentId;
import io.fluxcapacitor.javaclient.modeling.AssertLegal;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import lombok.Value;

@Value
public class EscalateIncident implements IncidentUpdate, AssertReporter {
    IncidentId incidentId;

    @AssertLegal
    void assertNoAcceptedOffers(Incident incident) {
        if (incident.getAcceptedOffer().isPresent()) {
            throw new IllegalCommandException("An offer has already been accepted.");
        }
    }

    @AssertLegal
    void assertNotEscalated(Incident incident) {
        if (incident.isEscalated()) {
            throw new IllegalCommandException("Incident has already been escalated.");
        }
    }

    @Apply
    Incident apply(Incident incident) {
        return incident.toBuilder().escalated(true).build();
    }
}
