package com.sehhaty.web;

import com.sehhaty.dto.DoctorDto;
import com.sehhaty.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctors;

    public DoctorController(DoctorService doctors) {
        this.doctors = doctors;
    }

    @GetMapping
    public List<DoctorDto> all() {
        return doctors.findAll();
    }

    @GetMapping("/nearby")
    public List<DoctorDto> nearby(@RequestParam double lat,
                                  @RequestParam double lng,
                                  @RequestParam(defaultValue = "10") double radius) {
        if (lat < -90 || lat > 90 || lng < -180 || lng > 180 || radius < 0 || radius > 500) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "lat [-90;90], lng [-180;180] et radius [0;500] km requis");
        }
        return doctors.findNearby(lat, lng, radius);
    }
}
