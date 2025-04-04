package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.PatientBO;
import com.dinidu.lk.pmt.dto.PatientsDTO;

import java.sql.SQLException;
import java.util.List;

public class PatientBOImpl implements PatientBO {
    @Override
    public String getTherapistsIdByName(String value) throws SQLException, ClassNotFoundException {
        return "";
    }

    @Override
    public Long getProgramIdByName(String selectedTaskName) throws SQLException, ClassNotFoundException {
        return 0L;
    }

    @Override
    public boolean updatePatient(PatientsDTO currentIssue) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean deletePatient(Long id) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public String getProgramNameById(long taskId) throws SQLException, ClassNotFoundException {
        return "";
    }

    @Override
    public String getTherapistNameById(String projectId) throws SQLException, ClassNotFoundException {
        return "";
    }

    @Override
    public List<String> getActivePatients() throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public List<String> getProgramsByTherapist(String selectedProject) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public List<String> getActiveTherapistsNames() throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public List<PatientsDTO> getAllPatients() throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public boolean createPatient(PatientsDTO patientsDTO) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public Long getUserIdByName(String memberName) throws SQLException, ClassNotFoundException {
        return 0L;
    }

    @Override
    public List<String> getProgramByTherapist(String selectedProject) throws SQLException, ClassNotFoundException {
        return List.of();
    }
}
