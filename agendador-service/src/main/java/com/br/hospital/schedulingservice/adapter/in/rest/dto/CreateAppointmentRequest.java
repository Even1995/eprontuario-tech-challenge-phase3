package com.br.eprontuario.schedulingservice.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for creating an appointment")
public class CreateAppointmentRequest {

    @Schema(description = "Patient ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID patientId;

    @Schema(description = "Doctor ID", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID doctorId;

    @Schema(description = "Nurse ID (optional)", example = "550e8400-e29b-41d4-a716-446655440002")
    private UUID nurseId;

    @Schema(description = "Appointment start time", example = "2026-09-15T10:00:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant startAt;

    @Schema(description = "Appointment end time", example = "2026-09-15T10:30:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant endAt;

    @Schema(description = "Appointment notes", example = "Regular checkup")
    private String notes;
}
