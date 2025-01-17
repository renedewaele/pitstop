package com.example.app.pitstop;

import com.example.app.pitstop.aaa.AaaOrder;
import com.example.app.pitstop.aaa.AaaRequest;
import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentId;
import com.example.app.pitstop.api.command.EscalateIncident;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.Association;
import io.fluxcapacitor.javaclient.tracking.handling.HandleEvent;
import io.fluxcapacitor.javaclient.tracking.handling.Stateful;
import io.fluxcapacitor.javaclient.web.HttpRequestMethod;
import lombok.Builder;
import lombok.Value;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Stateful
@Value
@Builder(toBuilder = true)
public class AaaWorkOrder {
    @Association
    IncidentId incidentId;
    @Association
    String aaaId;

    @HandleEvent
    static AaaWorkOrder handle(EscalateIncident event, Incident incident) {
        String aaaId = FluxCapacitor.sendCommandAndWait(new AaaRequest(
                HttpRequestMethod.POST,
                "/aaa/orders", new AaaOrder(incident.getDetails())));
        return AaaWorkOrder.builder().aaaId(aaaId).incidentId(event.getIncidentId()).build();
    }
}
