package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentId;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.modeling.AssertLegal;
import io.fluxcapacitor.javaclient.publishing.routing.RoutingKey;
import io.fluxcapacitor.javaclient.tracking.Consumer;
import io.fluxcapacitor.javaclient.tracking.TrackSelf;
import io.fluxcapacitor.javaclient.tracking.handling.HandleCommand;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

@TrackSelf
@Consumer(name = "incident-core")
public interface IncidentUpdate {
    @RoutingKey
    @NotNull
    IncidentId getIncidentId();

    @HandleCommand
    default void handle() {
        FluxCapacitor.loadAggregate(getIncidentId()).assertAndApply(this);
    }

    @AssertLegal
    default void assertExistence(@Nullable Incident incident) {
        if (incident == null) {
            throw new IllegalCommandException("Incident not found");
        }
    }

    @AssertLegal
    default void assertOngoing(Incident incident) {
        if (incident.getEnd() != null) {
            throw new IllegalCommandException("Incident has been closed");
        }
    }
}
