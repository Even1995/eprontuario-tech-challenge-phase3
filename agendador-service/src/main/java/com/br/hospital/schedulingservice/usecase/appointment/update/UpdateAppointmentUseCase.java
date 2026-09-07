package com.br.eprontuario.schedulingservice.usecase.appointment.update;

import com.br.hospital.schedulingservice.domain.entity.Appointment;
import com.br.hospital.schedulingservice.domain.exception.AppointmentNotFoundException;
import com.br.hospital.schedulingservice.port.in.appointment.AppointmentUseCase;
import com.br.hospital.schedulingservice.port.out.messaging.AppointmentEventPublisher;
import com.br.hospital.schedulingservice.port.out.persistence.AppointmentRepository;
import com.br.hospital.schedulingservice.util.validator.AppointmentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentEventPublisher eventPublisher;
    private final AppointmentValidator appointmentValidator;

    @Transactional
    public Appointment execute(UUID appointmentId, AppointmentUseCase.UpdateAppointmentCommand command) {
        log.info("Updating appointment ID: {}", appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: " + appointmentId));

        if (!appointment.isScheduled()) {
            throw new IllegalStateException("Cannot update appointment with status: " + appointment.getStatus());
        }

        // Validate new times
        appointmentValidator.validateAppointmentData(
                command.startAt(),
                command.endAt(),
                appointment.getPatientId(),
                command.doctorId()
        );

        // Update appointment
        appointment.update(
                command.doctorId(),
                command.nurseId(),
                command.startAt(),
                command.endAt(),
                command.notes(),
                command.updatedBy()
        );

        // Save to persistence
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment updated: {}", appointmentId);

        // Publish event
        try {
            eventPublisher.publishAppointmentUpdated(updatedAppointment);
            log.info("Appointment update event published for ID: {}", appointmentId);
        } catch (Exception e) {
            log.error("Failed to publish appointment update event, but appointment was saved", e);
        }

        return updatedAppointment;
    }
}
