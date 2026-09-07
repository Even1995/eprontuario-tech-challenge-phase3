package com.br.eprontuario.notificationsservice.adapter.out.kafka.consumer;

import com.br.eprontuario.notificationsservice.adapter.out.jpa.entity.NotificationJpaEntity;
import com.br.eprontuario.notificationsservice.adapter.out.jpa.repository.NotificationJpaRepository;
import com.br.eprontuario.notificationsservice.domain.entity.AppointmentEventMessage;
import com.br.hospital.notificationsservice.domain.entity.NotificationStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AppointmentEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentEventConsumer.class);

    private final NotificationJpaRepository notificationJpaRepository;
    private final ObjectMapper objectMapper;

    public AppointmentEventConsumer(NotificationJpaRepository notificationJpaRepository,
                                    ObjectMapper objectMapper) {
        this.notificationJpaRepository = notificationJpaRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topics.appointments-created}", 
                   groupId = "${kafka.consumer-group}")
    public void onAppointmentCreated(String message) {
        try {
            logger.info("Received appointment created event: {}", message);
            
            AppointmentEventMessage event = objectMapper.readValue(message, AppointmentEventMessage.class);
            
            createNotification(event, "Appointment Confirmation",
                    String.format("Your appointment has been scheduled for %s", event.getScheduledAt()));
            
            logger.info("Notification created for appointment: {}", event.getAppointmentId());
        } catch (Exception e) {
            logger.error("Error processing appointment created event", e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.appointments-updated}", 
                   groupId = "${kafka.consumer-group}")
    public void onAppointmentUpdated(String message) {
        try {
            logger.info("Received appointment updated event: {}", message);
            
            AppointmentEventMessage event = objectMapper.readValue(message, AppointmentEventMessage.class);
            
            String messageText = buildUpdateMessage(event);
            createNotification(event, "Appointment Update", messageText);
            
            logger.info("Notification created for updated appointment: {}", event.getAppointmentId());
        } catch (Exception e) {
            logger.error("Error processing appointment updated event", e);
        }
    }

    private void createNotification(AppointmentEventMessage event, String subject, String messageText) {
        try {
            NotificationJpaEntity notification = new NotificationJpaEntity();
            notification.setId(UUID.randomUUID());
            notification.setAppointmentId(event.getAppointmentId());
            notification.setPatientId(event.getPatientId());
            notification.setStatus(NotificationStatus.PENDING);
            notification.setMessage(messageText);
            notification.setAttemptCount(0);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setUpdatedAt(LocalDateTime.now());

            notificationJpaRepository.save(notification);
            logger.info("Notification saved with ID: {}", notification.getId());
        } catch (Exception e) {
            logger.error("Error creating notification for appointment: {}", event.getAppointmentId(), e);
        }
    }

    private String buildUpdateMessage(AppointmentEventMessage event) {
        return String.format("Your appointment status has been updated to %s. %s",
                event.getStatus(),
                event.getReason() != null ? "Reason: " + event.getReason() : "");
    }

}
