package com.dinidu.lk.pmt.dao.custom;

import com.dinidu.lk.pmt.dao.CrudDAO;
import com.dinidu.lk.pmt.entity.Patients;

import java.sql.SQLException;

public interface PatientsDAO extends CrudDAO<Patients> {
    boolean deletePatient(Long id) throws SQLException , ClassNotFoundException;
}
