package com.dinidu.lk.pmt.bo.custom;

import com.dinidu.lk.pmt.bo.SuperBO;
import com.dinidu.lk.pmt.dto.ProjectDTO;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectStatus;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProjectsBO extends SuperBO {
    List<ProjectDTO> getProjectsByStatus(ProjectStatus projectStatus) throws SQLException, ClassNotFoundException;
    List<ProjectDTO> getProjectById(String projectId) throws SQLException, ClassNotFoundException;
    Optional<ProjectDTO> isProjectIdTaken(String projectId) throws SQLException, ClassNotFoundException;
    String getProjectIdByName(String selectedProjectName) throws SQLException,ClassNotFoundException;
    Map<String, String> getAllProjectNames() throws SQLException,ClassNotFoundException;
    String getProjectNameById(String projectId) throws SQLException, ClassNotFoundException;
    String getProjectIdByTaskId(long l) throws SQLException,ClassNotFoundException;
    boolean insert(ProjectDTO project) throws SQLException, ClassNotFoundException;
    List<ProjectDTO> searchProjectsByName(String searchQuery) throws SQLException, ClassNotFoundException;
    void updateProject(ProjectDTO projectDTO) throws SQLException, ClassNotFoundException;
    List<ProjectDTO> getAllProjects()throws SQLException, ClassNotFoundException;
    boolean deleteProject(String projectId) throws SQLException,ClassNotFoundException;
}
