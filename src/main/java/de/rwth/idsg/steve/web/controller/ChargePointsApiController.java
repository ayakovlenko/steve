package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChargePointsApiController {

    @Autowired
    private ChargePointRepository repo;

    @GetMapping("/api/chargepoints")
    List<ChargePoint.Overview> getAll() {
        return repo.getChargePointOverviews();
    }
}
