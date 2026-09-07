package com.br.eprontuario.schedulingservice.domain.exception;

public class InvalidAppointmentDataException extends DomainException {
    public InvalidAppointmentDataException(String message) {
        super(message);
    }

    public InvalidAppointmentDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
