package com.br.eprontuario.historyservice.adapter.out.kafka.consumer;

import com.br.historico.service.adapter.out.jpa.entity.AppointmentGraphQLEntity;
import com.br.historico.service.adapter.out.jpa.repository.HistoryRepository;
import com.br.historico.service.adapter.out.kafka.consumer.HistoryEventConsumer;
import com.br.historico.service.domain.entity.AgendamentoStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HistoryEventConsumerTest {
    
    @Mock
    private HistoryRepository historyRepository;
    
    @InjectMocks
    private HistoryEventConsumer historyEventConsumer;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeEach
    public void setUp() {
        historyEventConsumer = new HistoryEventConsumer(historyRepository, objectMapper);
    }
    
    @Test
    public void testConsumeAppointmentCreatedEvent() {
        String message = """
            {
              "id": "apt-1",
              "startAt": "2024-01-15T10:00:00",
              "endAt": "2024-01-15T11:00:00",
              "status": "SCHEDULED",
              "notes": "Routine checkup",
              "doctorId": "doc-1",
              "nurseId": "nurse-1",
              "patientId": "patient-1"
            }
            """;
        
        when(historyRepository.saveAppointment(any(AppointmentGraphQLEntity.class)))
            .thenReturn(AppointmentGraphQLEntity.builder()
                .id("apt-1")
                .status(AgendamentoStatus.AGENDADO)
                .build());
        
        historyEventConsumer.consumeAppointmentCreated(message);
        
        verify(historyRepository, times(1)).saveAppointment(any(AppointmentGraphQLEntity.class));
    }
    
    @Test
    public void testConsumeAppointmentUpdatedEvent() {
        String message = """
            {
              "id": "apt-1",
              "startAt": "2024-01-15T11:00:00",
              "endAt": "2024-01-15T12:00:00",
              "status": "COMPLETED",
              "notes": "Updated notes",
              "doctorId": "doc-1",
              "patientId": "patient-1"
            }
            """;
        
        AppointmentGraphQLEntity existing = AppointmentGraphQLEntity.builder()
            .id("apt-1")
            .status(AgendamentoStatus.AGENDADO)
            .createdAt(LocalDateTime.now())
            .build();
        
        when(historyRepository.findAppointmentById("apt-1"))
            .thenReturn(Optional.of(existing));
        
        when(historyRepository.saveAppointment(any(AppointmentGraphQLEntity.class)))
            .thenReturn(existing);
        
        historyEventConsumer.consumeAppointmentUpdated(message);
        
        verify(historyRepository, times(1)).saveAppointment(any(AppointmentGraphQLEntity.class));
    }
    
    @Test
    public void testConsumeAppointmentCancelledEvent() {
        String message = """
            {
              "id": "apt-1"
            }
            """;
        
        AppointmentGraphQLEntity existing = AppointmentGraphQLEntity.builder()
            .id("apt-1")
            .status(AgendamentoStatus.AGENDADO)
            .createdAt(LocalDateTime.now())
            .build();
        
        when(historyRepository.findAppointmentById("apt-1"))
            .thenReturn(Optional.of(existing));
        
        when(historyRepository.saveAppointment(any(AppointmentGraphQLEntity.class)))
            .thenReturn(existing);
        
        historyEventConsumer.consumeAppointmentCancelled(message);
        
        verify(historyRepository, times(1)).saveAppointment(any(AppointmentGraphQLEntity.class));
    }
}
