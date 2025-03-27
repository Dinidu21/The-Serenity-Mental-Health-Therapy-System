package com.dinidu.lk.pmt.dto;

import com.dinidu.lk.pmt.utils.checklistTypes.ChecklistPriority;
import com.dinidu.lk.pmt.utils.checklistTypes.ChecklistStatus;
import javafx.beans.property.*;

import java.time.LocalDateTime;

public class ChecklistDTO {
    private final LongProperty id = new SimpleLongProperty();
    private final LongProperty taskId = new SimpleLongProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<ChecklistStatus> status = new SimpleObjectProperty<>();
    private final ObjectProperty<ChecklistPriority> priority = new SimpleObjectProperty<>();
    private final LongProperty assignedTo = new SimpleLongProperty();
    private final ObjectProperty<LocalDateTime> dueDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();

    public LongProperty idProperty() {
        return id;
    }

    public LongProperty taskIdProperty() {
        return taskId;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public ObjectProperty<ChecklistStatus> statusProperty() {
        return status;
    }

    public ObjectProperty<ChecklistPriority> priorityProperty() {
        return priority;
    }

    public LongProperty assignedToProperty() {
        return assignedTo;
    }

    public ObjectProperty<LocalDateTime> dueDateProperty() {
        return dueDate;
    }

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public ObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }
}
