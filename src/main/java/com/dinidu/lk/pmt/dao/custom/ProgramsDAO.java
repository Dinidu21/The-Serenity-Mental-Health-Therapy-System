package com.dinidu.lk.pmt.dao.custom;

import com.dinidu.lk.pmt.dao.CrudDAO;
import com.dinidu.lk.pmt.entity.TherapyPrograms;
import javafx.beans.property.StringProperty;

import java.sql.SQLException;
import java.util.List;


public interface ProgramsDAO extends CrudDAO<TherapyPrograms> {
    long getLastProgramID() throws SQLException, ClassNotFoundException;
    TherapyPrograms getProgramById(long proId) throws SQLException, ClassNotFoundException;
    TherapyPrograms getById(Long programId) throws SQLException, ClassNotFoundException;
}
