package com.br.eprontuario.schedulingservice.adapter.out.jpa.repository;

import com.br.hospital.schedulingservice.adapter.out.jpa.entity.AppointmentJpaEntity;
import com.br.hospital.schedulingservice.adapter.out.jpa.mapper.AppointmentJpaMapper;
import com.br.hospital.schedulingservice.domain.entity.Appointment;
import com.br.hospital.schedulingservice.port.out.persistence.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AppointmentRepositoryImpl implements AppointmentRepository {

    private final AppointmentJpaRepository jpaRepository;
    private final AppointmentJpaMapper mapper;

    @Override
    public Appointment save(Appointment appointment) {
        AppointmentJpaEntity jpaEntity = mapper.toJpaEntity(appointment);
        AppointmentJpaEntity saved = jpaRepository.save(jpaEntity);
        return mapper.toDomainEntity(saved);
    }

    @Override
    public Optional<Appointment> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomainEntity);
    }

    @Override
    public List<Appointment> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientId(patientId).stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    @Override
    public List<Appointment> findByDoctorId(UUID doctorId) {
        return jpaRepository.findByDoctorId(doctorId).stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}
