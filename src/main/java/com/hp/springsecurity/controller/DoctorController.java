package com.hp.springsecurity.controller;

import com.hp.springsecurity.dto.AppointmentResponseDto;
import com.hp.springsecurity.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final AppointmentService appointmentService;

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointmentsOfDoctor() {
        return ResponseEntity.ok(appointmentService.getAllAppointmentsOfDoctor(1L));
    }
}
