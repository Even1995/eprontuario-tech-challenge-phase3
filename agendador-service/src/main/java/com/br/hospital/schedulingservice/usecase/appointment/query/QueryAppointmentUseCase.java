package com.br.eprontuario.schedulingservice.usecase.appointment.query;

import com.br.eprontuario.schedulingservice.domain.entity.Appointment;
import com.br.eprontuario.schedulingservice.domain.exception.AppointmentNotFoundException;
import com.br.eprontuario.schedulingservice.port.out.persistence.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueryAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;

    @Transactional(readOnly = true)
    public Appointment getAppointmentById(UUID id) {
        log.debug("Fetching appointment ID: {}", id);
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Appointment> listPatientAppointments(UUID patientId) {
        log.debug("Fetching appointments for patient ID: {}", patientId);
        return appointmentRepository.findByPatientId(patientId);
    }

    @Transactional(readOnly = true)
    public List<Appointment> listDoctorAppointments(UUID doctorId) {
        log.debug("Fetching appointments for doctor ID: {}", doctorId);
        return appointmentRepository.findByDoctorId(doctorId);
    }
}
