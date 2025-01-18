package com.example.app.pitstop.aaa;

import com.example.app.pitstop.api.Assistance;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import io.fluxcapacitor.common.serialization.JsonUtils;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value
public class AaaAssistance {
    String orderId;
    Instant timestamp;
    Instant plannedArrival;
    AaaStatus workStatus;

    public Instant getEta() {
        return plannedArrival;
    }

    public Instant getAta() {
        return switch (workStatus) {
            case accepted, planned, enRoute -> null;
            default -> getEta();
        };
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public boolean isResolved() {
        return switch (workStatus) {
            case completed -> true;
            default -> false;
        };
    }

    public Assistance toAssistance() {
        return JsonUtils.convertValue(this, Assistance.class);
    }

    public enum AaaStatus {
        accepted, planned, enRoute, arrived, completed
    }
}
