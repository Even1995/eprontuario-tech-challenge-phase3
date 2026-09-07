package com.br.historico.service.adapter.in.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentGraphQL {
    private String id;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String status;
    private String notes;
    private String doctorId;
    private String nurseId;
    private String patientId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
