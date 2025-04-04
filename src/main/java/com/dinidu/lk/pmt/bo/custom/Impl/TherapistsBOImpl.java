package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.TherapistsBO;
import com.dinidu.lk.pmt.dto.ProjectDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TherapistsBOImpl implements TherapistsBO {

    public List<ProjectDTO> getAllTherapists() throws SQLException,ClassNotFoundException {
        return null;
    }

    public boolean deleteTherapists(String id) throws SQLException,ClassNotFoundException{
        return false;
    }

    @Override
    public Optional<Object> isTherapistIdTaken(String fullProjectId) throws SQLException, ClassNotFoundException {
        return Optional.empty();
    }

    @Override
    public boolean insert(ProjectDTO projectDTO) {
        return false;
    }

    @Override
    public String getTherapistIdByTaskId(long l) {
        return "";
    }

    @Override
    public String getTherapistNameById(String s) throws SQLException, ClassNotFoundException {
        return "";
    }

    @Override
    public String getTherapistIdByName(String selectedProjectName) throws SQLException, ClassNotFoundException {
        return "";
    }

    @Override
    public List<ProjectDTO> getTherapistById(String projectId) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Map<String, String> getAllTherapistNames() throws SQLException, ClassNotFoundException {
        return Map.of();
    }

    @Override
    public List<ProjectDTO> searchTherapistByName(String query) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    public void updateTherapist(ProjectDTO currentProject) throws SQLException,ClassNotFoundException{

    }
}
