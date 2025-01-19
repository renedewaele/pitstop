package com.example.app.pitstop.api;

import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.javaclient.common.serialization.FilterContent;
import io.fluxcapacitor.javaclient.modeling.EntityId;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Offer {
    @EntityId OfferId offerId;
    OfferDetails details;

    boolean accepted;

    @FilterContent
    Offer filter(Sender sender, Incident incident) {
        return sender.isAuthorizedFor(incident.getReporter()) || sender.isAuthorizedFor(details.getOperatorId())
                ? this : null;
    }
}
