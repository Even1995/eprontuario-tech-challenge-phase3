package com.br.eprontuario.schedulingservice.adapter.in.rest.controller;

import com.br.eprontuario.schedulingservice.adapter.in.rest.dto.AppointmentResponse;
import com.br.eprontuario.schedulingservice.adapter.in.rest.dto.CreateAppointmentRequest;
import com.br.eprontuario.schedulingservice.adapter.in.rest.mapper.AppointmentMapper;
import com.br.eprontuario.schedulingservice.domain.entity.Appointment;
import com.br.eprontuario.schedulingservice.port.in.appointment.AppointmentUseCase;
import com.br.eprontuario.schedulingservice.usecase.appointment.create.CreateAppointmentUseCase;
import com.br.eprontuario.schedulingservice.usecase.appointment.query.QueryAppointmentUseCase;
import com.br.eprontuario.schedulingservice.usecase.appointment.update.UpdateAppointmentUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Appointment management endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class AppointmentController {

    private final CreateAppointmentUseCase createAppointmentUseCase;
    private final UpdateAppointmentUseCase updateAppointmentUseCase;
    private final QueryAppointmentUseCase queryAppointmentUseCase;
    private final AppointmentMapper mapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE')")
    @Operation(summary = "Create a new appointment")
    public ResponseEntity<AppointmentResponse> createAppointment(
            @RequestBody CreateAppointmentRequest request,
            Authentication authentication) {
        log.info("Creating appointment for patient: {}", request.getPatientId());
        
        UUID userId = (UUID) authentication.getPrincipal();
        AppointmentUseCase.CreateAppointmentCommand command = mapper.toCreateCommand(request, userId);
        
        Appointment appointment = createAppointmentUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(appointment));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE')")
    @Operation(summary = "Update an existing appointment")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable UUID id,
            @RequestBody CreateAppointmentRequest request,
            Authentication authentication) {
        log.info("Updating appointment: {}", id);
        
        UUID userId = (UUID) authentication.getPrincipal();
        AppointmentUseCase.UpdateAppointmentCommand command = new AppointmentUseCase.UpdateAppointmentCommand(
                request.getDoctorId(),
                request.getNurseId(),
                request.getStartAt(),
                request.getEndAt(),
                request.getNotes(),
                userId
        );
        
        Appointment appointment = updateAppointmentUseCase.execute(id, command);
        return ResponseEntity.ok(mapper.toResponse(appointment));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'PATIENT')")
    @Operation(summary = "Get appointment by ID")
    public ResponseEntity<AppointmentResponse> getAppointmentById(
            @PathVariable UUID id,
            Authentication authentication) {
        log.debug("Fetching appointment: {}", id);
        
        Appointment appointment = queryAppointmentUseCase.getAppointmentById(id);
        // Add authorization check here if needed
        
        return ResponseEntity.ok(mapper.toResponse(appointment));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE') or @authorizationService.isPatientOrAdmin(#patientId, authentication)")
    @Operation(summary = "List all appointments for a patient")
    public ResponseEntity<List<AppointmentResponse>> listPatientAppointments(
            @PathVariable UUID patientId) {
        log.debug("Listing appointments for patient: {}", patientId);
        
        List<Appointment> appointments = queryAppointmentUseCase.listPatientAppointments(patientId);
        List<AppointmentResponse> responses = appointments.stream()
                .map(mapper::toResponse)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE')")
    @Operation(summary = "List all appointments for a doctor")
    public ResponseEntity<List<AppointmentResponse>> listDoctorAppointments(
            @PathVariable UUID doctorId) {
        log.debug("Listing appointments for doctor: {}", doctorId);
        
        List<Appointment> appointments = queryAppointmentUseCase.listDoctorAppointments(doctorId);
        List<AppointmentResponse> responses = appointments.stream()
                .map(mapper::toResponse)
                .toList();
        
        return ResponseEntity.ok(responses);
    }
}
