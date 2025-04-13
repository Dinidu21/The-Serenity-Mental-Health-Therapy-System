package com.dinidu.lk.pmt.dto;

import javafx.beans.property.*;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PatientsDTO {
    private Long id;

    private StringProperty fullName = new SimpleStringProperty();
    private StringProperty email = new SimpleStringProperty();
    private StringProperty address = new SimpleStringProperty();
    private StringProperty phoneNumber = new SimpleStringProperty();
    private StringProperty medicalHistory = new SimpleStringProperty();
    private ObjectProperty<LocalDate> registrationDate = new SimpleObjectProperty<>();

    public PatientsDTO(Long id, String fullName, String email, String address, String phoneNumber, String medicalHistory, LocalDate registrationDate) {
        this.id = id;
        this.fullName.set(fullName);
        this.email.set(email);
        this.address.set(address);
        this.phoneNumber.set(phoneNumber);
        this.medicalHistory.set(medicalHistory);
        this.registrationDate.set(registrationDate);
    }

    public String getFullName() { return fullName.get(); }
    public void setFullName(String fullName) { this.fullName.set(fullName); }
    public StringProperty fullNameProperty() { return fullName; }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }

    public String getAddress() { return address.get(); }
    public void setAddress(String address) { this.address.set(address); }
    public StringProperty addressProperty() { return address; }

    public String getPhoneNumber() { return phoneNumber.get(); }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber.set(phoneNumber); }
    public StringProperty phoneNumberProperty() { return phoneNumber; }

    public String getMedicalHistory() { return medicalHistory.get(); }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory.set(medicalHistory); }
    public StringProperty medicalHistoryProperty() { return medicalHistory; }

    public LocalDate getRegistrationDate() { return registrationDate.get(); }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate.set(registrationDate); }
    public ObjectProperty<LocalDate> registrationDateProperty() { return registrationDate; }
}
