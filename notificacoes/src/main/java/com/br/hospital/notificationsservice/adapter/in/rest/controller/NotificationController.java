package com.br.hospital.notificationsservice.adapter.in.rest.controller;

import com.br.hospital.notificationsservice.adapter.out.jpa.repository.NotificationJpaRepository;
import com.br.hospital.notificationsservice.domain.entity.NotificationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationJpaRepository notificationJpaRepository;

    public NotificationController(NotificationJpaRepository notificationJpaRepository) {
        this.notificationJpaRepository = notificationJpaRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getNotification(@PathVariable UUID id) {
        return notificationJpaRepository.findById(id)
                .map(notification -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", notification.getId());
                    response.put("appointmentId", notification.getAppointmentId());
                    response.put("patientId", notification.getPatientId());
                    response.put("status", notification.getStatus());
                    response.put("message", notification.getMessage());
                    response.put("attemptCount", notification.getAttemptCount());
                    response.put("lastError", notification.getLastError());
                    response.put("createdAt", notification.getCreatedAt());
                    response.put("updatedAt", notification.getUpdatedAt());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<Map<String, Object>> getNotificationByAppointmentId(
            @PathVariable UUID appointmentId) {
        return notificationJpaRepository.findByAppointmentId(appointmentId)
                .map(notification -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", notification.getId());
                    response.put("appointmentId", notification.getAppointmentId());
                    response.put("patientId", notification.getPatientId());
                    response.put("status", notification.getStatus());
                    response.put("message", notification.getMessage());
                    response.put("attemptCount", notification.getAttemptCount());
                    response.put("createdAt", notification.getCreatedAt());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}/list")
    public ResponseEntity<Map<String, Object>> getNotificationsByPatientId(
            @PathVariable UUID patientId) {
        var notifications = notificationJpaRepository.findByPatientId(patientId);
        Map<String, Object> response = new HashMap<>();
        response.put("patientId", patientId);
        response.put("count", notifications.size());
        response.put("notifications", notifications);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}/list")
    public ResponseEntity<Map<String, Object>> getNotificationsByStatus(
            @PathVariable String status) {
        try {
            NotificationStatus notificationStatus = NotificationStatus.fromValue(status);
            var notifications = notificationJpaRepository.findByStatus(notificationStatus);
            Map<String, Object> response = new HashMap<>();
            response.put("status", notificationStatus);
            response.put("count", notifications.size());
            response.put("notifications", notifications);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid notification status: " + status);
            return ResponseEntity.badRequest().body(error);
        }
    }

}
