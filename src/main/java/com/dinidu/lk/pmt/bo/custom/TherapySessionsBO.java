package com.dinidu.lk.pmt.bo.custom;

import com.dinidu.lk.pmt.bo.SuperBO;
import com.dinidu.lk.pmt.dto.TherapySessionsDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface TherapySessionsBO extends SuperBO {
    boolean insert(TherapySessionsDTO therapySessions) throws SQLException, ClassNotFoundException;
    List<TherapySessionsDTO> getAllSessions() throws SQLException , ClassNotFoundException;

    boolean updateChecklist(TherapySessionsDTO currentChecklist) throws SQLException, ClassNotFoundException;

    boolean deleteChecklist(long id) throws SQLException, ClassNotFoundException;

    TherapySessionsDTO getSessionIdByDesc(String selectedSession) throws SQLException, ClassNotFoundException;

    TherapySessionsDTO getSessionById(Long sessionId) throws SQLException, ClassNotFoundException;

    boolean checkSchedulingConflict(String therapistId, LocalDate sessionDate, String sessionTime) throws SQLException, ClassNotFoundException;
}
