package com.example.app.pitstop;

import com.example.app.pitstop.aaa.AaaWorkOrder;
import com.example.app.pitstop.api.command.EscalateIncident;
import com.example.app.pitstop.api.command.UpdateAssistance;
import io.fluxcapacitor.javaclient.test.TestFixture;
import io.fluxcapacitor.javaclient.web.HandleGet;
import io.fluxcapacitor.javaclient.web.HandlePost;
import io.fluxcapacitor.javaclient.web.PathParam;
import org.junit.jupiter.api.Test;

import java.time.Duration;

class AaaWorkOrderTest {

    final TestFixture testFixture = TestFixture.create(
                    PitStopApi.class, IncidentLifecycleHandler.class,
                    AaaMock.class, AaaWorkOrder.class)
            .withProperty("aaa.token", "789798")
            .withProperty("aaa.domain", "");

    @Test
    void reportViaApi() {
        testFixture
                .givenPost("/api/incidents", "/pitstop/incident-details.json")
                .whenTimeElapses(IncidentLifecycleHandler.ESCALATE_DEADLINE)
                .expectEvents(EscalateIncident.class)
                .expectWebRequest(r -> true)
                .andThen()
                .whenTimeElapses(Duration.ofMinutes(1))
                .expectEvents(UpdateAssistance.class)
                .andThen()
                .whenCommand("/pitstop/close-incident.json")
                .expectNoSchedules();
    }

    static class AaaMock {

        @HandlePost("/aaa/orders")
        String handlePost() {
            return "123";
        }

        @HandleGet("/aaa/orders/{orderId}")
        String handlePost(@PathParam String orderId) {
            return """
                    {
                      "plannedArrival" : "2025-01-17T16:00:00.000+01:00",
                      "workStatus" : "arrived"
                    }
                    """;
        }


    }

}