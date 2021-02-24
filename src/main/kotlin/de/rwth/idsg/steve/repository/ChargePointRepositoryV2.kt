package de.rwth.idsg.steve.repository

import de.rwth.idsg.steve.model.ChargePoint
import jooq.steve.db.Tables.CHARGE_BOX
import jooq.steve.db.Tables.CONNECTOR
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class ChargePointRepositoryV2 {

    @Autowired
    lateinit var ctx: DSLContext

    fun findChargePoints(): MutableList<ChargePoint> {
        return ctx.select()
                .from(
                        CHARGE_BOX.leftJoin(CONNECTOR)
                                .on(CHARGE_BOX.CHARGE_BOX_ID.eq(CONNECTOR.CHARGE_BOX_ID)))
                .fetch()
                .map { r ->
                    ChargePoint(
                            r.get(CHARGE_BOX.CHARGE_BOX_ID),
                            r.get(CONNECTOR.CONNECTOR_ID),
                            r.get(CHARGE_BOX.LOCATION_LATITUDE),
                            r.get(CHARGE_BOX.LOCATION_LONGITUDE))
                }
    }
}
