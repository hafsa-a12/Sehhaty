package com.sehhaty.web;

import com.sehhaty.dto.AppointmentDtos.CreateRequest;
import com.sehhaty.dto.AppointmentDtos.Response;
import com.sehhaty.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Tous les endpoints exigent un JWT : le patient est toujours l'utilisateur du token. */
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointments;

    public AppointmentController(AppointmentService appointments) {
        this.appointments = appointments;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response create(Authentication auth, @Valid @RequestBody CreateRequest req) {
        return appointments.create(auth.getName(), req);
    }

    @GetMapping
    public List<Response> mine(Authentication auth) {
        return appointments.listMine(auth.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(Authentication auth, @PathVariable Long id) {
        appointments.cancel(auth.getName(), id);
    }
}
