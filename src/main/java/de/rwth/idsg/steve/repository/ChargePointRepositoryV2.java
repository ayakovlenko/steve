package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.model.ChargePoint;
import de.rwth.idsg.steve.model.Connector;
import jooq.steve.db.tables.records.ChargeBoxRecord;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static jooq.steve.db.Tables.CHARGE_BOX;
import static jooq.steve.db.Tables.CONNECTOR;
import static jooq.steve.db.Tables.CONNECTOR_STATUS;
import static org.jooq.impl.DSL.max;

@Repository
public class ChargePointRepositoryV2 {

    @Autowired
    private DSLContext ctx;

    public List<ChargePoint> findChargePoints() {
        var latestConnectorStatus =
                ctx.select(
                        CONNECTOR_STATUS.CONNECTOR_PK.as("connector_pk"),
                        max(CONNECTOR_STATUS.STATUS_TIMESTAMP).as("connector_status_timestamp"))
                        .from(CONNECTOR_STATUS)
                        .groupBy(CONNECTOR_STATUS.CONNECTOR_PK)
                        .asTable();

        var latestConnectorStatusJoinCond =
                CONNECTOR.CONNECTOR_PK.eq(
                        latestConnectorStatus.field("connector_pk", Integer.class))
                        .and(CONNECTOR_STATUS.STATUS_TIMESTAMP.eq(
                                latestConnectorStatus.field("connector_status_timestamp", DateTime.class)));

        return ctx.select()
                .from(CHARGE_BOX)
                .leftJoin(CONNECTOR)
                .on(CHARGE_BOX.CHARGE_BOX_ID.eq(CONNECTOR.CHARGE_BOX_ID))
                .leftJoin(CONNECTOR_STATUS)
                .on(CONNECTOR_STATUS.CONNECTOR_PK.eq(CONNECTOR.CONNECTOR_PK))
                .join(latestConnectorStatus)
                .on(latestConnectorStatusJoinCond)
                .groupBy(CHARGE_BOX.CHARGE_BOX_ID, CONNECTOR.CONNECTOR_PK)
                .fetchGroups(CHARGE_BOX)
                .entrySet()
                .stream()
                .map(ChargePointRepositoryV2::toChargePointModel)
                .collect(Collectors.toList());
    }

    private static ChargePoint toChargePointModel(Map.Entry<ChargeBoxRecord, Result<Record>> r) {
        return new ChargePoint(
                r.getKey().get(CHARGE_BOX.CHARGE_BOX_ID),
                r.getKey().get(CHARGE_BOX.REGISTRATION_STATUS),
                r.getKey().get(CHARGE_BOX.LOCATION_LATITUDE),
                r.getKey().get(CHARGE_BOX.LOCATION_LONGITUDE),
                r.getValue()
                        .stream()
                        .filter(cr ->
                                cr.get(CONNECTOR.CONNECTOR_ID) != null)
                        .map(ChargePointRepositoryV2::toConnectorModel)
                        .collect(Collectors.toList()));
    }

    private static Connector toConnectorModel(Record cr) {
        var id = cr.get(CONNECTOR.CONNECTOR_ID);
        var status = cr.get(CONNECTOR_STATUS.STATUS);
        var statusTimestamp = cr.get(CONNECTOR_STATUS.STATUS_TIMESTAMP);
        var errorCode = cr.get(CONNECTOR_STATUS.ERROR_CODE);
        var errorInfo = cr.get(CONNECTOR_STATUS.ERROR_INFO);
        return new Connector(id, status, statusTimestamp, errorCode, errorInfo);
    }
}
