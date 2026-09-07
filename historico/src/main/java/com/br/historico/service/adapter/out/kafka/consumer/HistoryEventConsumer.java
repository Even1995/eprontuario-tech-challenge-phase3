package com.br.historico.service.adapter.out.kafka.consumer;

import com.br.historico.service.adapter.out.jpa.entity.AppointmentGraphQLEntity;
import com.br.historico.service.adapter.out.jpa.repository.HistoryRepository;
import com.br.historico.service.domain.entity.AgendamentoStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoryEventConsumer {
    
    private final HistoryRepository historyRepository;
    private final ObjectMapper objectMapper;
    
    @KafkaListener(
        topics = "appointments.created",
        groupId = "history-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeAppointmentCreated(String message) {
        log.info("Received appointment.created event: {}", message);
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> eventData = objectMapper.readValue(message, Map.class);
            
            AppointmentGraphQLEntity appointment = AppointmentGraphQLEntity.builder()
                .id((String) eventData.get("id"))
                .startAt(parseLocalDateTime((String) eventData.get("startAt")))
                .endAt(parseLocalDateTime((String) eventData.get("endAt")))
                .status(AgendamentoStatus.valueOf((String) eventData.get("status")))
                .notes((String) eventData.get("notes"))
                .doctorId((String) eventData.get("doctorId"))
                .nurseId((String) eventData.get("nurseId"))
                .patientId((String) eventData.get("patientId"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            
            historyRepository.saveAppointment(appointment);
            log.info("Appointment created in history: {}", appointment.getId());
            
        } catch (Exception e) {
            log.error("Error processing appointment.created event", e);
        }
    }
    
    @KafkaListener(
        topics = "appointments.updated",
        groupId = "history-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeAppointmentUpdated(String message) {
        log.info("Received appointment.updated event: {}", message);
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> eventData = objectMapper.readValue(message, Map.class);
            
            String appointmentId = (String) eventData.get("id");
            
            AppointmentGraphQLEntity existing = historyRepository.findAppointmentById(appointmentId)
                .orElse(AppointmentGraphQLEntity.builder()
                    .id(appointmentId)
                    .createdAt(LocalDateTime.now())
                    .build());
            
            existing.setStartAt(parseLocalDateTime((String) eventData.get("startAt")));
            existing.setEndAt(parseLocalDateTime((String) eventData.get("endAt")));
            existing.setStatus(AgendamentoStatus.valueOf((String) eventData.get("status")));
            existing.setNotes((String) eventData.get("notes"));
            existing.setDoctorId((String) eventData.get("doctorId"));
            existing.setNurseId((String) eventData.get("nurseId"));
            existing.setPatientId((String) eventData.get("patientId"));
            existing.setUpdatedAt(LocalDateTime.now());
            
            historyRepository.saveAppointment(existing);
            log.info("Appointment updated in history: {}", appointmentId);
            
        } catch (Exception e) {
            log.error("Error processing appointment.updated event", e);
        }
    }
    
    @KafkaListener(
        topics = "appointments.cancelled",
        groupId = "history-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeAppointmentCancelled(String message) {
        log.info("Received appointment.cancelled event: {}", message);
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> eventData = objectMapper.readValue(message, Map.class);
            
            String appointmentId = (String) eventData.get("id");
            
            historyRepository.findAppointmentById(appointmentId)
                .ifPresent(appointment -> {
                    appointment.setStatus(AgendamentoStatus.CANCELAD0);
                    appointment.setUpdatedAt(LocalDateTime.now());
                    historyRepository.saveAppointment(appointment);
                    log.info("Appointment cancelled in history: {}", appointmentId);
                });
            
        } catch (Exception e) {
            log.error("Error processing appointment.cancelled event", e);
        }
    }
    
    private LocalDateTime parseLocalDateTime(String dateTimeString) {
        if (dateTimeString == null) {
            return LocalDateTime.now();
        }
        return LocalDateTime.parse(dateTimeString);
    }
}
