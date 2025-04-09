package com.dinidu.lk.pmt.dto;

import javafx.beans.property.*;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TherapyProgramsDTO {
    private LongProperty id = new SimpleLongProperty();
    private StringProperty projectId = new SimpleStringProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty fee = new SimpleStringProperty();
    private StringProperty duration = new SimpleStringProperty();


    public LongProperty idProperty() {
        return id;
    }
    public long getId() {
        return id.get();
    }
    public void setId(long id) {
        this.id.set(id);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty feeProperty() {
        return fee;
    }

    public String getFee() {
        return fee.get();
    }

    public void setFee(String fee) {
        this.fee.set(fee);
    }

    public StringProperty durationProperty() {
        return duration;
    }

    public String getDuration() {
        return duration.get();
    }

    public void setDuration(String duration) {
        this.duration.set(duration);
    }

}
