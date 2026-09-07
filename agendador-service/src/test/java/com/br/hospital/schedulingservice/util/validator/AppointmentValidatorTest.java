package com.br.hospital.schedulingservice.util.validator;

import com.br.hospital.schedulingservice.domain.exception.InvalidAppointmentDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentValidatorTest {

    private AppointmentValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AppointmentValidator();
    }

    @Test
    void testValidAppointmentData() {
        // Arrange
        Instant startAt = Instant.now().plusSeconds(3600);
        Instant endAt = startAt.plusSeconds(1800);
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        // Act & Assert
        assertDoesNotThrow(() -> validator.validateAppointmentData(startAt, endAt, patientId, doctorId));
    }

    @Test
    void testNullStartAt() {
        // Arrange
        Instant endAt = Instant.now().plusSeconds(5400);
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        // Act & Assert
        assertThrows(InvalidAppointmentDataException.class,
                () -> validator.validateAppointmentData(null, endAt, patientId, doctorId));
    }

    @Test
    void testNullEndAt() {
        // Arrange
        Instant startAt = Instant.now().plusSeconds(3600);
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        // Act & Assert
        assertThrows(InvalidAppointmentDataException.class,
                () -> validator.validateAppointmentData(startAt, null, patientId, doctorId));
    }

    @Test
    void testEndBeforeStart() {
        // Arrange
        Instant startAt = Instant.now().plusSeconds(3600);
        Instant endAt = startAt.minusSeconds(1800);
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        // Act & Assert
        assertThrows(InvalidAppointmentDataException.class,
                () -> validator.validateAppointmentData(startAt, endAt, patientId, doctorId));
    }

    @Test
    void testDurationTooShort() {
        // Arrange
        Instant startAt = Instant.now().plusSeconds(3600);
        Instant endAt = startAt.plusSeconds(60); // Only 1 minute
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        // Act & Assert
        assertThrows(InvalidAppointmentDataException.class,
                () -> validator.validateAppointmentData(startAt, endAt, patientId, doctorId));
    }

    @Test
    void testDurationTooLong() {
        // Arrange
        Instant startAt = Instant.now().plusSeconds(3600);
        Instant endAt = startAt.plusSeconds(36000); // 10 hours
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        // Act & Assert
        assertThrows(InvalidAppointmentDataException.class,
                () -> validator.validateAppointmentData(startAt, endAt, patientId, doctorId));
    }

    @Test
    void testPastAppointment() {
        // Arrange
        Instant startAt = Instant.now().minusSeconds(3600); // 1 hour ago
        Instant endAt = startAt.plusSeconds(1800);
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        // Act & Assert
        assertThrows(InvalidAppointmentDataException.class,
                () -> validator.validateAppointmentData(startAt, endAt, patientId, doctorId));
    }

    @Test
    void testNullPatientId() {
        // Arrange
        Instant startAt = Instant.now().plusSeconds(3600);
        Instant endAt = startAt.plusSeconds(1800);
        UUID doctorId = UUID.randomUUID();

        // Act & Assert
        assertThrows(InvalidAppointmentDataException.class,
                () -> validator.validateAppointmentData(startAt, endAt, null, doctorId));
    }

    @Test
    void testNullDoctorId() {
        // Arrange
        Instant startAt = Instant.now().plusSeconds(3600);
        Instant endAt = startAt.plusSeconds(1800);
        UUID patientId = UUID.randomUUID();

        // Act & Assert
        assertThrows(InvalidAppointmentDataException.class,
                () -> validator.validateAppointmentData(startAt, endAt, patientId, null));
    }
}
