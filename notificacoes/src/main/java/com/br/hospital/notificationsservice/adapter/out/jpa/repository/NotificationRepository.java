package com.br.hospital.notificationsservice.adapter.out.jpa.repository;

import com.br.hospital.notificationsservice.adapter.out.jpa.entity.NotificationJpaEntity;
import com.br.hospital.notificationsservice.domain.entity.Notification;
import com.br.hospital.notificationsservice.domain.entity.NotificationStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class NotificationRepository {

    private final NotificationJpaRepository jpaRepository;

    public NotificationRepository(NotificationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public Notification save(Notification notification) {
        NotificationJpaEntity entity = toJpaEntity(notification);
        NotificationJpaEntity saved = jpaRepository.save(entity);
        return toDomainEntity(saved);
    }

    public Optional<Notification> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomainEntity);
    }

    public Optional<Notification> findByAppointmentId(UUID appointmentId) {
        return jpaRepository.findByAppointmentId(appointmentId).map(this::toDomainEntity);
    }

    public List<Notification> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientId(patientId)
                .stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    public List<Notification> findByStatus(NotificationStatus status) {
        return jpaRepository.findByStatus(status)
                .stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    public List<Notification> findPendingWithRetriesAvailable(int maxAttempts) {
        return jpaRepository.findByStatusAndAttemptCountLessThan(NotificationStatus.PENDING, maxAttempts)
                .stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    public List<Notification> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    private NotificationJpaEntity toJpaEntity(Notification notification) {
        return new NotificationJpaEntity(
                notification.getId(),
                notification.getAppointmentId(),
                notification.getPatientId(),
                notification.getStatus(),
                notification.getMessage(),
                notification.getAttemptCount(),
                notification.getLastError(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }

    private Notification toDomainEntity(NotificationJpaEntity jpaEntity) {
        Notification notification = new Notification();
        notification.setId(jpaEntity.getId());
        notification.setAppointmentId(jpaEntity.getAppointmentId());
        notification.setPatientId(jpaEntity.getPatientId());
        notification.setStatus(jpaEntity.getStatus());
        notification.setMessage(jpaEntity.getMessage());
        notification.setAttemptCount(jpaEntity.getAttemptCount());
        notification.setLastError(jpaEntity.getLastError());
        notification.setCreatedAt(jpaEntity.getCreatedAt());
        notification.setUpdatedAt(jpaEntity.getUpdatedAt());
        return notification;
    }

}
