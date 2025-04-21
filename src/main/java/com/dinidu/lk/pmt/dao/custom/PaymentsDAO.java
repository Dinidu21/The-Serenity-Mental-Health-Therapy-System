package com.dinidu.lk.pmt.dao.custom;

import com.dinidu.lk.pmt.dao.CrudDAO;
import com.dinidu.lk.pmt.entity.Payments;

import java.sql.SQLException;
import java.util.List;

public interface PaymentsDAO extends CrudDAO<Payments> {
    Payments findById(Long id) throws SQLException, ClassNotFoundException;

    boolean deleteID(Long id) throws SQLException, ClassNotFoundException;

    List<Payments> findAll() throws SQLException, ClassNotFoundException;
}
