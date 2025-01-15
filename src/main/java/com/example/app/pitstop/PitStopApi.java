package com.example.app.pitstop;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentDetails;
import com.example.app.pitstop.api.IncidentId;
import com.example.app.pitstop.api.OfferDetails;
import com.example.app.pitstop.api.OfferId;
import com.example.app.pitstop.api.command.AcceptOffer;
import com.example.app.pitstop.api.command.CloseIncident;
import com.example.app.pitstop.api.command.OfferAssistance;
import com.example.app.pitstop.api.command.ReportIncident;
import com.example.app.pitstop.api.query.GetIncidents;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.web.HandleGet;
import io.fluxcapacitor.javaclient.web.HandlePost;
import io.fluxcapacitor.javaclient.web.Path;
import io.fluxcapacitor.javaclient.web.PathParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Path("/api")
public class PitStopApi {

    @HandlePost("incidents")
    IncidentId reportIncident(IncidentDetails details) {
        var incidentId = IncidentId.newValue();
        FluxCapacitor.sendCommandAndWait(new ReportIncident(incidentId, details));
        return incidentId;
    }

    @HandleGet("incidents")
    List<Incident> getIncidents() {
        return FluxCapacitor.queryAndWait(new GetIncidents());
    }

    @HandlePost("incidents/{incidentId}/offers")
    OfferId offerAssistance(@PathParam IncidentId incidentId, OfferDetails details) {
        var command = new OfferAssistance(incidentId, details);
        FluxCapacitor.sendCommandAndWait(command);
        return command.getOfferId();
    }

    @HandlePost("incidents/{incidentId}/offers/{offerId}/accept")
    void acceptOffer(@PathParam IncidentId incidentId, @PathParam OfferId offerId) {
        FluxCapacitor.sendCommandAndWait(new AcceptOffer(incidentId, offerId));
    }

    @HandlePost("incidents/{incidentId}/close")
    void closeIncident(@PathParam IncidentId incidentId) {
        FluxCapacitor.sendCommandAndWait(new CloseIncident(incidentId));
    }

}
