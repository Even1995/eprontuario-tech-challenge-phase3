package com.br.eprontuario.schedulingservice.adapter.out.jpa.repository;

import com.br.eprontuario.schedulingservice.adapter.out.jpa.entity.AppointmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentJpaRepository extends JpaRepository<AppointmentJpaEntity, UUID> {
    List<AppointmentJpaEntity> findByPatientId(UUID patientId);
    List<AppointmentJpaEntity> findByDoctorId(UUID doctorId);
}
