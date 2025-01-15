package com.example.app.pitstop.api;

import io.fluxcapacitor.javaclient.modeling.EntityId;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Offer {
    @EntityId OfferId offerId;
    OfferDetails details;

    boolean accepted;
}
