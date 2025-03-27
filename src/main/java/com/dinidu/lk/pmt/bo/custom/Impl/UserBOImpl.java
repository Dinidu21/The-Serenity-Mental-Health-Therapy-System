package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.config.FactoryConfiguration;
import com.dinidu.lk.pmt.dao.DAOFactory;
import com.dinidu.lk.pmt.dao.custom.UserDAO;
import com.dinidu.lk.pmt.dto.UserDTO;

import com.dinidu.lk.pmt.entity.Users;
import com.dinidu.lk.pmt.utils.EntityDTOMapper;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.permissionTypes.Permission;
import javafx.scene.image.Image;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.FileNotFoundException;
import java.sql.SQLException;

public class UserBOImpl implements UserBO {

    UserDAO userDAO =
            (UserDAO) DAOFactory.getDaoFactory().
                    getDAO(DAOFactory.DAOTypes.USER);

    @Override
    public boolean isEmailRegistered(String email) throws SQLException, ClassNotFoundException {
        return userDAO.isEmailRegistered(email);
    }

    @Override
    public boolean updatePassword(String loggedInUsername, String password) throws SQLException, ClassNotFoundException {
        return userDAO.updatePassword(loggedInUsername, password);
    }

    @Override
    public boolean updatePasswordUsingEmail(String userEmail, String newPassword) throws SQLException, ClassNotFoundException {
        return userDAO.updatePasswordUsingEmail(userEmail, newPassword);
    }

    @Override
    public UserDTO getUserDetailsByUsername(String loggedInUsername) throws SQLException, ClassNotFoundException {
        return EntityDTOMapper.mapEntityToDTO(userDAO.getUserDetailsByUsername(loggedInUsername), UserDTO.class);
    }

    @Override
    public String verifyUser(String username, String password) throws SQLException, ClassNotFoundException {
        return userDAO.verifyUser(username, password);
    }

    @Override
    public boolean saveUser(UserDTO dto) throws SQLException, ClassNotFoundException {
        return userDAO.save(new Users(
                dto.getUsername(),
                dto.getFullName(),
                dto.getPassword(),
                dto.getEmail(),
                dto.getPhoneNumber()));
    }

    @Override
    public boolean updateUser(UserDTO dto) throws SQLException, ClassNotFoundException {
        return userDAO.update(new Users(
                dto.getUsername(),
                dto.getFullName(),
                dto.getPassword(),
                dto.getEmail(),
                dto.getPhoneNumber()));
    }

    @Override
    public String getUserEmail(String loggedInUsername) throws SQLException, ClassNotFoundException {
        return userDAO.getUserEmail(loggedInUsername);
    }

    @Override
    public void updateProfileImagePath(String username, String imagePath) throws SQLException, ClassNotFoundException {
        userDAO.updateProfileImagePath(username, imagePath);
    }

    @Override
    public Long getUserIdByUsername(String username) throws SQLException, ClassNotFoundException {
        return userDAO.getUserIdByUsername(username);
    }

    @Override
    public String getUserFullNameById(Long userId) throws SQLException, ClassNotFoundException {
        return userDAO.getUserFullNameById(userId);
    }

    @Override
    public Image getUserProfilePicByUserId(Long createdBy) throws SQLException, ClassNotFoundException, FileNotFoundException {
        return userDAO.getUserProfilePicByUserId(createdBy);
    }

    @Override
    public boolean updateUserStatus(String username, String status) throws SQLException, ClassNotFoundException {
        return userDAO.updateUserStatus(username, status);
    }

    @Override
    public Long getUserIdByFullName(String selectedMemberName) throws SQLException, ClassNotFoundException {
        return userDAO.getUserIdByFullName(selectedMemberName);
    }

    @Override
    public String getUserEmailById(Long id) throws SQLException, ClassNotFoundException {
        return userDAO.getUserEmailById(id);
    }

    @Override
    public String getUserEmailByFullName(String selectedUser) throws SQLException, ClassNotFoundException {
        return userDAO.getUserEmailByFullName(selectedUser);
    }

    @Override
    public String getUserNameByEmail(String email) throws SQLException, ClassNotFoundException {
        return userDAO.getUserNameByEmail(email);
    }

    @Override
    public String getProfileImagePath(String username) throws SQLException, ClassNotFoundException {
        return userDAO.getProfileImagePath(username);
    }

    // ============= TRANSACTION ==============
    @Override
    public boolean updateUserRoleAndPermissions(String username, UserDTO user) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();

            // Step 1: Update user's role
            boolean isRoleUpdated = userDAO.updateUserRole(user.getRole().getId(), username, session);
            System.out.println("IsRoleUpdated: " + isRoleUpdated);

            if (!isRoleUpdated) {
                transaction.rollback();
                System.out.println("Role is Not Updated ... Transaction Has Been Rolled Back.");
                return false;
            }
            System.out.println("Role updated successfully for user: " + username + ", New Role ID: " + user.getRole().getId());

            // Step 2: Delete existing permissions for the current role
            boolean permissionsDeleted = userDAO.deletePermissionsInCurrentRole(username, session);
            if (!permissionsDeleted) {
                System.out.println("No existing permissions to delete for user: " + username);
            } else {
                System.out.println("Old permissions deleted successfully for user: " + username);
            }

            // Step 3: Insert new permissions for the role
            for (Permission permissionEnum : user.getPermissions()) {
                // Use the enum's getId() to fetch the corresponding Permissions entity
                boolean permissionInserted = userDAO.insertPermissionsInCurrentRole(
                        user.getRole().getId(),
                        permissionEnum.getId(),
                        session
                );
                if (!permissionInserted) {
                    System.out.println("Failed to insert permission ID: " + permissionEnum.getId() + " for Role ID: " + user.getRole().getId());
                    transaction.rollback();
                    return false;
                }
                System.out.println("Permission inserted successfully: " + permissionEnum.getId() + " for Role ID: " + user.getRole().getId());
            }

            transaction.commit();
            System.out.println("Role and permissions updated successfully for user: " + username);
            return true;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error updating user role and permissions: " + e.getMessage());
            CustomErrorAlert.showAlert("ERROR", "Error updating user role and permissions.");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return false;
    }


    /////////////////// no need to override
    @Override
    public void insert(UserDTO t) throws SQLException, ClassNotFoundException {
    }
    @Override
    public void delete(String idOrName) throws SQLException, ClassNotFoundException {
    }
}
