package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentId;
import com.example.app.pitstop.api.Offer;
import com.example.app.pitstop.api.OfferId;
import com.example.app.user.authentication.RequiresRole;
import com.example.app.user.authentication.Role;
import io.fluxcapacitor.javaclient.modeling.AssertLegal;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
@RequiresRole(Role.user)
public class AcceptOffer implements IncidentUpdate, AssertReporter {
    IncidentId incidentId;
    @NotNull
    OfferId offerId;

    @AssertLegal
    void offerExists(@Nullable Offer offer) {
        if (offer == null) {
            throw new IllegalCommandException("Offer not found");
        }
    }

    @AssertLegal
    void assertNoAcceptedOffers(Incident incident) {
        if (incident.getAcceptedOffer().isPresent()) {
            throw new IllegalCommandException("Another offer has already been accepted.");
        }
    }

    @Apply
    Offer apply(Offer offer) {
        return offer.toBuilder().accepted(true).build();
    }
}
