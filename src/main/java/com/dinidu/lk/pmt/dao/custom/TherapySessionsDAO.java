package com.dinidu.lk.pmt.dao.custom;

import com.dinidu.lk.pmt.dao.CrudDAO;
import com.dinidu.lk.pmt.entity.TherapySessions;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface TherapySessionsDAO extends CrudDAO<TherapySessions> {

    boolean deleteSessions(long id) throws SQLException, ClassNotFoundException;

    TherapySessions getSessionIdByDesc(String selectedSession) throws SQLException, ClassNotFoundException;

    TherapySessions findById(Long sessionId) throws SQLException, ClassNotFoundException;

    List<TherapySessions> findByTherapistIdAndSessionDateAndSessionTime(String therapistId, LocalDate sessionDate, String sessionTime)throws SQLException, ClassNotFoundException;
}
