package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentId;
import com.example.app.pitstop.api.Offer;
import com.example.app.pitstop.api.OfferDetails;
import com.example.app.pitstop.api.OfferId;
import io.fluxcapacitor.javaclient.modeling.AssertLegal;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import io.fluxcapacitor.javaclient.tracking.handling.Request;
import io.fluxcapacitor.javaclient.tracking.handling.authentication.RequiresUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
@RequiresUser
public class OfferAssistance implements IncidentUpdate, Request<OfferId> {
    IncidentId incidentId;
    @NotNull OfferId offerId = OfferId.newValue();

    @AssertLegal @Valid @NotNull
    OfferDetails details;

    @Override
    public OfferId handle() {
        IncidentUpdate.super.handle();
        return offerId;
    }

    @AssertLegal
    void assertNewOffer(Offer offer) {
        throw new IllegalCommandException("Offer already exists");
    }

    @AssertLegal
    void assertNoAcceptedOffers(Incident incident) {
        if (incident.getAcceptedOffer().isPresent()) {
            throw new IllegalCommandException("Another offer has already been accepted.");
        }
    }

    @AssertLegal
    void assertNoOtherOffersFromOperator(Incident incident) {
        if (incident.getOffers().stream()
                .anyMatch(o -> o.getDetails().getOperatorId().equals(details.getOperatorId()))) {
            throw new IllegalCommandException("Another offer by this operator has already been made.");
        }
    }

    @AssertLegal
    void assertNotEscalated(Incident incident) {
        if (incident.isEscalated()) {
            throw new IllegalCommandException("Incident has already been escalated to the default operator.");
        }
    }

    @Apply
    Offer apply() {
        return Offer.builder().offerId(offerId).details(details).build();
    }

}
