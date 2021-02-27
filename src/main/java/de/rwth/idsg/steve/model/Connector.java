package de.rwth.idsg.steve.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Connector {

    public final int id;

    public final String status;

    @JsonProperty("status_timestamp")
    public final DateTime statusTimestamp;

    @JsonProperty("error_code")
    public final String errorCode;

    @JsonProperty("error_info")
    public final String errorInfo;

    public Connector(int id,
                     String status,
                     DateTime statusTimestamp,
                     String errorCode,
                     String errorInfo) {
        this.id = id;
        this.status = status;
        this.statusTimestamp = statusTimestamp;
        this.errorCode = errorCode;
        this.errorInfo = errorInfo;
    }
}
