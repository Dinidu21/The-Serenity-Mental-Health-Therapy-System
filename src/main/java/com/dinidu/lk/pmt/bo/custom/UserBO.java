package com.dinidu.lk.pmt.bo.custom;

import com.dinidu.lk.pmt.bo.SuperBO;
import com.dinidu.lk.pmt.dto.UserDTO;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.sql.SQLException;


public interface UserBO extends SuperBO {
    boolean saveUser(UserDTO dto) throws SQLException,ClassNotFoundException;
    boolean updateUser(UserDTO dto) throws SQLException,ClassNotFoundException;
    void insert(UserDTO t) throws SQLException,ClassNotFoundException;
    void delete(String idOrName)throws SQLException,ClassNotFoundException;
    String verifyUser(String username, String password) throws SQLException,ClassNotFoundException;
    boolean isEmailRegistered(String email) throws SQLException,ClassNotFoundException;
    boolean updatePassword(String loggedInUsername, String password) throws SQLException,ClassNotFoundException;
    boolean updatePasswordUsingEmail(String userEmail, String newPassword) throws SQLException,ClassNotFoundException;
    UserDTO getUserDetailsByUsername(String loggedInUsername) throws SQLException,ClassNotFoundException;
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
    // TRANSACTION
    boolean updateUserRoleAndPermissions(String username, UserDTO user) throws SQLException,ClassNotFoundException;

}
