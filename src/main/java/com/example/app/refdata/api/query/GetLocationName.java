package com.example.app.refdata.api.query;

import io.fluxcapacitor.javaclient.tracking.handling.Request;
import io.fluxcapacitor.javaclient.tracking.handling.authentication.RequiresUser;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
@RequiresUser
public class GetLocationName implements Request<String> {
    @NotNull BigDecimal latitude, longitude;
}
