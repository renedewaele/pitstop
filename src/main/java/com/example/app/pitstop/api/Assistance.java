package com.example.app.pitstop.api;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class Assistance {
    boolean resolved;
    Instant eta, ata;
    String assistantName;
}
