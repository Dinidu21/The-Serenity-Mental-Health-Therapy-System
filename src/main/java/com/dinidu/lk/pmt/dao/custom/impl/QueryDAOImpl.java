package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.config.FactoryConfiguration;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dto.UserDTO;
import com.dinidu.lk.pmt.entity.Roles;
import com.dinidu.lk.pmt.entity.Users;
import com.dinidu.lk.pmt.entity.Permissions;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.permissionTypes.Permission;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class QueryDAOImpl implements QueryDAO {

    @Override
    public UserRole getUserRoleByUsername(String username) throws SQLException, ClassNotFoundException {
        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            Query<Users> query = session.createQuery(
                    "FROM Users u LEFT JOIN FETCH u.role WHERE u.username = :username",
                    Users.class
            );
            query.setParameter("username", username);
            Users user = query.uniqueResult();
            if (user != null) {
                return user.getUserRole();
            }
            return null;
        }
    }

    @Override
    public List<UserDTO> getAllActiveMembersNames() {
        Session session = null;
        try {
            session = FactoryConfiguration.getInstance().getSession();

            List<Users> users = session.createQuery(
                            "FROM Users u JOIN u.role r WHERE u.status = :status AND r.roleName IN (:receptionist)", Users.class)
                    .setParameter("status", Users.Status.ACTIVE)
                    .setParameter("Receptionist", UserRole.RECEPTIONIST.name())
                    .getResultList();

            List<UserDTO> activeMembers = new ArrayList<>();
            for (Users user : users) {
                UserDTO userDTO = new UserDTO();
                userDTO.setUsername(user.getUsername());
                userDTO.setFullName(user.getFullName());
                userDTO.setEmail(user.getEmail());
                activeMembers.add(userDTO);
            }
            return activeMembers;

        } catch (Exception e) {
            System.err.println("Error retrieving active members: " + e.getMessage());
            CustomErrorAlert.showAlert("Error retrieving active members", e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return List.of();
    }

    @Override
    public Set<String> getUserPermissionsByRole(UserRole userRole) throws SQLException, ClassNotFoundException {
        if (userRole == null) {
            return Set.of(); // Return empty set if role is null
        }
        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            Query<String> query = session.createQuery(
                    "SELECT p.permissionName FROM Roles r " +
                            "JOIN r.permissions p WHERE r.roleName = :roleName",
                    String.class
            );
            query.setParameter("roleName", userRole.name().charAt(0) + userRole.name().substring(1).toLowerCase());
            return Set.copyOf(query.getResultList());
        }
    }

    @Override
    public List<UserDTO> getAllUsersWithRolesAndPermissions() throws SQLException, ClassNotFoundException {
        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            Query<Users> query = session.createQuery(
                    "FROM Users u LEFT JOIN FETCH u.role r LEFT JOIN FETCH r.permissions WHERE u.status = :status",
                    Users.class
            );
            query.setParameter("status", Users.Status.ACTIVE);
            List<Users> users = query.getResultList();

            return users.stream().map(user -> {
                UserRole userRole = user.getUserRole();
                Set<Permission> permissions = new HashSet<>();
                if (user.getRole() != null && user.getRole().getPermissions() != null) {
                    permissions = user.getRole().getPermissions().stream()
                            .map(p -> Permission.valueOf(p.getPermissionName()))
                            .collect(Collectors.toSet());
                }
                return new UserDTO(
                        user.getUsername(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getPassword(),
                        userRole,
                        permissions
                );
            }).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error retrieving users with roles and permissions: " + e.getMessage());
            throw e;
        }
    }}