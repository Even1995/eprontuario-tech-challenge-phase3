package com.br.eprontuario.schedulingservice.port.out.persistence;

import com.br.eprontuario.schedulingservice.domain.entity.Appointment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository {
    Appointment save(Appointment appointment);
    Optional<Appointment> findById(UUID id);
    List<Appointment> findByPatientId(UUID patientId);
    List<Appointment> findByDoctorId(UUID doctorId);
    void delete(UUID id);
    boolean existsById(UUID id);
}
