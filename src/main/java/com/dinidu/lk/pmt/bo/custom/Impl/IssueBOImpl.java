package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.IssuesBO;
import com.dinidu.lk.pmt.dao.DAOFactory;
import com.dinidu.lk.pmt.dao.custom.IssueDAO;
import com.dinidu.lk.pmt.dto.IssueDTO;
import com.dinidu.lk.pmt.entity.Issue;
import com.dinidu.lk.pmt.utils.EntityDTOMapper;

import java.sql.SQLException;
import java.util.List;

public class IssueBOImpl implements IssuesBO {
    IssueDAO issueDAO = (IssueDAO)
            DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ISSUES);

    @Override
    public boolean createIssue(IssueDTO issueDTO) throws SQLException, ClassNotFoundException {
        return issueDAO.insert(EntityDTOMapper.mapDTOToEntity(issueDTO, Issue.class));
    }

    @Override
    public String getProjectNameById(String projectId) throws SQLException, ClassNotFoundException {
        return issueDAO.getProjectNameById(projectId);
    }

    @Override
    public String getTaskNameById(long taskId) throws SQLException, ClassNotFoundException {
        return issueDAO.getTaskNameById(taskId);
    }

    @Override
    public List<IssueDTO> getAllIssues() throws SQLException, ClassNotFoundException {
        List<Issue> issues = issueDAO.fetchAll();
        return EntityDTOMapper.mapEntityListToDTOList(issues, IssueDTO.class);
    }

    @Override
    public List<IssueDTO> searchIssuesByName(String selectedIssueName) throws SQLException, ClassNotFoundException {
        return EntityDTOMapper.mapEntityListToDTOList(issueDAO.searchByName(selectedIssueName), IssueDTO.class);
    }

    @Override
    public boolean updateIssue(IssueDTO currentIssue) throws SQLException, ClassNotFoundException {
        return issueDAO.update(EntityDTOMapper.mapDTOToEntity(currentIssue, Issue.class));
    }

    @Override
    public boolean deleteIssue(Long id) throws SQLException, ClassNotFoundException {
        return issueDAO.deleteIssue(id);
    }

    @Override
    public String getProjectIdByName(String projectName) throws SQLException, ClassNotFoundException {
        return issueDAO.getProjectIdByName(projectName);
    }

    @Override
    public Long getTaskIdByName(String taskName) throws SQLException, ClassNotFoundException {
        return issueDAO.getIdByName(taskName);
    }

    @Override
    public Long getUserIdByName(String memberName) throws SQLException, ClassNotFoundException {
        return issueDAO.getUserIdByName(memberName);
    }

    @Override
    public List<String> getActiveProjectNames() throws SQLException, ClassNotFoundException {
        return issueDAO.getActiveProjectNames();
    }

    @Override
    public List<String> getTasksByProject(String selectedProject) throws SQLException, ClassNotFoundException {
        return issueDAO.getTasksByProject(selectedProject);
    }

    @Override
    public List<String> getActiveMembers() throws SQLException, ClassNotFoundException {
        return issueDAO.getActiveMembers();
    }
}
