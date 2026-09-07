package com.br.historico.service.adapter.out.jpa.repository;

import com.br.historico.service.adapter.out.jpa.entity.AppointmentGraphQLEntity;
import com.br.historico.service.domain.entity.AgendamentoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HistoryRepository {
    
    private final AppointmentJpaRepository appointmentJpaRepository;
    
    public Optional<AppointmentGraphQLEntity> findAppointmentById(String id) {
        return appointmentJpaRepository.findById(id);
    }
    
    public Page<AppointmentGraphQLEntity> findPatientAppointments(String patientId, Pageable pageable) {
        return appointmentJpaRepository.findByPatientId(patientId, pageable);
    }
    
    public Page<AppointmentGraphQLEntity> findFuturePatientAppointments(String patientId, Pageable pageable) {
        return appointmentJpaRepository.findFutureAppointmentsByPatientId(patientId, pageable);
    }
    
    public Page<AppointmentGraphQLEntity> findDoctorAppointments(String doctorId, Pageable pageable) {
        return appointmentJpaRepository.findByDoctorId(doctorId, pageable);
    }
    
    public Page<AppointmentGraphQLEntity> findByStatus(AgendamentoStatus status, Pageable pageable) {
        return appointmentJpaRepository.findByStatus(status, pageable);
    }
    
    public List<AppointmentGraphQLEntity> findPatientAppointmentsByStatus(String patientId, AgendamentoStatus status) {
        return appointmentJpaRepository.findByPatientIdAndStatus(patientId, status);
    }
    
    public List<AppointmentGraphQLEntity> findAppointmentsByDateRange(LocalDateTime startFrom, LocalDateTime endTo) {
        return appointmentJpaRepository.findByDateRange(startFrom, endTo);
    }
    
    public Page<AppointmentGraphQLEntity> findAppointmentsWithFilter(
            String patientId, 
            AgendamentoStatus status,
            LocalDateTime startDateFrom,
            LocalDateTime startDateTo,
            Pageable pageable) {
        
        List<AppointmentGraphQLEntity> all = appointmentJpaRepository.findAll();
        
        List<AppointmentGraphQLEntity> filtered = all.stream()
            .filter(a -> patientId == null || a.getPatientId().equals(patientId))
            .filter(a -> status == null || a.getStatus() == status)
            .filter(a -> startDateFrom == null || a.getStartAt().isAfter(startDateFrom) || a.getStartAt().isEqual(startDateFrom))
            .filter(a -> startDateTo == null || a.getStartAt().isBefore(startDateTo) || a.getStartAt().isEqual(startDateTo))
            .toList();
        
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        
        List<AppointmentGraphQLEntity> paged = filtered.subList(start, end);
        
        return new PageImpl<>(paged, pageable, filtered.size());
    }
    
    public AppointmentGraphQLEntity saveAppointment(AppointmentGraphQLEntity appointment) {
        return appointmentJpaRepository.save(appointment);
    }
    
    public void deleteAppointment(String id) {
        appointmentJpaRepository.deleteById(id);
    }
    
    public long countByStatus(AgendamentoStatus status) {
        return appointmentJpaRepository.countByStatus(status);
    }
    
    public long getTotalCount() {
        return appointmentJpaRepository.count();
    }
}
