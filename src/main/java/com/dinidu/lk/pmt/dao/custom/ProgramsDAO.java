package com.dinidu.lk.pmt.dao.custom;

import com.dinidu.lk.pmt.dao.CrudDAO;
import com.dinidu.lk.pmt.entity.TherapyPrograms;

import java.sql.SQLException;


public interface ProgramsDAO extends CrudDAO<TherapyPrograms> {

    long getLastProgramID() throws SQLException, ClassNotFoundException;
}
