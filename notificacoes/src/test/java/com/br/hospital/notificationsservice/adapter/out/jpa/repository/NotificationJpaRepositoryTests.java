package com.br.hospital.notificationsservice.adapter.out.jpa.repository;

import com.br.hospital.notificationsservice.adapter.out.jpa.repository.NotificationJpaRepository;
import com.br.hospital.notificationsservice.adapter.out.jpa.entity.NotificationJpaEntity;
import com.br.hospital.notificationsservice.domain.entity.NotificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class NotificationJpaRepositoryTests {

    @Autowired
    private NotificationJpaRepository repository;

    private NotificationJpaEntity testNotification;

    @BeforeEach
    public void setUp() {
        testNotification = new NotificationJpaEntity();
        testNotification.setId(UUID.randomUUID());
        testNotification.setAppointmentId(UUID.randomUUID());
        testNotification.setPatientId(UUID.randomUUID());
        testNotification.setStatus(NotificationStatus.PENDING);
        testNotification.setMessage("Test message");
        testNotification.setAttemptCount(0);
    }

    @Test
    public void testSaveNotification() {
        // Act
        NotificationJpaEntity saved = repository.save(testNotification);

        // Assert
        assertNotNull(saved);
        assertEquals(testNotification.getId(), saved.getId());
        assertEquals(testNotification.getMessage(), saved.getMessage());
    }

    @Test
    public void testFindByAppointmentId() {
        // Arrange
        repository.save(testNotification);

        // Act
        var found = repository.findByAppointmentId(testNotification.getAppointmentId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(testNotification.getId(), found.get().getId());
    }

    @Test
    public void testFindByStatus() {
        // Arrange
        repository.save(testNotification);

        // Act
        List<NotificationJpaEntity> found = repository.findByStatus(NotificationStatus.PENDING);

        // Assert
        assertTrue(found.stream().anyMatch(n -> n.getId().equals(testNotification.getId())));
    }

    @Test
    public void testFindByStatusAndAttemptCountLessThan() {
        // Arrange
        testNotification.setAttemptCount(1);
        repository.save(testNotification);

        // Act
        List<NotificationJpaEntity> found = repository.findByStatusAndAttemptCountLessThan(
                NotificationStatus.PENDING, 3);

        // Assert
        assertTrue(found.stream().anyMatch(n -> n.getId().equals(testNotification.getId())));
    }

    @Test
    public void testUpdateNotification() {
        // Arrange
        repository.save(testNotification);

        // Act
        testNotification.setStatus(NotificationStatus.SENT);
        testNotification.setAttemptCount(1);
        repository.save(testNotification);

        // Assert
        var updated = repository.findById(testNotification.getId());
        assertTrue(updated.isPresent());
        assertEquals(NotificationStatus.SENT, updated.get().getStatus());
        assertEquals(1, updated.get().getAttemptCount());
    }

}
