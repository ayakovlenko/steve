package de.rwth.idsg.steve.web.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.ocpp.RequestResult;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.ChargePointService16_Client;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@Validated
public class TransactionsApiController {

    private final static String BASE_ENDPOINT = "/api/transactions";

    @Autowired
    private ChargePointService16_Client client;

    @Autowired
    private TaskStore taskStore;

    @PostMapping(BASE_ENDPOINT)
    ResponseEntity<RemoteStartTransactionResponse> startTransaction(
            @Valid @RequestBody RemoteStartTransactionRequest req) {

        var params = new RemoteStartTransactionParams();
        var cps = new ChargePointSelect(OcppTransport.JSON, req.chargeBoxId);
        params.setIdTag(req.idTag);
        params.setConnectorId(req.connectorId);
        params.setChargePointSelectList(List.of(cps));

        var task = taskStore.get(client.remoteStartTransaction(params));

        //noinspection StatementWithEmptyBody
        while (!task.isFinished() || task.getResultMap().size() > 1) {
        }
        var result = (RequestResult) task.getResultMap().get(req.chargeBoxId);
        if (result.getResponse() == null) {
            return new ResponseEntity<>(
                    new RemoteStartTransactionResponse(null),
                    HttpStatus.PRECONDITION_FAILED
            );
        } else {
            return new ResponseEntity<>(
                    new RemoteStartTransactionResponse(result.getResponse()),
                    HttpStatus.OK
            );
        }
    }

    @DeleteMapping(BASE_ENDPOINT)
    void stopTransaction() {
        // TODO
    }

    @Data
    private static class RemoteStartTransactionRequest {

        @NotNull
        @JsonProperty("chargepoint_id")
        private String chargeBoxId;

        @NotNull
        @JsonProperty("connector_id")
        private int connectorId;

        @NotNull
        @JsonProperty("id_tag")
        private String idTag;
    }

    @Data
    @AllArgsConstructor
    private static class RemoteStartTransactionResponse {
        private String response;
    }
}
