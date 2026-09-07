package com.br.eprontuario.schedulingservice.adapter.out.kafka.event;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class AppointmentEventMessage {

    public enum Action {
        CREATED, UPDATED
    }

    private String eventId;
    private Action action;
    private AppointmentData appointment;
    private EventMetadata metadata;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppointmentData {
        private UUID id;
        private UUID patientId;
        private UUID doctorId;
        private UUID nurseId;
        private Instant startAt;
        private Instant endAt;
        private String status;
        private String notes;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventMetadata {
        private UUID createdBy;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private Instant createdAt;
        private String schemaVersion;
    }
}
