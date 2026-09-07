package com.br.eprontuario.schedulingservice.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentMetadata {
    private UUID createdBy;
    private Instant createdAt;
    private UUID updatedBy;
    private Instant updatedAt;

    public static AppointmentMetadata create(UUID createdBy) {
        Instant now = Instant.now();
        return new AppointmentMetadata(createdBy, now, createdBy, now);
    }

    public void markUpdated(UUID updatedBy) {
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
    }
}
