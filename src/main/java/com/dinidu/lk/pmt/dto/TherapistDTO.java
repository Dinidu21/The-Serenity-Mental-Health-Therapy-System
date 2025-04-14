package com.dinidu.lk.pmt.dto;

import com.dinidu.lk.pmt.utils.projectTypes.TherapistStatus;
import javafx.beans.property.*;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TherapistDTO {
    private String id;
    private final StringProperty fullName = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final StringProperty phoneNumber = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final ObjectProperty<TherapistStatus> status = new SimpleObjectProperty<>();
    private Long createdBy;
    private Date createdAt;
    private Date updatedAt;

    public TherapistDTO(String id, String fullName, String address, String phoneNumber, String email, TherapistStatus status, Long createdBy, Date createdAt, Date updatedAt) {
        this.id = id;
        this.fullName.set(fullName);
        this.address.set(address);
        this.phoneNumber.set(phoneNumber);
        this.email.set(email);
        this.status.set(status);
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getFullName() { return fullName.get(); }
    public void setFullName(String fullName) { this.fullName.set(fullName); }
    public StringProperty fullNameProperty() { return fullName; }

    public String getAddress() { return address.get(); }
    public void setAddress(String address) { this.address.set(address); }
    public StringProperty addressProperty() { return address; }

    public String getPhoneNumber() { return phoneNumber.get(); }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber.set(phoneNumber); }
    public StringProperty phoneNumberProperty() { return phoneNumber; }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }

    public TherapistStatus getStatus() { return status.get(); }
    public void setStatus(TherapistStatus status) { this.status.set(status); }
    public ObjectProperty<TherapistStatus> statusProperty() { return status; }
}
