package com.br.eprontuario.schedulingservice.port.out.messaging;

import com.br.eprontuario.schedulingservice.domain.entity.Appointment;

public interface AppointmentEventPublisher {
    void publishAppointmentCreated(Appointment appointment);
    void publishAppointmentUpdated(Appointment appointment);
}
