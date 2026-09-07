CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    appointment_id UUID NOT NULL,
    patient_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    attempt_count INTEGER NOT NULL DEFAULT 0,
    last_error TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_appointment_id ON notifications(appointment_id);
CREATE INDEX idx_patient_id ON notifications(patient_id);
CREATE INDEX idx_status ON notifications(status);
CREATE INDEX idx_created_at ON notifications(created_at DESC);
