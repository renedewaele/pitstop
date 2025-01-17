package com.example.app.pitstop;

import com.example.app.pitstop.aaa.AaaOrder;
import com.example.app.pitstop.api.IncidentId;
import com.example.app.pitstop.api.command.EscalateIncident;
import com.example.app.pitstop.api.command.ReportIncident;
import io.fluxcapacitor.javaclient.test.TestFixture;
import io.fluxcapacitor.javaclient.web.HandlePost;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AaaWorkOrderTest {

    final TestFixture testFixture = TestFixture.create(
                    PitStopApi.class, IncidentLifecycleHandler.class,
                    AaaMock.class, AaaWorkOrder.class)
            .withProperty("aaa-token", "789798")
            .withProperty("aaa.domain", "");

    @Test
    void reportViaApi() {
        testFixture
                .givenPost("/api/incidents", "/pitstop/incident-details.json")
                .whenTimeElapses(IncidentLifecycleHandler.ESCALATE_DEADLINE)
                .expectEvents(EscalateIncident.class)
                .expectWebRequest(r -> true);
    }

    static class AaaMock {

        @HandlePost("/aaa/orders")
        String handlePost() {
            return "123";
        }


    }

}