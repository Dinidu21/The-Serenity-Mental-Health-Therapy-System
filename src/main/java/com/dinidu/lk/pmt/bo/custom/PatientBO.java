package com.dinidu.lk.pmt.bo.custom;

import com.dinidu.lk.pmt.bo.SuperBO;
import com.dinidu.lk.pmt.dto.PatientsDTO;

import java.sql.SQLException;
import java.util.List;

public interface PatientBO extends SuperBO {
    String getTherapistsIdByName(String value) throws SQLException , ClassNotFoundException;
    Long getProgramIdByName(String selectedTaskName) throws SQLException, ClassNotFoundException;
    boolean updatePatient(PatientsDTO currentIssue) throws SQLException, ClassNotFoundException;
    boolean deletePatient(Long id) throws SQLException, ClassNotFoundException;
    String getProgramNameById(long taskId) throws SQLException, ClassNotFoundException;
    String getTherapistNameById(String projectId) throws SQLException, ClassNotFoundException;
    List<String> getActivePatients() throws  SQLException, ClassNotFoundException;
    List<String> getProgramsByTherapist(String selectedProject) throws SQLException, ClassNotFoundException;
    List<String> getActiveTherapistsNames() throws SQLException, ClassNotFoundException;
    List<PatientsDTO> getAllPatients() throws SQLException, ClassNotFoundException;
    boolean createPatient(PatientsDTO patientsDTO) throws SQLException, ClassNotFoundException;
    Long getUserIdByName(String memberName) throws SQLException, ClassNotFoundException;
    List<String> getProgramByTherapist(String selectedProject) throws SQLException, ClassNotFoundException;
}
