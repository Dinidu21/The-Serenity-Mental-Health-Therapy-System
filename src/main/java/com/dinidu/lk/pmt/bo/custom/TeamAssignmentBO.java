package com.dinidu.lk.pmt.bo.custom;

import com.dinidu.lk.pmt.bo.SuperBO;
import com.dinidu.lk.pmt.dto.TeamAssignmentDTO;

import java.sql.SQLException;
import java.util.List;

public interface TeamAssignmentBO extends SuperBO {
    List<String> getAllTeamMembersNamesByTask(String taskName) throws SQLException, ClassNotFoundException;
    List<String> getTeamMemberEmailsByTask(long taskId) throws SQLException, ClassNotFoundException;
    List<Long> getTeamMemberIdsByTask(long tasksId) throws SQLException, ClassNotFoundException;
    boolean saveAssignment(TeamAssignmentDTO teamAssignment) throws SQLException, ClassNotFoundException;
    List<TeamAssignmentDTO> getAssignmentsByTaskId(Long taskId) throws SQLException, ClassNotFoundException;
}
