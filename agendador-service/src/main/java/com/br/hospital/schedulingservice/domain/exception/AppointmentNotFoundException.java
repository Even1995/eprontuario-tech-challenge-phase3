package com.br.eprontuario.schedulingservice.domain.exception;

public class AppointmentNotFoundException extends DomainException {
    public AppointmentNotFoundException(String message) {
        super(message);
    }

    public AppointmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
