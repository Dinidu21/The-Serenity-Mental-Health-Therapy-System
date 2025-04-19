package com.dinidu.lk.pmt.bo.custom;

import com.dinidu.lk.pmt.bo.SuperBO;
import com.dinidu.lk.pmt.dto.PaymentDTO;
import com.dinidu.lk.pmt.entity.Patients;
import com.dinidu.lk.pmt.entity.Payments;

import java.sql.SQLException;

public interface PaymentsBO extends SuperBO {

    boolean delete(Payments payment) throws SQLException, ClassNotFoundException;

    boolean save(PaymentDTO paymentDTO) throws SQLException, ClassNotFoundException;;
}
