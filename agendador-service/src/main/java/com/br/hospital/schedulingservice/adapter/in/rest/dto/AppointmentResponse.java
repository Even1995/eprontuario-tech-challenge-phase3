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
@Schema(description = "DTO for appointment response")
public class AppointmentResponse {

    @Schema(description = "Appointment ID", example = "550e8400-e29b-41d4-a716-446655440100")
    private UUID id;

    @Schema(description = "Patient ID")
    private UUID patientId;

    @Schema(description = "Doctor ID")
    private UUID doctorId;

    @Schema(description = "Nurse ID")
    private UUID nurseId;

    @Schema(description = "Appointment start time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant startAt;

    @Schema(description = "Appointment end time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant endAt;

    @Schema(description = "Appointment status", example = "SCHEDULED")
    private String status;

    @Schema(description = "Appointment notes")
    private String notes;

    @Schema(description = "User who created the appointment")
    private UUID createdBy;

    @Schema(description = "Creation timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant createdAt;

    @Schema(description = "User who last updated the appointment")
    private UUID updatedBy;

    @Schema(description = "Last update timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant updatedAt;
}
