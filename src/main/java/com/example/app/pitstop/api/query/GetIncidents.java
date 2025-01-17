package com.example.app.pitstop.api.query;

import com.example.app.pitstop.api.Incident;
import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.persisting.search.Search;
import io.fluxcapacitor.javaclient.tracking.handling.HandleQuery;
import io.fluxcapacitor.javaclient.tracking.handling.Request;
import io.fluxcapacitor.javaclient.tracking.handling.authentication.RequiresUser;
import lombok.Value;

import java.util.List;

@Value
@RequiresUser
public class GetIncidents implements Request<List<Incident>> {

    @HandleQuery
    List<Incident> handle(Sender sender) {
        Search search = FluxCapacitor.search(Incident.class);
        if (!sender.isAdmin() && sender.getOperator() == null) {
            search.matchFacet("reporter", sender.getUserId());
        }
        return search.fetchAll();
    }
}
