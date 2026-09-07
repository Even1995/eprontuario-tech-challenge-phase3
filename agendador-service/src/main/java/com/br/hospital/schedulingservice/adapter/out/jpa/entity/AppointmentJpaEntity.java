package com.br.eprontuario.schedulingservice.adapter.out.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "patient_id", nullable = false, columnDefinition = "UUID")
    private UUID patientId;

    @Column(name = "doctor_id", nullable = false, columnDefinition = "UUID")
    private UUID doctorId;

    @Column(name = "nurse_id", columnDefinition = "UUID")
    private UUID nurseId;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_by", nullable = false, columnDefinition = "UUID")
    private UUID createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_by", columnDefinition = "UUID")
    private UUID updatedBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum Status {
        SCHEDULED, CANCELLED, COMPLETED
    }
}
