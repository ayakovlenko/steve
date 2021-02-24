package de.rwth.idsg.steve.repository

import de.rwth.idsg.steve.model.ChargePoint
import de.rwth.idsg.steve.model.Connector
import jooq.steve.db.Tables.CHARGE_BOX
import jooq.steve.db.Tables.CONNECTOR
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class ChargePointRepositoryV2 {

    @Autowired
    lateinit var ctx: DSLContext

    fun findChargePoints(): List<ChargePoint> {
        return ctx.select()
                .from(CHARGE_BOX)
                .leftJoin(CONNECTOR)
                .on(CHARGE_BOX.CHARGE_BOX_ID.eq(CONNECTOR.CHARGE_BOX_ID))
                .fetchGroups(CHARGE_BOX)
                .map { r ->
                    ChargePoint(
                            r.key.get(CHARGE_BOX.CHARGE_BOX_ID),
                            r.value.map { cr ->
                                Connector(cr.get(CONNECTOR.CONNECTOR_ID))
                            },
                            r.key.get(CHARGE_BOX.LOCATION_LATITUDE),
                            r.key.get(CHARGE_BOX.LOCATION_LONGITUDE))
                }
    }
}
