package com.br.historico.service.adapter.out.jpa.repository;

import com.br.historico.service.adapter.out.jpa.entity.AppointmentGraphQLEntity;
import com.br.historico.service.domain.entity.AgendamentoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentJpaRepository extends JpaRepository<AppointmentGraphQLEntity, String> {
    
    Optional<AppointmentGraphQLEntity> findById(String id);
    
    Page<AppointmentGraphQLEntity> findByPatientId(String patientId, Pageable pageable);
    
    @Query("SELECT a FROM AppointmentGraphQLEntity a WHERE a.patientId = :patientId AND a.startAt >= CURRENT_TIMESTAMP ORDER BY a.startAt ASC")
    Page<AppointmentGraphQLEntity> findFutureAppointmentsByPatientId(
        @Param("patientId") String patientId,
        Pageable pageable
    );
    
    Page<AppointmentGraphQLEntity> findByDoctorId(String doctorId, Pageable pageable);
    
    Page<AppointmentGraphQLEntity> findByStatus(AgendamentoStatus status, Pageable pageable);
    
    @Query("SELECT a FROM AppointmentGraphQLEntity a WHERE a.patientId = :patientId AND a.status = :status")
    List<AppointmentGraphQLEntity> findByPatientIdAndStatus(
        @Param("patientId") String patientId,
        @Param("status") AgendamentoStatus status
    );
    
    @Query("SELECT a FROM AppointmentGraphQLEntity a WHERE a.startAt >= :startFrom AND a.startAt <= :startTo ORDER BY a.startAt ASC")
    List<AppointmentGraphQLEntity> findByDateRange(
        @Param("startFrom") LocalDateTime startFrom,
        @Param("startTo") LocalDateTime startTo
    );
    
    @Query("SELECT COUNT(a) FROM AppointmentGraphQLEntity a WHERE a.status = :status")
    long countByStatus(@Param("status") AgendamentoStatus status);
}
