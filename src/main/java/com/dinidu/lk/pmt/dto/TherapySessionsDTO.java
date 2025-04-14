package com.dinidu.lk.pmt.dto;

import com.dinidu.lk.pmt.entity.TherapySessions.SessionStatus;
import javafx.beans.property.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TherapySessionsDTO {

    private LongProperty id = new SimpleLongProperty();
    private StringProperty therapistId = new SimpleStringProperty();
    private LongProperty patientId = new SimpleLongProperty();
    private LongProperty therapyProgramId = new SimpleLongProperty();

    private ObjectProperty<LocalDate> sessionDate = new SimpleObjectProperty<>();
    private StringProperty sessionTime = new SimpleStringProperty();
    private ObjectProperty<LocalDate> sessionMadeDate = new SimpleObjectProperty<>();

    private StringProperty description = new SimpleStringProperty();
    private ObjectProperty<SessionStatus> status = new SimpleObjectProperty<>();

    public TherapySessionsDTO(Long id, String therapistId, Long patientId, Long therapyProgramId,
                              LocalDate sessionDate, String sessionTime,
                              LocalDate sessionMadeDate, String description, SessionStatus status) {
        this.id.set(id);
        this.therapistId.set(therapistId);
        this.patientId.set(patientId);
        this.therapyProgramId.set(therapyProgramId);
        this.sessionDate.set(sessionDate);
        this.sessionTime.set(sessionTime);
        this.sessionMadeDate.set(sessionMadeDate);
        this.description.set(description);
        this.status.set(status);
    }

    // Property accessors for JavaFX binding
    public long getId() { return id.get(); }
    public void setId(long id) { this.id.set(id); }
    public LongProperty idProperty() { return id; }

    public String getTherapistId() { return therapistId.get(); }
    public void setTherapistId(String therapistId) { this.therapistId.set(therapistId); }
    public StringProperty therapistIdProperty() { return therapistId; }

    public long getPatientId() { return patientId.get(); }
    public void setPatientId(long patientId) { this.patientId.set(patientId); }
    public LongProperty patientIdProperty() { return patientId; }

    public long getTherapyProgramId() { return therapyProgramId.get(); }
    public void setTherapyProgramId(long therapyProgramId) { this.therapyProgramId.set(therapyProgramId); }
    public LongProperty therapyProgramIdProperty() { return therapyProgramId; }

    public LocalDate getSessionDate() { return sessionDate.get(); }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate.set(sessionDate); }
    public ObjectProperty<LocalDate> sessionDateProperty() { return sessionDate; }

    public String getSessionTime() { return sessionTime.get(); }
    public void setSessionTime(String sessionTime) { this.sessionTime.set(sessionTime); }
    public StringProperty sessionTimeProperty() { return sessionTime; }

    public LocalDate getSessionMadeDate() { return sessionMadeDate.get(); }
    public void setSessionMadeDate(LocalDate sessionMadeDate) { this.sessionMadeDate.set(sessionMadeDate); }
    public ObjectProperty<LocalDate> sessionMadeDateProperty() { return sessionMadeDate; }

    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    public StringProperty descriptionProperty() { return description; }

    public SessionStatus getStatus() { return status.get(); }
    public void setStatus(SessionStatus status) { this.status.set(status); }
    public ObjectProperty<SessionStatus> statusProperty() { return status; }
}