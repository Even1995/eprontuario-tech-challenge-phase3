package com.br.historico.service.adapter.in.graphql.resolver;

import com.br.historico.service.adapter.out.jpa.entity.AppointmentGraphQLEntity;
import com.br.historico.service.adapter.out.jpa.repository.HistoryRepository;
import com.br.historico.service.domain.entity.AgendamentoStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AppointmentResolver {
    
    private final HistoryRepository historyRepository;
    
    @QueryMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'PATIENT', 'ADMIN')")
    public Map<String, Object> appointmentById(@Argument String id) {
        log.info("Fetching appointment by id: {}", id);
        
        Optional<AppointmentGraphQLEntity> appointment = historyRepository.findAppointmentById(id);
        
        if (appointment.isPresent()) {
            return entityToGraphQLMap(appointment.get());
        }
        
        log.warn("Appointment not found: {}", id);
        return null;
    }
    
    @QueryMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'PATIENT', 'ADMIN')")
    public Map<String, Object> patientAppointments(
            @Argument String patientId,
            @Argument Boolean futureOnly,
            @Argument Integer limit,
            @Argument Integer offset) {
        
        log.info("Fetching appointments for patient: {}, futureOnly: {}, limit: {}, offset: {}", 
                 patientId, futureOnly, limit, offset);
        
        int pageSize = limit != null ? limit : 10;
        int pageOffset = offset != null ? offset : 0;
        Pageable pageable = PageRequest.of(pageOffset / pageSize, pageSize);
        
        Page<AppointmentGraphQLEntity> appointmentsPage;
        if (Boolean.TRUE.equals(futureOnly)) {
            appointmentsPage = historyRepository.findFuturePatientAppointments(patientId, pageable);
        } else {
            appointmentsPage = historyRepository.findPatientAppointments(patientId, pageable);
        }
        
        return buildConnectionMap(appointmentsPage, pageSize);
    }
    
    @QueryMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public Map<String, Object> appointmentsByDoctor(
            @Argument String doctorId,
            @Argument Integer limit,
            @Argument Integer offset) {
        
        log.info("Fetching appointments for doctor: {}, limit: {}, offset: {}", doctorId, limit, offset);
        
        int pageSize = limit != null ? limit : 10;
        int pageOffset = offset != null ? offset : 0;
        Pageable pageable = PageRequest.of(pageOffset / pageSize, pageSize);
        
        Page<AppointmentGraphQLEntity> appointmentsPage = historyRepository.findDoctorAppointments(doctorId, pageable);
        
        return buildConnectionMap(appointmentsPage, pageSize);
    }
    
    @QueryMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'ADMIN')")
    public Map<String, Object> appointmentsByStatus(
            @Argument AgendamentoStatus status,
            @Argument Integer limit,
            @Argument Integer offset) {
        
        log.info("Fetching appointments by status: {}, limit: {}, offset: {}", status, limit, offset);
        
        int pageSize = limit != null ? limit : 10;
        int pageOffset = offset != null ? offset : 0;
        Pageable pageable = PageRequest.of(pageOffset / pageSize, pageSize);
        
        Page<AppointmentGraphQLEntity> appointmentsPage = historyRepository.findByStatus(status, pageable);
        
        return buildConnectionMap(appointmentsPage, pageSize);
    }
    
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Map<String, Object> appointmentStats() {
        log.info("Fetching appointment statistics");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAppointments", historyRepository.getTotalCount());
        stats.put("completedAppointments", historyRepository.countByStatus(AgendamentoStatus.COMPLETO));
        stats.put("cancelledAppointments", historyRepository.countByStatus(AgendamentoStatus.CANCELAD0));
        stats.put("noShowAppointments", historyRepository.countByStatus(AgendamentoStatus.NAO_COMPARECEU));
        stats.put("scheduledAppointments", historyRepository.countByStatus(AgendamentoStatus.AGENDADO));
        
        return stats;
    }
    
    private Map<String, Object> buildConnectionMap(Page<AppointmentGraphQLEntity> page, int pageSize) {
        Map<String, Object> connection = new HashMap<>();
        
        var edges = page.getContent().stream()
            .map(apt -> {
                Map<String, Object> edge = new HashMap<>();
                edge.put("node", entityToGraphQLMap(apt));
                edge.put("cursor", apt.getId());
                return edge;
            })
            .toList();
        
        connection.put("edges", edges);
        
        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("hasNextPage", page.hasNext());
        pageInfo.put("hasPreviousPage", page.hasPrevious());
        pageInfo.put("totalCount", page.getTotalElements());
        pageInfo.put("pageSize", pageSize);
        pageInfo.put("currentPage", page.getNumber());
        
        connection.put("pageInfo", pageInfo);
        
        return connection;
    }
    
    private Map<String, Object> entityToGraphQLMap(AppointmentGraphQLEntity entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", entity.getId());
        map.put("startAt", entity.getStartAt());
        map.put("endAt", entity.getEndAt());
        map.put("status", entity.getStatus());
        map.put("notes", entity.getNotes());
        map.put("doctorId", entity.getDoctorId());
        map.put("nurseId", entity.getNurseId());
        map.put("patientId", entity.getPatientId());
        map.put("createdAt", entity.getCreatedAt());
        map.put("updatedAt", entity.getUpdatedAt());
        return map;
    }
}
