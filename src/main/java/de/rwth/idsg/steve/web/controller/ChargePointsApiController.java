package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.model.ChargePoint;
import de.rwth.idsg.steve.repository.ChargePointRepositoryV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChargePointsApiController {

    @Autowired
    private ChargePointRepositoryV2 repo;

    @GetMapping("/api/chargepoints")
    List<ChargePoint> getAll() {
        return repo.findChargePoints();
    }
}
