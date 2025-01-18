package com.example.app.pitstop.api;

import lombok.Builder;
import lombok.Value;

import java.time.Duration;

@Value
@Builder
public class AppSettings {
    Duration escalationDelay;
}
