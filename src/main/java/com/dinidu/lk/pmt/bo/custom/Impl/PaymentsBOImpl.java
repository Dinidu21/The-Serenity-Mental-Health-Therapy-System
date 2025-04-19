package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.PaymentsBO;
import com.dinidu.lk.pmt.bo.custom.TherapySessionsBO;
import com.dinidu.lk.pmt.dao.DAOFactory;
import com.dinidu.lk.pmt.dao.custom.PatientsDAO;
import com.dinidu.lk.pmt.dao.custom.PaymentsDAO;
import com.dinidu.lk.pmt.dao.custom.TherapySessionsDAO;
import com.dinidu.lk.pmt.dto.PaymentDTO;
import com.dinidu.lk.pmt.entity.Patients;
import com.dinidu.lk.pmt.entity.Payments;
import com.dinidu.lk.pmt.entity.TherapySessions;

import java.sql.SQLException;

public class PaymentsBOImpl implements PaymentsBO {

    PaymentsDAO paymentsDAO =
            (PaymentsDAO) DAOFactory.getDaoFactory().
                    getDAO(DAOFactory.DAOTypes.PAYMENTS);
    PatientsDAO patientDAO =
            (PatientsDAO) DAOFactory.getDaoFactory().
                    getDAO(DAOFactory.DAOTypes.Patients);
    TherapySessionsDAO therapySessionDAO =
            (TherapySessionsDAO) DAOFactory.getDaoFactory().
                    getDAO(DAOFactory.DAOTypes.SESSIONS);


    @Override
    public boolean delete(Payments payment) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean save(PaymentDTO paymentDTO) throws SQLException, ClassNotFoundException {
        if (paymentDTO == null) {
            throw new SQLException("PaymentDTO object is null.");
        }

        Payments payments = new Payments();
        payments.setId(paymentDTO.getId());
        payments.setPaymentDate(paymentDTO.getPaymentDate());
        payments.setAmount(paymentDTO.getAmount());
        payments.setPaymentMethod(paymentDTO.getPaymentMethod());
        payments.setStatus(paymentDTO.getStatus());

        // Fetch Patients and TherapySessions entities by ID
        Patients patient = patientDAO.findById(paymentDTO.getPatientId());
        if (patient == null) {
            throw new SQLException("Patient with ID " + paymentDTO.getPatientId() + " not found.");
        }
        payments.setPatient(patient);

        TherapySessions therapySession = therapySessionDAO.findById(paymentDTO.getSessionId());
        if (therapySession == null) {
            throw new SQLException("Therapy Session with ID " + paymentDTO.getSessionId() + " not found.");
        }
        payments.setTherapySession(therapySession);

        return paymentsDAO.insert(payments);
    }

}
