package com.dinidu.lk.pmt.bo.custom;

import com.dinidu.lk.pmt.bo.SuperBO;
import com.dinidu.lk.pmt.dto.PatientsDTO;
import com.dinidu.lk.pmt.dto.TherapyProgramsDTO;

import java.sql.SQLException;
import java.util.List;

public interface ProgramsBO extends SuperBO {
    boolean deleteProgram(String s) throws SQLException,ClassNotFoundException;
    void searchProgramByTherapistName(String projectName) throws SQLException,ClassNotFoundException;
    String getProgramNameById(Long taskId) throws SQLException,ClassNotFoundException;
    List<PatientsDTO> searchPatientsByName(String query) throws SQLException,ClassNotFoundException;
    String getTherapistNameById(String projectId) throws SQLException,ClassNotFoundException;
    List<PatientsDTO> searchProgramsByPatientName(String selectedIssueName) throws SQLException,ClassNotFoundException;
    List<PatientsDTO> getAllPatients() throws SQLException,ClassNotFoundException;
    void updateProgram(TherapyProgramsDTO currentTask) throws SQLException,ClassNotFoundException;
    List<TherapyProgramsDTO> getAllPrograms() throws SQLException,ClassNotFoundException;
    boolean insertProgram(TherapyProgramsDTO therapyProgramsDTO) throws SQLException,ClassNotFoundException;
    List<TherapyProgramsDTO> getProgramByTherapistId(String s) throws SQLException,ClassNotFoundException;
    List<TherapyProgramsDTO> searchProgramsByName(String query) throws SQLException,ClassNotFoundException;
    long getLastProgramID()throws SQLException,ClassNotFoundException;
}
