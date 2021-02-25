package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.model.ChargePoint;
import de.rwth.idsg.steve.model.Connector;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
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
                .map(r ->
                        new ChargePoint(
                                r.getKey().get(CHARGE_BOX.CHARGE_BOX_ID),
                                r.getValue().stream().map(cr ->
                                        new Connector(cr.get(CONNECTOR.CONNECTOR_ID))
                                ).collect(Collectors.toList()),
                                r.getKey().get(CHARGE_BOX.LOCATION_LATITUDE),
                                r.getKey().get(CHARGE_BOX.LOCATION_LONGITUDE))
                ).collect(Collectors.toList());
    }
}
