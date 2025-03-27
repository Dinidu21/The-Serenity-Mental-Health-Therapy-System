package com.dinidu.lk.pmt.dao.custom;

import com.dinidu.lk.pmt.dao.CrudDAO;
import com.dinidu.lk.pmt.entity.Project;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectStatus;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProjectDAO extends CrudDAO<Project> {
    List<Project> getAllProjects() throws SQLException, ClassNotFoundException;
    List<Project> getProjectsByStatus(ProjectStatus projectStatus) throws SQLException,ClassNotFoundException;
    List<Project> getProjectById(String projectId) throws SQLException,ClassNotFoundException;
    Optional<Project> isProjectIdTaken(String projectId) throws SQLException,ClassNotFoundException;
    void updateProject(Project project) throws SQLException,ClassNotFoundException;
    String getProjectIdByName(String selectedProjectName) throws SQLException,ClassNotFoundException;
    String getProjectIdByTaskId(long l) throws SQLException,ClassNotFoundException;
    String getProjectNameById(String projectId) throws SQLException,ClassNotFoundException;
}
