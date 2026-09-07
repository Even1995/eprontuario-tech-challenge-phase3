package com.br.hospital.notificationsservice.adapter.out.jpa.repository;

import com.br.hospital.notificationsservice.adapter.out.jpa.entity.NotificationJpaEntity;
import com.br.hospital.notificationsservice.domain.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, UUID> {

    Optional<NotificationJpaEntity> findByAppointmentId(UUID appointmentId);

    List<NotificationJpaEntity> findByPatientId(UUID patientId);

    List<NotificationJpaEntity> findByStatus(NotificationStatus status);

    List<NotificationJpaEntity> findByStatusAndAttemptCountLessThan(NotificationStatus status, Integer maxAttempts);

}
