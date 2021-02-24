package de.rwth.idsg.steve.web.controller

import de.rwth.idsg.steve.model.ChargePoint
import de.rwth.idsg.steve.repository.ChargePointRepositoryV2
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ChargePointsApiController {

    @Autowired
    private lateinit var repo: ChargePointRepositoryV2

    @GetMapping("/api/chargepoints")
    fun getAll(): List<ChargePoint> {
        return repo.findChargePoints()
    }
}
