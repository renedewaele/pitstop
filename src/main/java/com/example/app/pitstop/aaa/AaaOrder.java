package com.example.app.pitstop.aaa;

import com.example.app.pitstop.api.IncidentDetails;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class AaaOrder {
    @Getter(AccessLevel.NONE)
    IncidentDetails incidentDetails;

    public BigDecimal getLat() {
        return incidentDetails.getLocation().getLatitude();
    }

    public BigDecimal getLon() {
        return incidentDetails.getLocation().getLongitude();
    }

    public String getLicensePlate() {
        return incidentDetails.getVehicle().getLicensePlateNumber();
    }

}
