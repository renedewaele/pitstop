package com.example.app.pitstop.api;

import com.example.app.user.api.UserId;
import io.fluxcapacitor.common.search.Facet;
import io.fluxcapacitor.javaclient.modeling.Aggregate;
import io.fluxcapacitor.javaclient.modeling.EntityId;
import io.fluxcapacitor.javaclient.modeling.EventPublication;
import io.fluxcapacitor.javaclient.modeling.Member;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Aggregate(searchable = true, collection = "incidents", timestampPath = "start", endPath = "end",
        eventPublication = EventPublication.IF_MODIFIED)
@Builder(toBuilder = true)
@Value
public class Incident {
    @EntityId
    IncidentId incidentId;
    IncidentDetails details;

    @Facet
    UserId reporter;

    Instant start, end;

    @Member
    @Singular
    List<Offer> offers;

    @Facet
    Assistance assistance;

    boolean escalated;

    public Optional<Offer> getAcceptedOffer() {
        return offers.stream().filter(Offer::isAccepted).findFirst();
    }
}
