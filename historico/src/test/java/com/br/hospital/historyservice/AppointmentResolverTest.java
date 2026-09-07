package com.br.hospital.historyservice;

import com.br.historico.service.adapter.out.jpa.entity.AppointmentGraphQLEntity;
import com.br.historico.service.adapter.out.jpa.repository.HistoryRepository;
import com.br.historico.service.domain.entity.AgendamentoStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppointmentResolverTest {
    
    @Mock
    private HistoryRepository historyRepository;
    
    @InjectMocks
    private AppointmentResolver appointmentResolver;
    
    private AppointmentGraphQLEntity testAppointment;
    
    @BeforeEach
    public void setUp() {
        testAppointment = AppointmentGraphQLEntity.builder()
            .id("apt-1")
            .startAt(LocalDateTime.now().plusDays(1))
            .endAt(LocalDateTime.now().plusDays(1).plusHours(1))
            .status(AgendamentoStatus.AGENDADO)
            .notes("Routine checkup")
            .doctorId("doc-1")
            .nurseId("nurse-1")
            .patientId("patient-1")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    @Test
    public void testAppointmentById() {
        when(historyRepository.findAppointmentById("apt-1"))
            .thenReturn(Optional.of(testAppointment));
        
        Map<String, Object> result = appointmentResolver.appointmentById("apt-1");
        
        assertNotNull(result);
        assertEquals("apt-1", result.get("id"));
        assertEquals("patient-1", result.get("patientId"));
    }
    
    @Test
    public void testAppointmentByIdNotFound() {
        when(historyRepository.findAppointmentById("apt-999"))
            .thenReturn(Optional.empty());
        
        Map<String, Object> result = appointmentResolver.appointmentById("apt-999");
        
        assertNull(result);
    }
    
    @Test
    public void testPatientAppointments() {
        Page<AppointmentGraphQLEntity> page = new PageImpl<>(
            List.of(testAppointment),
            PageRequest.of(0, 10),
            1
        );
        
        when(historyRepository.findPatientAppointments("patient-1", PageRequest.of(0, 10)))
            .thenReturn(page);
        
        Map<String, Object> result = appointmentResolver.patientAppointments(
            "patient-1", false, 10, 0
        );
        
        assertNotNull(result);
        assertTrue(result.containsKey("edges"));
        assertTrue(result.containsKey("pageInfo"));
    }
    
    @Test
    public void testAppointmentStats() {
        when(historyRepository.getTotalCount()).thenReturn(100L);
        when(historyRepository.countByStatus(AgendamentoStatus.COMPLETO)).thenReturn(80L);
        when(historyRepository.countByStatus(AgendamentoStatus.CANCELAD0)).thenReturn(10L);
        when(historyRepository.countByStatus(AgendamentoStatus.NAO_COMPARECEU)).thenReturn(5L);
        when(historyRepository.countByStatus(AgendamentoStatus.AGENDADO)).thenReturn(5L);
        
        Map<String, Object> result = appointmentResolver.appointmentStats();
        
        assertNotNull(result);
        assertEquals(100L, result.get("totalAppointments"));
        assertEquals(80L, result.get("completedAppointments"));
    }
}
