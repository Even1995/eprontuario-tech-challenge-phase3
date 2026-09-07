CREATE TABLE IF NOT EXISTS appointments_history (
    id VARCHAR(36) PRIMARY KEY,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    notes TEXT,
    doctor_id VARCHAR(36) NOT NULL,
    nurse_id VARCHAR(36),
    patient_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_patient_id ON appointments_history(patient_id);
CREATE INDEX idx_doctor_id ON appointments_history(doctor_id);
CREATE INDEX idx_status ON appointments_history(status);
CREATE INDEX idx_start_at ON appointments_history(start_at);
CREATE INDEX idx_created_at ON appointments_history(created_at);
