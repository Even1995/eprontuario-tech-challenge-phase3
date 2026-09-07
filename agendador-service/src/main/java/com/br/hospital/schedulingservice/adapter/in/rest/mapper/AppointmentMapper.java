package com.br.eprontuario.schedulingservice.adapter.in.rest.mapper;

import com.br.eprontuario.schedulingservice.adapter.in.rest.dto.AppointmentResponse;
import com.br.eprontuario.schedulingservice.adapter.in.rest.dto.CreateAppointmentRequest;
import com.br.eprontuario.schedulingservice.domain.entity.Appointment;
import com.br.eprontuario.schedulingservice.port.in.appointment.AppointmentUseCase;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AppointmentMapper {

    public AppointmentUseCase.CreateAppointmentCommand toCreateCommand(CreateAppointmentRequest request, UUID createdBy) {
        return new AppointmentUseCase.CreateAppointmentCommand(
                request.getPatientId(),
                request.getDoctorId(),
                request.getNurseId(),
                request.getStartAt(),
                request.getEndAt(),
                request.getNotes(),
                createdBy
        );
    }

    public AppointmentResponse toResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .nurseId(appointment.getNurseId())
                .startAt(appointment.getStartAt())
                .endAt(appointment.getEndAt())
                .status(appointment.getStatus().name())
                .notes(appointment.getNotes())
                .createdBy(appointment.getMetadata().getCreatedBy())
                .createdAt(appointment.getMetadata().getCreatedAt())
                .updatedBy(appointment.getMetadata().getUpdatedBy())
                .updatedAt(appointment.getMetadata().getUpdatedAt())
                .build();
    }
}
