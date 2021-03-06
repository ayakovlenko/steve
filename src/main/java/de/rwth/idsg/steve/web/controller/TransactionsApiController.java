package de.rwth.idsg.steve.web.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.ocpp.RequestResult;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.ChargePointService16_Client;
import de.rwth.idsg.steve.service.TransactionStopService;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStopTransactionParams;
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
import java.util.Map;

@RestController
@Validated
public class TransactionsApiController {

    private final static String BASE_ENDPOINT = "/api/transactions";

    @Autowired
    private ChargePointService16_Client client;

    @Autowired
    private TaskStore taskStore;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionStopService transactionStopService;

    @PostMapping(BASE_ENDPOINT)
    ResponseEntity<RemoteStartTransactionResponse> startTransaction(
            @Valid @RequestBody RemoteStartTransactionRequest req) {
        // TODO: add precondition to check if there are active transactions already

        var params = new RemoteStartTransactionParams();
        var cps = List.of(new ChargePointSelect(OcppTransport.JSON, req.chargeBoxId));
        params.setIdTag(req.idTag);
        params.setConnectorId(req.connectorId);
        params.setChargePointSelectList(cps);

        var task = taskStore.get(client.remoteStartTransaction(params));
        var result = (RequestResult) waitTask(task).get(req.chargeBoxId);
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
    ResponseEntity<RemoteStopTransactionResponse> stopTransaction(
            @Valid @RequestBody RemoteStopTransactionRequest req) {
        var transactionIds = transactionRepository.getActiveTransactionIds(req.chargeBoxId);

        if (transactionIds.isEmpty()) {
            return new ResponseEntity<>(
                    new RemoteStopTransactionResponse(null, "NO_ACTIVE_TRANSACTIONS"),
                    HttpStatus.EXPECTATION_FAILED
            );
        }
        var transactionId = transactionIds.remove(transactionIds.size() - 1);
        var params = new RemoteStopTransactionParams();
        var cps = List.of(new ChargePointSelect(OcppTransport.JSON, req.chargeBoxId));
        params.setTransactionId(transactionId);
        params.setChargePointSelectList(cps);

        var task = taskStore.get(client.remoteStopTransaction(params));

        transactionStopService.stop(transactionIds);
        var result = (RequestResult) waitTask(task).get(req.chargeBoxId);
        transactionStopService.stop(transactionId);

        return new ResponseEntity<>(
                new RemoteStopTransactionResponse("DELETED", null),
                HttpStatus.OK
        );
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class RemoteStartTransactionResponse {
        private String response;
    }

    @Data
    private static class RemoteStopTransactionRequest {

        @NotNull
        @JsonProperty("chargepoint_id")
        private String chargeBoxId;
    }

    @Data
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class RemoteStopTransactionResponse {
        private String status;
        private String error;
    }

    @SuppressWarnings("rawtypes")
    private Map waitTask(@SuppressWarnings("rawtypes") CommunicationTask task) {
        //noinspection StatementWithEmptyBody
        while (!task.isFinished() || task.getResultMap().size() > 1) {
        }
        return task.getResultMap();
    }
}
