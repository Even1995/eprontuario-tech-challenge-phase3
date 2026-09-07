package com.br.eprontuario.schedulingservice.adapter.out.jpa.mapper;

import com.br.eprontuario.schedulingservice.adapter.out.jpa.entity.AppointmentJpaEntity;
import com.br.eprontuario.schedulingservice.domain.entity.Appointment;
import com.br.eprontuario.schedulingservice.domain.valueobject.AppointmentMetadata;
import org.springframework.stereotype.Component;

@Component
public class AppointmentJpaMapper {

    public AppointmentJpaEntity toJpaEntity(Appointment appointment) {
        return AppointmentJpaEntity.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .nurseId(appointment.getNurseId())
                .startAt(appointment.getStartAt())
                .endAt(appointment.getEndAt())
                .status(AppointmentJpaEntity.Status.valueOf(appointment.getStatus().name()))
                .notes(appointment.getNotes())
                .createdBy(appointment.getMetadata().getCreatedBy())
                .createdAt(appointment.getMetadata().getCreatedAt())
                .updatedBy(appointment.getMetadata().getUpdatedBy())
                .updatedAt(appointment.getMetadata().getUpdatedAt())
                .build();
    }

    public Appointment toDomainEntity(AppointmentJpaEntity jpaEntity) {
        AppointmentMetadata metadata = new AppointmentMetadata(
                jpaEntity.getCreatedBy(),
                jpaEntity.getCreatedAt(),
                jpaEntity.getUpdatedBy(),
                jpaEntity.getUpdatedAt()
        );

        return Appointment.builder()
                .id(jpaEntity.getId())
                .patientId(jpaEntity.getPatientId())
                .doctorId(jpaEntity.getDoctorId())
                .nurseId(jpaEntity.getNurseId())
                .startAt(jpaEntity.getStartAt())
                .endAt(jpaEntity.getEndAt())
                .status(Appointment.Status.valueOf(jpaEntity.getStatus().name()))
                .notes(jpaEntity.getNotes())
                .metadata(metadata)
                .build();
    }
}
