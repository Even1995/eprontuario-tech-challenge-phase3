package com.br.eprontuario.schedulingservice.util.validator;

import com.br.eprontuario.schedulingservice.domain.exception.InvalidAppointmentDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
public class AppointmentValidator {

    private static final long MINIMUM_APPOINTMENT_MINUTES = 5;
    private static final long MAXIMUM_APPOINTMENT_HOURS = 8;

    public void validateAppointmentData(Instant startAt, Instant endAt, UUID patientId, UUID doctorId) {
        validateNotNull(startAt, "Start time cannot be null");
        validateNotNull(endAt, "End time cannot be null");
        validateNotNull(patientId, "Patient ID cannot be null");
        validateNotNull(doctorId, "Doctor ID cannot be null");

        validateStartBeforeEnd(startAt, endAt);
        validateMinimumDuration(startAt, endAt);
        validateMaximumDuration(startAt, endAt);
        validateFutureAppointment(startAt);
    }

    private void validateNotNull(Object value, String message) {
        if (value == null) {
            log.warn("Validation failed: {}", message);
            throw new InvalidAppointmentDataException(message);
        }
    }

    private void validateStartBeforeEnd(Instant startAt, Instant endAt) {
        if (!startAt.isBefore(endAt)) {
            String message = "Start time must be before end time";
            log.warn("Validation failed: {}", message);
            throw new InvalidAppointmentDataException(message);
        }
    }

    private void validateMinimumDuration(Instant startAt, Instant endAt) {
        long durationMinutes = java.time.temporal.ChronoUnit.MINUTES.between(startAt, endAt);
        if (durationMinutes < MINIMUM_APPOINTMENT_MINUTES) {
            String message = "Appointment must be at least " + MINIMUM_APPOINTMENT_MINUTES + " minutes";
            log.warn("Validation failed: {}", message);
            throw new InvalidAppointmentDataException(message);
        }
    }

    private void validateMaximumDuration(Instant startAt, Instant endAt) {
        long durationHours = java.time.temporal.ChronoUnit.HOURS.between(startAt, endAt);
        if (durationHours > MAXIMUM_APPOINTMENT_HOURS) {
            String message = "Appointment cannot exceed " + MAXIMUM_APPOINTMENT_HOURS + " hours";
            log.warn("Validation failed: {}", message);
            throw new InvalidAppointmentDataException(message);
        }
    }

    private void validateFutureAppointment(Instant startAt) {
        if (startAt.isBefore(Instant.now())) {
            String message = "Appointment start time must be in the future";
            log.warn("Validation failed: {}", message);
            throw new InvalidAppointmentDataException(message);
        }
    }
}
