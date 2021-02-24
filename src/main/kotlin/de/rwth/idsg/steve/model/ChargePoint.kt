package de.rwth.idsg.steve.model

import java.math.BigDecimal

data class ChargePoint(val id: String,
                       val connectorId: Int?,
                       val latitude: BigDecimal?,
                       val longtitude: BigDecimal?)
