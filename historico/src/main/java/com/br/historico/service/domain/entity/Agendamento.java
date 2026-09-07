package com.br.historico.service.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agendamento {
    private String id;
    private LocalDateTime horarioInicio;
    private LocalDateTime horarioFim;
    private String status;
    private String notas;
    private String doctorId;
    private String nurseId;
    private String patientId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
