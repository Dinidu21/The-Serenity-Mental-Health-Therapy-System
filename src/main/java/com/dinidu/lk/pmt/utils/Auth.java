package com.dinidu.lk.pmt.utils;

import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;

import java.sql.SQLException;

public class Auth {
    static QueryDAO queryDAO = new QueryDAOImpl();
    public static void userAccessLevelCheck(){
        String username = SessionUser.getLoggedInUsername();
        System.out.println("Logged in username Inside Create Project: " + username);
        UserRole userRole;
        try {
            userRole = queryDAO.getUserRoleByUsername(username);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if ((userRole != UserRole.ADMIN &&
                userRole != UserRole.RECEPTIONIST)) {
            CustomErrorAlert.showAlert("Access Denied", "You do not have permission to create projects.");
        }
    }
}
