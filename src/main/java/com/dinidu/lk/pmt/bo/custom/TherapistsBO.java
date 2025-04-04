package com.dinidu.lk.pmt.bo.custom;

import com.dinidu.lk.pmt.bo.SuperBO;
import com.dinidu.lk.pmt.dto.ProjectDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TherapistsBO extends SuperBO {
    List<ProjectDTO> getAllTherapists() throws SQLException,ClassNotFoundException;
    void updateTherapist(ProjectDTO currentProject) throws SQLException,ClassNotFoundException;
    boolean deleteTherapists(String id)throws SQLException,ClassNotFoundException;
    Optional<Object> isTherapistIdTaken(String fullProjectId) throws SQLException,ClassNotFoundException;
    boolean insert(ProjectDTO projectDTO) throws SQLException, ClassNotFoundException;
    String getTherapistIdByTaskId(long l) throws SQLException, ClassNotFoundException;
    String getTherapistNameById(String s) throws SQLException, ClassNotFoundException;
    String getTherapistIdByName(String selectedProjectName) throws SQLException, ClassNotFoundException;
    List<ProjectDTO> getTherapistById(String projectId) throws SQLException, ClassNotFoundException;
    Map<String, String> getAllTherapistNames() throws SQLException,ClassNotFoundException;
    List<ProjectDTO> searchTherapistByName(String query) throws SQLException, ClassNotFoundException;
}
