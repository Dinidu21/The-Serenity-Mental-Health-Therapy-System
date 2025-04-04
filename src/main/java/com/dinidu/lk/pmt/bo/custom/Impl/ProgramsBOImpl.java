package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.ProgramsBO;
import com.dinidu.lk.pmt.dto.PatientsDTO;
import com.dinidu.lk.pmt.dto.ProgramsDTO;

import java.sql.SQLException;
import java.util.List;

public class ProgramsBOImpl implements ProgramsBO {

    @Override
    public boolean deleteProgram(String s) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public void searchProgramByTherapistName(String projectName) throws SQLException, ClassNotFoundException {

    }

    @Override
    public String getProgramNameById(Long taskId) throws SQLException, ClassNotFoundException {
        return "";
    }

    @Override
    public List<PatientsDTO> searchPatientsByName(String query) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public String getTherapistNameById(String projectId) throws SQLException, ClassNotFoundException {
        return "";
    }

    @Override
    public List<PatientsDTO> searchProgramsByPatientName(String selectedIssueName) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public List<PatientsDTO> getAllPatients() throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public void updateProgram(ProgramsDTO currentTask) throws SQLException, ClassNotFoundException {

    }

    @Override
    public List<ProgramsDTO> getAllPrograms() throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public boolean insertProgram(ProgramsDTO programsDTO) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<ProgramsDTO> getProgramByTherapistId(String s) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public List<ProgramsDTO> searchProgramsByName(String query) throws SQLException, ClassNotFoundException {
        return List.of();
    }
}
