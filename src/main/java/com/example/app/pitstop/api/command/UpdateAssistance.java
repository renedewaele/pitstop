package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.Assistance;
import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentId;
import io.fluxcapacitor.javaclient.common.Message;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class UpdateAssistance implements IncidentUpdate {
    IncidentId incidentId;
    @NotNull
    @Valid Assistance assistance;

    @Apply
    Incident apply(Incident incident) {
        return incident.toBuilder().assistance(assistance).build();
    }
}
