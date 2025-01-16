package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentDetails;
import com.example.app.pitstop.api.IncidentId;
import com.example.app.user.authentication.RequiresRole;
import com.example.app.user.authentication.Role;
import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.javaclient.common.Message;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import io.fluxcapacitor.javaclient.tracking.handling.authentication.RequiresUser;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@RequiresUser
@Value
public class ReportIncident implements IncidentUpdate {
    IncidentId incidentId;
    @NotNull @Valid
    IncidentDetails details;

    @Override
    public void assertExistence(@Nullable Incident incident) {
        if (incident != null) {
            throw new IllegalCommandException("Assistance already exists");
        }
    }

    @Apply
    Incident create(Sender sender, Message message) {
        return Incident.builder().incidentId(incidentId)
                .details(details).reporter(sender.getUserId())
                .start(message.getTimestamp()).build();
    }

}
