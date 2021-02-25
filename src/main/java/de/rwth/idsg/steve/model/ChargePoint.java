package de.rwth.idsg.steve.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChargePoint {

    public final String id;

    public final List<Connector> connectors;

    @JsonProperty("lat")
    public final BigDecimal latitude;

    @JsonProperty("long")
    public final BigDecimal longtitude;

    public ChargePoint(String id,
                       List<Connector> connectors,
                       BigDecimal latitude,
                       BigDecimal longtitude) {
        this.id = id;
        this.connectors = connectors;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }
}
