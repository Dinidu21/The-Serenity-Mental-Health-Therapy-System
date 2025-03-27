package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.ProjectsBO;
import com.dinidu.lk.pmt.dao.DAOFactory;
import com.dinidu.lk.pmt.dao.custom.ProjectDAO;
import com.dinidu.lk.pmt.dto.ProjectDTO;
import com.dinidu.lk.pmt.entity.Project;
import com.dinidu.lk.pmt.utils.EntityDTOMapper;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectStatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProjectsBOImpl implements ProjectsBO {

    ProjectDAO projectDAO =
            (ProjectDAO) DAOFactory.getDaoFactory().
                    getDAO(DAOFactory.DAOTypes.PROJECTS);

    @Override
    public List<ProjectDTO> getProjectsByStatus(ProjectStatus projectStatus) throws SQLException, ClassNotFoundException {
        return EntityDTOMapper.mapEntityListToDTOList(projectDAO.getProjectsByStatus(projectStatus), ProjectDTO.class);
    }

    @Override
    public List<ProjectDTO> getProjectById(String projectId) throws SQLException, ClassNotFoundException {
        List<Project> projectById = projectDAO.getProjectById(projectId);
        return EntityDTOMapper.mapEntityListToDTOList(projectById, ProjectDTO.class);
    }

    @Override
    public Optional<ProjectDTO> isProjectIdTaken(String projectId) throws SQLException, ClassNotFoundException {
        Optional<Project> projectIdTaken = projectDAO.isProjectIdTaken(projectId);
        return projectIdTaken.map(project -> new ProjectDTO(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getStatus(),
                project.getPriority(),
                project.getVisibility(),
                project.getCreatedBy(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        ));
    }

    @Override
    public String getProjectIdByName(String selectedProjectName) throws SQLException, ClassNotFoundException {
       return projectDAO.getProjectIdByName(selectedProjectName);
    }

    @Override
    public Map<String, String> getAllProjectNames() throws SQLException, ClassNotFoundException {
        return projectDAO.getAllNames();
    }

    @Override
    public String getProjectNameById(String projectId) throws SQLException, ClassNotFoundException {
        return projectDAO.getProjectNameById(projectId);
    }

    @Override
    public String getProjectIdByTaskId(long l) throws SQLException, ClassNotFoundException {
        return projectDAO.getProjectIdByTaskId(l);
    }

    @Override
    public boolean insert(ProjectDTO project) throws SQLException, ClassNotFoundException {
        Project projectEntity = convertToEntity(project);
        return projectDAO.insert(projectEntity);
    }

    @Override
    public List<ProjectDTO> searchProjectsByName(String searchQuery) throws SQLException, ClassNotFoundException {
        List<Project> projects = projectDAO.searchByName(searchQuery);
        List<ProjectDTO> projectDTOS = new ArrayList<>();
        for (Project project : projects) {
            projectDTOS.add(new ProjectDTO(
                    project.getId(),
                    project.getName(),
                    project.getDescription(),
                    project.getStartDate(),
                    project.getEndDate(),
                    project.getStatus(),
                    project.getPriority(),
                    project.getVisibility(),
                    project.getCreatedBy(),
                    project.getCreatedAt(),
                    project.getUpdatedAt()
            ));
        }
        return projectDTOS;
    }

    @Override
    public List<ProjectDTO> getAllProjects() throws SQLException, ClassNotFoundException{
        List<Project> allProjects = projectDAO.getAllProjects();
        return EntityDTOMapper.mapEntityListToDTOList(allProjects, ProjectDTO.class);
    }

    @Override
    public boolean deleteProject(String projectId) throws SQLException, ClassNotFoundException {
        return projectDAO.delete(projectId);
    }

    @Override
    public void updateProject(ProjectDTO projectDTO) throws SQLException, ClassNotFoundException {
        Project project = convertToEntity(projectDTO);
        projectDAO.updateProject(project);
    }

    private Project convertToEntity(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setId(projectDTO.getId());
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setStatus(projectDTO.getStatus());
        project.setPriority(projectDTO.getPriority());
        project.setVisibility(projectDTO.getVisibility());
        project.setCreatedBy(projectDTO.getCreatedBy());
        project.setCreatedAt(projectDTO.getCreatedAt());
        project.setUpdatedAt(projectDTO.getUpdatedAt());
        return project;
    }
}