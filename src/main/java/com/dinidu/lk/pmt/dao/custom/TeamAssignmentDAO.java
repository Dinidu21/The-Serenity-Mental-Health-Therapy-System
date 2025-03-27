package com.dinidu.lk.pmt.dao.custom;

import com.dinidu.lk.pmt.dao.CrudDAO;
import com.dinidu.lk.pmt.entity.TeamAssignment;

import java.sql.SQLException;
import java.util.List;

public interface TeamAssignmentDAO extends CrudDAO<TeamAssignment> {
    List<String> getTeamMemberEmailsByTask(long taskId) throws SQLException,ClassNotFoundException;
    List<Long> getTeamMemberIdsByTask(long l) throws SQLException,ClassNotFoundException;
    List<TeamAssignment> getAssignmentsByTaskId(Long taskId) throws SQLException,ClassNotFoundException;
    List<String> getAllTeamMembersNamesByTask(String taskName) throws SQLException, ClassNotFoundException;
}
