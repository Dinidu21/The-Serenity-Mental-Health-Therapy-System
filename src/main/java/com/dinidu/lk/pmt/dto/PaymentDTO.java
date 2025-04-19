package com.dinidu.lk.pmt.dto;

import com.dinidu.lk.pmt.entity.Payments.PaymentMethod;
import com.dinidu.lk.pmt.entity.Payments.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentDTO {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long sessionId;
    private String sessionName;
    private Double amount;
    private LocalDateTime paymentDate;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
}

