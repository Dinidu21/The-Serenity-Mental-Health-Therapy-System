package com.dinidu.lk.pmt.dto;

import javafx.beans.property.*;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TherapyProgramsDTO {
    private LongProperty programId = new SimpleLongProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty fee = new SimpleStringProperty();
    private StringProperty duration = new SimpleStringProperty();

    public TherapyProgramsDTO(Long programId, String programName, String duration, String fee) {
        this.programId.set(programId);
        this.name.set(programName);
        this.duration.set(duration);
        this.fee.set(fee);
    }

    public long getProgramId() { return programId.get(); }
    public void setProgramId(long programId) { this.programId.set(programId); }
    public LongProperty programIdProperty() { return programId; }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public String getFee() { return fee.get(); }
    public void setFee(String fee) { this.fee.set(fee); }
    public StringProperty feeProperty() { return fee; }

    public String getDuration() { return duration.get(); }
    public void setDuration(String duration) { this.duration.set(duration); }
    public StringProperty durationProperty() { return duration; }
}