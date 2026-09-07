package com.br.eprontuario.schedulingservice.adapter.out.kafka.producer;

import com.br.eprontuario.schedulingservice.adapter.out.kafka.event.AppointmentEventMessage;
import com.br.eprontuario.schedulingservice.domain.entity.Appointment;
import com.br.eprontuario.schedulingservice.port.out.messaging.AppointmentEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentKafkaProducer implements AppointmentEventPublisher {

    @Value("${kafka.topics.appointments.created:appointments.created}")
    private String appointmentCreatedTopic;

    @Value("${kafka.topics.appointments.updated:appointments.updated}")
    private String appointmentUpdatedTopic;

    @Value("${app.schema-version:1.0.0}")
    private String schemaVersion;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publishAppointmentCreated(Appointment appointment) {
        AppointmentEventMessage message = buildEventMessage(appointment, AppointmentEventMessage.Action.CREATED);
        publishEvent(appointmentCreatedTopic, message);
        log.info("Published appointment.created event for ID: {}", appointment.getId());
    }

    @Override
    public void publishAppointmentUpdated(Appointment appointment) {
        AppointmentEventMessage message = buildEventMessage(appointment, AppointmentEventMessage.Action.UPDATED);
        publishEvent(appointmentUpdatedTopic, message);
        log.info("Published appointment.updated event for ID: {}", appointment.getId());
    }

    private void publishEvent(String topic, AppointmentEventMessage message) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            Message<String> kafkaMessage = MessageBuilder
                    .withPayload(payload)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .build();

            kafkaTemplate.send(kafkaMessage);
        } catch (Exception e) {
            log.error("Failed to publish event to topic: {}", topic, e);
            throw new RuntimeException("Failed to publish appointment event", e);
        }
    }

    private AppointmentEventMessage buildEventMessage(Appointment appointment, AppointmentEventMessage.Action action) {
        AppointmentEventMessage.AppointmentData appointmentData = AppointmentEventMessage.AppointmentData.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .nurseId(appointment.getNurseId())
                .startAt(appointment.getStartAt())
                .endAt(appointment.getEndAt())
                .status(appointment.getStatus().name())
                .notes(appointment.getNotes())
                .build();

        AppointmentEventMessage.EventMetadata metadata = AppointmentEventMessage.EventMetadata.builder()
                .createdBy(appointment.getMetadata().getCreatedBy())
                .createdAt(appointment.getMetadata().getCreatedAt())
                .schemaVersion(schemaVersion)
                .build();

        return AppointmentEventMessage.builder()
                .eventId(UUID.randomUUID().toString())
                .action(action)
                .appointment(appointmentData)
                .metadata(metadata)
                .build();
    }
}
