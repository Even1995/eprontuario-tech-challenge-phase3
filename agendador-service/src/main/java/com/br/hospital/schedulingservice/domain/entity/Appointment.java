package com.br.eprontuario.schedulingservice.domain.entity;

import com.br.eprontuario.schedulingservice.domain.valueobject.AppointmentMetadata;
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
public class Appointment {

    public enum Status {
        SCHEDULED, CANCELLED, COMPLETED
    }

    private UUID id;
    private UUID patientId;
    private UUID doctorId;
    private UUID nurseId;
    private Instant startAt;
    private Instant endAt;
    private Status status;
    private String notes;
    private AppointmentMetadata metadata;

    public static Appointment create(
            UUID patientId,
            UUID doctorId,
            UUID nurseId,
            Instant startAt,
            Instant endAt,
            String notes,
            UUID createdBy) {
        return Appointment.builder()
                .id(UUID.randomUUID())
                .patientId(patientId)
                .doctorId(doctorId)
                .nurseId(nurseId)
                .startAt(startAt)
                .endAt(endAt)
                .status(Status.SCHEDULED)
                .notes(notes)
                .metadata(AppointmentMetadata.create(createdBy))
                .build();
    }

    public void update(UUID doctorId, UUID nurseId, Instant startAt, Instant endAt, String notes, UUID updatedBy) {
        this.doctorId = doctorId;
        this.nurseId = nurseId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.notes = notes;
        this.metadata.markUpdated(updatedBy);
    }

    public void cancel() {
        this.status = Status.CANCELLED;
    }

    public void markCompleted() {
        this.status = Status.COMPLETED;
    }

    public boolean isScheduled() {
        return status == Status.SCHEDULED;
    }

    public boolean isPast() {
        return Instant.now().isAfter(endAt != null ? endAt : startAt);
    }
}
