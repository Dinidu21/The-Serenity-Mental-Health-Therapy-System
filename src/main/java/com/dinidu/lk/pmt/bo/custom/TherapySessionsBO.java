package com.dinidu.lk.pmt.bo.custom;

import com.dinidu.lk.pmt.bo.SuperBO;
import com.dinidu.lk.pmt.dto.TherapySessionsDTO;

import java.sql.SQLException;
import java.util.List;

public interface TherapySessionsBO extends SuperBO {
    boolean insert(TherapySessionsDTO therapySessions) throws SQLException, ClassNotFoundException;
    List<TherapySessionsDTO> getAllSessions() throws SQLException , ClassNotFoundException;
}
