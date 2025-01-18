package com.example.app.pitstop.aaa;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentId;
import com.example.app.pitstop.api.command.CloseIncident;
import com.example.app.pitstop.api.command.EscalateIncident;
import com.example.app.pitstop.api.command.UpdateAssistance;
import io.fluxcapacitor.common.serialization.JsonUtils;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.Association;
import io.fluxcapacitor.javaclient.tracking.handling.HandleEvent;
import io.fluxcapacitor.javaclient.tracking.handling.HandleSchedule;
import io.fluxcapacitor.javaclient.tracking.handling.Stateful;
import io.fluxcapacitor.javaclient.web.HttpRequestMethod;
import lombok.Builder;
import lombok.Value;

import java.time.Duration;

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
                HttpRequestMethod.POST, "/aaa/orders", new AaaOrder(incident.getDetails())));
        FluxCapacitor.schedule(new PollAaa(aaaId), "aaa-" + event.getIncidentId(), Duration.ofMinutes(1));
        return AaaWorkOrder.builder().aaaId(aaaId).incidentId(event.getIncidentId()).build();
    }

    @HandleSchedule(allowedClasses = PollAaa.class)
    void pollAaa() {
        String response = FluxCapacitor.queryAndWait(
                new AaaRequest(HttpRequestMethod.GET, "/aaa/orders/" + aaaId, null));
        var assistance = JsonUtils.fromJson(response, AaaAssistance.class).toAssistance();
        FluxCapacitor.loadAggregate(incidentId).assertAndApply(new UpdateAssistance(incidentId, assistance));
    }

    @HandleEvent(allowedClasses = CloseIncident.class)
    AaaWorkOrder close() {
        FluxCapacitor.cancelSchedule("aaa-" + incidentId);
        return null;
    }


}
