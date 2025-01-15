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
public class CloseIncident implements IncidentUpdate {
    IncidentId incidentId;

    @AssertLegal
    void assertAssignedOperator(Incident incident, Sender sender) {
        if (!sender.isAuthorizedFor(incident.getReporter())) {
            incident.getAcceptedOffer().filter(offer -> sender.isAuthorizedFor(offer.getDetails().getOperatorId()))
                    .orElseThrow(() -> new IllegalCommandException("Sender is not authorized to close incident."));
        }
    }

    @Apply
    Incident apply(Incident incident, Message message) {
        return incident.toBuilder().end(message.getTimestamp()).build();
    }
}
