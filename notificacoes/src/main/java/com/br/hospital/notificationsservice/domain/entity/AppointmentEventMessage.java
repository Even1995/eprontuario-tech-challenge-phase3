package com.br.eprontuario.notificationsservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

public class AppointmentEventMessage {

    @JsonProperty("eventId")
    private UUID eventId;

    @JsonProperty("appointmentId")
    private UUID appointmentId;

    @JsonProperty("patientId")
    private UUID patientId;

    @JsonProperty("doctorId")
    private UUID doctorId;

    @JsonProperty("scheduledAt")
    private LocalDateTime scheduledAt;

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("status")
    private String status;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    public AppointmentEventMessage() {
    }

    public AppointmentEventMessage(UUID eventId, UUID appointmentId, UUID patientId, UUID doctorId,
                                    LocalDateTime scheduledAt, String eventType, String status,
                                    String reason, LocalDateTime timestamp) {
        this.eventId = eventId;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.scheduledAt = scheduledAt;
        this.eventType = eventType;
        this.status = status;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public UUID getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(UUID appointmentId) {
        this.appointmentId = appointmentId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(UUID doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}
