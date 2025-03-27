package com.dinidu.lk.pmt.dao;

import com.dinidu.lk.pmt.dto.UserDTO;
import com.dinidu.lk.pmt.entity.Roles;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface QueryDAO extends SuperDAO{
    ///  =============== USER BASED QUERIES ===============
    UserRole getUserRoleByUsername(String username) throws SQLException,ClassNotFoundException;
    List<UserDTO> getAllActiveMembersNames() throws SQLException, ClassNotFoundException;
    Set<String> getUserPermissionsByRole(UserRole userRole) throws SQLException,ClassNotFoundException;
    List<UserDTO> getAllUsersWithRolesAndPermissions() throws SQLException,ClassNotFoundException;
}
