package com.dinidu.lk.pmt.bo.custom;

import com.dinidu.lk.pmt.bo.SuperBO;
import com.dinidu.lk.pmt.dto.PaymentDTO;
import java.sql.SQLException;
import java.util.List;


public interface PaymentsBO extends SuperBO {

    boolean save(PaymentDTO paymentDTO) throws SQLException, ClassNotFoundException;;

    boolean delete(Long id)  throws SQLException, ClassNotFoundException;;

    List<PaymentDTO> getAllPayments()throws SQLException, ClassNotFoundException;


}
