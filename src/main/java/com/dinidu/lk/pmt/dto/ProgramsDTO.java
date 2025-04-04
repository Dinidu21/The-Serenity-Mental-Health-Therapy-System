package com.dinidu.lk.pmt.dto;

import com.dinidu.lk.pmt.utils.taskTypes.TaskPriority;
import com.dinidu.lk.pmt.utils.taskTypes.TaskStatus;
import javafx.beans.property.*;
import lombok.*;

import java.util.Date;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProgramsDTO {
    private LongProperty id = new SimpleLongProperty();
    private StringProperty projectId = new SimpleStringProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private ObjectProperty<Long> assignedTo = new SimpleObjectProperty<>();
    private ObjectProperty<TaskPriority> priority = new SimpleObjectProperty<>();
    private ObjectProperty<TaskStatus> status = new SimpleObjectProperty<>();
    private ObjectProperty<Date> dueDate = new SimpleObjectProperty<>();
    private ObjectProperty<Date> createdAt = new SimpleObjectProperty<>();
    private ObjectProperty<Date> updatedAt = new SimpleObjectProperty<>();

    public LongProperty idProperty() {
        return id;
    }

    public StringProperty projectIdProperty() {
        return projectId;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public ObjectProperty<TaskPriority> priorityProperty() {
        return priority;
    }

    public ObjectProperty<TaskStatus> statusProperty() {
        return status;
    }

    public ObjectProperty<Date> dueDateProperty() {
        return dueDate;
    }

    public ObjectProperty<Date> createdAtProperty() {
        return createdAt;
    }

    public ObjectProperty<Date> updatedAtProperty() {
        return updatedAt;
    }

    public TaskStatus getStatus() {
        return status.get();
    }

    public void setStatus(TaskStatus status) {
        this.status.set(status);
    }

    public TaskPriority getPriority() {
        return priority.get();
    }

    public void setPriority(TaskPriority priority) {
        this.priority.set(priority);
    }

    public ObjectProperty<Long> assignedToProperty() {
        return assignedTo;
    }
}
