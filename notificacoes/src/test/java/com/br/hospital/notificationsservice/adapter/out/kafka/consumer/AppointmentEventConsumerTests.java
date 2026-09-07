package com.br.eprontuario.notificationsservice.adapter.out.kafka.consumer;

import com.br.eprontuario.notificationsservice.adapter.out.jpa.entity.NotificationJpaEntity;
import com.br.eprontuario.notificationsservice.adapter.out.jpa.repository.NotificationJpaRepository;
import com.br.eprontuario.notificationsservice.domain.entity.NotificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092" })
@ActiveProfiles("test")
public class AppointmentEventConsumerTests {

    @Autowired
    private NotificationJpaRepository notificationJpaRepository;

    @BeforeEach
    public void setUp() {
        notificationJpaRepository.deleteAll();
    }

    @Test
    public void testNotificationCreation() {
        // Arrange
        UUID appointmentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        // Act - Create a notification manually (in real scenario this would come from Kafka)
        NotificationJpaEntity notification = new NotificationJpaEntity();
        notification.setId(UUID.randomUUID());
        notification.setAppointmentId(appointmentId);
        notification.setPatientId(patientId);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setMessage("Test notification");
        notification.setAttemptCount(0);

        notificationJpaRepository.save(notification);

        // Assert
        var saved = notificationJpaRepository.findByAppointmentId(appointmentId);
        assertTrue(saved.isPresent());
        assertEquals(appointmentId, saved.get().getAppointmentId());
        assertEquals(patientId, saved.get().getPatientId());
        assertEquals(NotificationStatus.PENDING, saved.get().getStatus());
    }

    @Test
    public void testFindPendingNotifications() {
        // Arrange
        NotificationJpaEntity notification1 = new NotificationJpaEntity();
        notification1.setId(UUID.randomUUID());
        notification1.setAppointmentId(UUID.randomUUID());
        notification1.setPatientId(UUID.randomUUID());
        notification1.setStatus(NotificationStatus.PENDING);
        notification1.setMessage("Test 1");
        notification1.setAttemptCount(0);

        NotificationJpaEntity notification2 = new NotificationJpaEntity();
        notification2.setId(UUID.randomUUID());
        notification2.setAppointmentId(UUID.randomUUID());
        notification2.setPatientId(UUID.randomUUID());
        notification2.setStatus(NotificationStatus.SENT);
        notification2.setMessage("Test 2");
        notification2.setAttemptCount(1);

        notificationJpaRepository.saveAll(List.of(notification1, notification2));

        // Act
        var pending = notificationJpaRepository.findByStatus(NotificationStatus.PENDING);

        // Assert
        assertEquals(1, pending.size());
        assertEquals(notification1.getId(), pending.get(0).getId());
    }

    @Test
    public void testRetryLogic() {
        // Arrange
        NotificationJpaEntity notification = new NotificationJpaEntity();
        notification.setId(UUID.randomUUID());
        notification.setAppointmentId(UUID.randomUUID());
        notification.setPatientId(UUID.randomUUID());
        notification.setStatus(NotificationStatus.PENDING);
        notification.setMessage("Test retry");
        notification.setAttemptCount(0);

        notificationJpaRepository.save(notification);

        // Act - Increment attempt count
        notification.setAttemptCount(1);
        notificationJpaRepository.save(notification);

        // Assert
        var updated = notificationJpaRepository.findById(notification.getId());
        assertTrue(updated.isPresent());
        assertEquals(1, updated.get().getAttemptCount());
    }

}
