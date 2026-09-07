package com.br.eprontuario.schedulingservice.port.in.appointment;

import com.br.eprontuario.schedulingservice.domain.entity.Appointment;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AppointmentUseCase {
    Appointment createAppointment(CreateAppointmentCommand command);
    Appointment updateAppointment(UUID appointmentId, UpdateAppointmentCommand command);
    List<Appointment> listPatientAppointments(UUID patientId);
    List<Appointment> listDoctorAppointments(UUID doctorId);
    Appointment getAppointmentById(UUID id);

    record CreateAppointmentCommand(
        UUID patientId,
        UUID doctorId,
        UUID nurseId,
        Instant startAt,
        Instant endAt,
        String notes,
        UUID createdBy
    ) {}

    record UpdateAppointmentCommand(
        UUID doctorId,
        UUID nurseId,
        Instant startAt,
        Instant endAt,
        String notes,
        UUID updatedBy
    ) {}
}
