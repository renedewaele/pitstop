package com.example.app.pitstop.aaa;

import io.fluxcapacitor.javaclient.scheduling.Periodic;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
@Periodic(delay = 60_000, autoStart = false)
public class PollAaa {
    @NotBlank String aaaId;
}
