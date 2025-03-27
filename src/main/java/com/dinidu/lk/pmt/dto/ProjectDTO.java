package com.dinidu.lk.pmt.dto;

import com.dinidu.lk.pmt.utils.projectTypes.ProjectPriority;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectStatus;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectVisibility;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.*;

import java.util.Date;
@NoArgsConstructor
@ToString
@Getter
@Setter
@AllArgsConstructor
public class ProjectDTO {
    private String id;
    private StringProperty name = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private ObjectProperty<Date> startDate = new SimpleObjectProperty<>();
    private ObjectProperty<Date> endDate = new SimpleObjectProperty<>();
    private ObjectProperty<ProjectStatus> status = new SimpleObjectProperty<>();
    private ObjectProperty<ProjectPriority> priority = new SimpleObjectProperty<>();
    private ObjectProperty<ProjectVisibility> visibility = new SimpleObjectProperty<>();
    private Long createdBy;
    private Date createdAt;
    private Date updatedAt;

    public ProjectDTO(String id, String name, String description, Date startDate, Date endDate, ProjectStatus status, ProjectPriority priority, ProjectVisibility visibility, Long createdBy, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
        this.status = new SimpleObjectProperty<>(status);
        this.priority = new SimpleObjectProperty<>(priority);
        this.visibility = new SimpleObjectProperty<>(visibility);
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    public StringProperty descriptionProperty() { return description; }

    public Date getStartDate() { return startDate.get(); }
    public void setStartDate(Date startDate) { this.startDate.set(startDate); }
    public ObjectProperty<Date> startDateProperty() { return startDate; }

    public Date getEndDate() { return endDate.get(); }
    public void setEndDate(Date endDate) { this.endDate.set(endDate); }
    public ObjectProperty<Date> endDateProperty() { return endDate; }

    public ProjectStatus getStatus() { return status.get(); }
    public void setStatus(ProjectStatus status) { this.status.set(status); }
    public ObjectProperty<ProjectStatus> statusProperty() { return status; }

    public ProjectPriority getPriority() { return priority.get(); }
    public void setPriority(ProjectPriority priority) { this.priority.set(priority); }
    public ObjectProperty<ProjectPriority> priorityProperty() { return priority; }

    public ProjectVisibility getVisibility() { return visibility.get(); }
    public void setVisibility(ProjectVisibility visibility) { this.visibility.set(visibility); }
    public ObjectProperty<ProjectVisibility> visibilityProperty() { return visibility; }
}
