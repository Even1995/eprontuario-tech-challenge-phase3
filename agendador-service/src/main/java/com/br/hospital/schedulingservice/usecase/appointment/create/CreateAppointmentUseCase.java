package com.br.eprontuario.schedulingservice.usecase.appointment.create;

import com.br.eprontuario.schedulingservice.domain.entity.Appointment;
import com.br.eprontuario.schedulingservice.domain.exception.InvalidAppointmentDataException;
import com.br.eprontuario.schedulingservice.port.in.appointment.AppointmentUseCase;
import com.br.hospital.schedulingservice.port.out.messaging.AppointmentEventPublisher;
import com.br.hospital.schedulingservice.port.out.persistence.AppointmentRepository;
import com.br.hospital.schedulingservice.util.validator.AppointmentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentEventPublisher eventPublisher;
    private final AppointmentValidator appointmentValidator;

    @Transactional
    public Appointment execute(AppointmentUseCase.CreateAppointmentCommand command) {
        log.info("Creating appointment for patient: {} with doctor: {}", command.patientId(), command.doctorId());

        // Validate appointment data
        appointmentValidator.validateAppointmentData(
                command.startAt(),
                command.endAt(),
                command.patientId(),
                command.doctorId()
        );

        // Create appointment entity
        Appointment appointment = Appointment.create(
                command.patientId(),
                command.doctorId(),
                command.nurseId(),
                command.startAt(),
                command.endAt(),
                command.notes(),
                command.createdBy()
        );

        // Save to persistence
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment created with ID: {}", savedAppointment.getId());

        // Publish event
        try {
            eventPublisher.publishAppointmentCreated(savedAppointment);
            log.info("Appointment creation event published for ID: {}", savedAppointment.getId());
        } catch (Exception e) {
            log.error("Failed to publish appointment creation event, but appointment was saved", e);
            // Don't fail the entire operation if event publishing fails
        }

        return savedAppointment;
    }
}
