package com.dinidu.lk.pmt.dto;

import com.dinidu.lk.pmt.utils.issuesTypes.IssuePriority;
import com.dinidu.lk.pmt.utils.issuesTypes.IssueStatus;
import javafx.beans.property.*;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;

@NoArgsConstructor
@ToString
@Getter
@Setter
@AllArgsConstructor
public class PatientsDTO {
    private Long id;
    private StringProperty projectId = new SimpleStringProperty();
    private LongProperty taskId = new SimpleLongProperty();
    private StringProperty description = new SimpleStringProperty();
    private LongProperty reportedBy = new SimpleLongProperty();
    private LongProperty assignedTo = new SimpleLongProperty();
    private ObjectProperty<IssueStatus> status = new SimpleObjectProperty<>();
    private ObjectProperty<IssuePriority> priority = new SimpleObjectProperty<>();
    private ObjectProperty<Date> dueDate = new SimpleObjectProperty<>();
    private ObjectProperty<Timestamp> createdAt = new SimpleObjectProperty<>();
    private ObjectProperty<Timestamp> updatedAt = new SimpleObjectProperty<>();

    public String getProjectId() { return projectId.get(); }
    public void setProjectId(String projectId) { this.projectId.set(projectId); }
    public StringProperty projectIdProperty() { return projectId; }

    public long getTaskId() { return taskId.get(); }
    public void setTaskId(long taskId) { this.taskId.set(taskId); }
    public LongProperty taskIdProperty() { return taskId; }

    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    public StringProperty descriptionProperty() { return description; }

    public long getReportedBy() { return reportedBy.get(); }
    public void setReportedBy(long reportedBy) { this.reportedBy.set(reportedBy); }
    public LongProperty reportedByProperty() { return reportedBy; }

    public long getAssignedTo() { return assignedTo.get(); }
    public void setAssignedTo(long assignedTo) { this.assignedTo.set(assignedTo); }
    public LongProperty assignedToProperty() { return assignedTo; }

    public IssueStatus getStatus() { return status.get(); }
    public void setStatus(IssueStatus status) { this.status.set(status); }
    public ObjectProperty<IssueStatus> statusProperty() { return status; }

    public IssuePriority getPriority() { return priority.get(); }
    public void setPriority(IssuePriority priority) { this.priority.set(priority); }
    public ObjectProperty<IssuePriority> priorityProperty() { return priority; }

    public Date getDueDate() { return dueDate.get(); }
    public void setDueDate(Date dueDate) { this.dueDate.set(dueDate); }
    public ObjectProperty<Date> dueDateProperty() { return dueDate; }

    public Timestamp getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt.set(createdAt); }
    public ObjectProperty<Timestamp> createdAtProperty() { return createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt.get(); }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt.set(updatedAt); }
    public ObjectProperty<Timestamp> updatedAtProperty() { return updatedAt; }

    public Long getIssueId() {
        return id;
    }
}

