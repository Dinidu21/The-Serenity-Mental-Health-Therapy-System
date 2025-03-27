package com.dinidu.lk.pmt.dao.custom;

import com.dinidu.lk.pmt.dao.CrudDAO;
import com.dinidu.lk.pmt.entity.Users;
import javafx.scene.image.Image;
import org.hibernate.Session;

import java.io.FileNotFoundException;
import java.sql.SQLException;


public interface UserDAO extends CrudDAO<Users> {
    String verifyUser(String username, String password) throws SQLException,ClassNotFoundException;
    boolean isEmailRegistered(String email) throws SQLException,ClassNotFoundException;
    boolean updatePassword(String loggedInUsername, String password) throws SQLException,ClassNotFoundException;
    boolean updatePasswordUsingEmail(String userEmail, String newPassword) throws SQLException,ClassNotFoundException;
    Users getUserDetailsByUsername(String loggedInUsername) throws SQLException,ClassNotFoundException;
    void updateProfileImagePath(String username, String imagePath)throws SQLException,ClassNotFoundException;
    String getProfileImagePath(String username) throws SQLException,ClassNotFoundException;
    Long getUserIdByUsername(String username) throws SQLException,ClassNotFoundException;
    String getUserFullNameById(Long userId) throws SQLException,ClassNotFoundException;
    Image getUserProfilePicByUserId(Long createdBy) throws SQLException,ClassNotFoundException, FileNotFoundException;
    boolean updateUserStatus(String username, String status) throws SQLException,ClassNotFoundException;
    Long getUserIdByFullName(String selectedMemberName)throws SQLException,ClassNotFoundException;
    String getUserEmailById(Long id) throws SQLException,ClassNotFoundException;
    String getUserEmailByFullName(String selectedUser) throws SQLException,ClassNotFoundException;
    String getUserNameByEmail(String email)throws SQLException,ClassNotFoundException;
    String getUserEmail(String loggedInUsername) throws SQLException,ClassNotFoundException;

    boolean updateUserRole(int id, String username, Session session)throws SQLException,ClassNotFoundException;
    boolean deletePermissionsInCurrentRole(String username,Session session)throws SQLException,ClassNotFoundException;
    boolean insertPermissionsInCurrentRole(int id, long id1,Session session)throws SQLException,ClassNotFoundException;
}
