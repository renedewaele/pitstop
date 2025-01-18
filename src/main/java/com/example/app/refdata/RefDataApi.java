package com.example.app.refdata;

import com.example.app.pitstop.api.Vehicle;
import com.example.app.refdata.api.Operator;
import com.example.app.refdata.api.OperatorDetails;
import com.example.app.refdata.api.OperatorId;
import com.example.app.refdata.api.command.RegisterOperator;
import com.example.app.refdata.api.query.FindVehicles;
import com.example.app.refdata.api.query.GetLocationName;
import com.example.app.refdata.api.query.GetOperators;
import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.authentication.RequiresUser;
import io.fluxcapacitor.javaclient.web.HandleGet;
import io.fluxcapacitor.javaclient.web.HandlePost;
import io.fluxcapacitor.javaclient.web.Path;
import io.fluxcapacitor.javaclient.web.QueryParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Path("/api")
@RequiresUser
public class RefDataApi {

    @HandlePost("operator")
    OperatorId registerOperator(OperatorDetails operatorDetails, Sender sender) {
        return FluxCapacitor.sendCommandAndWait(new RegisterOperator(operatorDetails, sender.getUserId()));
    }

    @HandleGet("operators")
    List<Operator> getOperators() {
        return FluxCapacitor.queryAndWait(new GetOperators());
    }

    @HandleGet("vehicles")
    List<Vehicle> getVehicles(@QueryParam String term) {
        return FluxCapacitor.queryAndWait(new FindVehicles(term));
    }

    @HandleGet("location")
    String getLocationName(@QueryParam BigDecimal latitude, @QueryParam BigDecimal longitude) {
        return FluxCapacitor.queryAndWait(new GetLocationName(latitude, longitude));
    }

}
