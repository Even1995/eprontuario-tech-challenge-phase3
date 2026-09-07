package com.br.hospital.schedulingservice.usecase.appointment.create;

import com.br.hospital.schedulingservice.domain.entity.Appointment;
import com.br.hospital.schedulingservice.domain.exception.InvalidAppointmentDataException;
import com.br.hospital.schedulingservice.port.in.appointment.AppointmentUseCase;
import com.br.hospital.schedulingservice.port.out.messaging.AppointmentEventPublisher;
import com.br.hospital.schedulingservice.port.out.persistence.AppointmentRepository;
import com.br.hospital.schedulingservice.util.validator.AppointmentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAppointmentUseCaseTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentEventPublisher eventPublisher;

    @Mock
    private AppointmentValidator appointmentValidator;

    private CreateAppointmentUseCase useCase;

    private UUID patientId;
    private UUID doctorId;
    private UUID nurseId;
    private UUID userId;
    private Instant startAt;
    private Instant endAt;

    @BeforeEach
    void setUp() {
        useCase = new CreateAppointmentUseCase(appointmentRepository, eventPublisher, appointmentValidator);

        patientId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        nurseId = UUID.randomUUID();
        userId = UUID.randomUUID();
        startAt = Instant.now().plusSeconds(3600);
        endAt = startAt.plusSeconds(1800);
    }

    @Test
    void testCreateAppointmentSuccessfully() {
        // Arrange
        AppointmentUseCase.CreateAppointmentCommand command = new AppointmentUseCase.CreateAppointmentCommand(
                patientId, doctorId, nurseId, startAt, endAt, "Regular checkup", userId
        );

        Appointment savedAppointment = Appointment.create(
                patientId, doctorId, nurseId, startAt, endAt, "Regular checkup", userId
        );

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);
        doNothing().when(eventPublisher).publishAppointmentCreated(any(Appointment.class));

        // Act
        Appointment result = useCase.execute(command);

        // Assert
        assertNotNull(result);
        assertEquals(patientId, result.getPatientId());
        assertEquals(doctorId, result.getDoctorId());
        assertEquals(Appointment.Status.SCHEDULED, result.getStatus());

        verify(appointmentValidator).validateAppointmentData(startAt, endAt, patientId, doctorId);
        verify(appointmentRepository).save(any(Appointment.class));
        verify(eventPublisher).publishAppointmentCreated(any(Appointment.class));
    }

    @Test
    void testCreateAppointmentWithInvalidData() {
        // Arrange
        AppointmentUseCase.CreateAppointmentCommand command = new AppointmentUseCase.CreateAppointmentCommand(
                patientId, doctorId, nurseId, startAt, endAt, "Regular checkup", userId
        );

        doThrow(new InvalidAppointmentDataException("Invalid appointment data"))
                .when(appointmentValidator).validateAppointmentData(startAt, endAt, patientId, doctorId);

        // Act & Assert
        assertThrows(InvalidAppointmentDataException.class, () -> useCase.execute(command));
        verify(appointmentRepository, never()).save(any());
        verify(eventPublisher, never()).publishAppointmentCreated(any());
    }
}
