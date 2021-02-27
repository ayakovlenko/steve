package de.rwth.idsg.steve.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChargePoint {

    public final String id;

    @JsonProperty("registration_status")
    public final String registrationStatus;

    @JsonProperty("lat")
    public final BigDecimal latitude;

    @JsonProperty("long")
    public final BigDecimal longtitude;

    public final List<Connector> connectors;

    public ChargePoint(String id,
                       String registrationStatus,
                       BigDecimal latitude,
                       BigDecimal longtitude,
                       List<Connector> connectors) {
        this.id = id;
        this.registrationStatus = registrationStatus;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.connectors = connectors;
    }
}
