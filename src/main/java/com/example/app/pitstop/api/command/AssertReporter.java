package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.Incident;
import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.javaclient.modeling.AssertLegal;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;

public interface AssertReporter {
    @AssertLegal
    default void assertReporter(Incident incident, Sender sender) {
        if (!sender.isAuthorizedFor(incident.getReporter())) {
            throw new IllegalCommandException("Only allowed if you reported the incident.");
        }
    }
}
