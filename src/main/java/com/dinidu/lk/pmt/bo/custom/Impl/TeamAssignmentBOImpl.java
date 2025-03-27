package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.TeamAssignmentBO;
import com.dinidu.lk.pmt.dao.DAOFactory;
import com.dinidu.lk.pmt.dao.custom.TeamAssignmentDAO;
import com.dinidu.lk.pmt.dto.TeamAssignmentDTO;
import com.dinidu.lk.pmt.entity.TeamAssignment;
import com.dinidu.lk.pmt.utils.EntityDTOMapper;

import java.sql.SQLException;
import java.util.List;

public class TeamAssignmentBOImpl implements TeamAssignmentBO {
    TeamAssignmentDAO teamAssignmentDAO  =
            (TeamAssignmentDAO) DAOFactory.getDaoFactory().
                    getDAO(DAOFactory.DAOTypes.TEAM_ASSIGNMENTS);
    @Override
    public List<String> getAllTeamMembersNamesByTask(String taskName) throws SQLException, ClassNotFoundException {
       return teamAssignmentDAO.getAllTeamMembersNamesByTask(taskName);
    }

    @Override
    public List<String> getTeamMemberEmailsByTask(long taskId) throws SQLException, ClassNotFoundException {
        return teamAssignmentDAO.getTeamMemberEmailsByTask(taskId);
    }

    @Override
    public List<Long> getTeamMemberIdsByTask(long taskId) throws SQLException, ClassNotFoundException {
        return teamAssignmentDAO.getTeamMemberIdsByTask(taskId);
    }

    @Override
    public boolean saveAssignment(TeamAssignmentDTO teamAssignment) throws SQLException, ClassNotFoundException {
        return teamAssignmentDAO.save(EntityDTOMapper.mapDTOToEntity(teamAssignment, TeamAssignment.class));
    }

    @Override
    public List<TeamAssignmentDTO> getAssignmentsByTaskId(Long taskId) throws SQLException, ClassNotFoundException {
        List<TeamAssignment> assignmentsByTas = teamAssignmentDAO.getAssignmentsByTaskId(taskId);
        return EntityDTOMapper.mapEntityListToDTOList(assignmentsByTas, TeamAssignmentDTO.class);
    }
}
