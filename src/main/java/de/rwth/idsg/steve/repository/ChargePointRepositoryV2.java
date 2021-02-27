package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.model.ChargePoint;
import de.rwth.idsg.steve.model.Connector;
import jooq.steve.db.tables.records.ChargeBoxRecord;
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

@Repository
public class ChargePointRepositoryV2 {

    @Autowired
    private DSLContext ctx;

    public List<ChargePoint> findChargePoints() {
        return ctx.select()
                .from(CHARGE_BOX)
                .leftJoin(CONNECTOR)
                .on(CHARGE_BOX.CHARGE_BOX_ID.eq(CONNECTOR.CHARGE_BOX_ID))
                .fetchGroups(CHARGE_BOX)
                .entrySet()
                .stream()
                .map(ChargePointRepositoryV2::toModel).collect(Collectors.toList());
    }

    private static ChargePoint toModel(Map.Entry<ChargeBoxRecord, Result<Record>> r) {
        return new ChargePoint(
                r.getKey().get(CHARGE_BOX.CHARGE_BOX_ID),
                r.getKey().get(CHARGE_BOX.REGISTRATION_STATUS),
                r.getKey().get(CHARGE_BOX.LOCATION_LATITUDE),
                r.getKey().get(CHARGE_BOX.LOCATION_LONGITUDE),
                r.getValue()
                        .stream()
                        .filter(cr ->
                                cr.get(CONNECTOR.CONNECTOR_ID) != null)
                        .map(cr ->
                                new Connector(cr.get(CONNECTOR.CONNECTOR_ID))
                        ).collect(Collectors.toList()));
    }
}
