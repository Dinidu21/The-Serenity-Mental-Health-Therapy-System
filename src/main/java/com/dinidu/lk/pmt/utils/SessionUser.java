package com.dinidu.lk.pmt.utils;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.entity.Roles;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.sql.SQLException;
import java.util.Set;

public class SessionUser {
    @Setter
    @Getter
    private static String loggedInUsername;

    @Setter
    @Getter
    private static UserRole loggedInUserRole;

    @Setter
    @Getter
    private static Set<String> permissions;

    @Setter
    @Getter
    private static Image profileImage;

    @Setter
    @Getter
    private static String profileImagePath;

    static UserBO userBO = (UserBO) BOFactory.
            getInstance().
            getBO(BOFactory.BOTypes.USER);

    public static void initializeSession(String username) {
        clearSession();
        loggedInUsername = username;

        try {
            profileImagePath = userBO.getProfileImagePath(username); //Working
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Profile image path: " + profileImagePath);

        if(profileImagePath == null) {
            System.out.println("Line 43 : Profile image path is null. Loading default image.");
            profileImage = new Image(SessionUser.class.getResourceAsStream("/asserts/icons/noPic.png"));
            ProfileImageManager.setCurrentProfileImage(profileImage);

        }

        if (profileImagePath != null && !profileImagePath.isEmpty()) {
            File imageFile = new File(profileImagePath);
            if (imageFile.exists()) {
                profileImage = new Image(imageFile.toURI().toString());
                System.out.println("Loaded profile image: " + profileImage);
                ProfileImageManager.setCurrentProfileImage(profileImage);
            } else {
                profileImage = new Image(SessionUser.class.getResourceAsStream("/asserts/icons/noPic.png"));
            }
        } else {
            profileImage = new Image(SessionUser.class.getResourceAsStream("/asserts/icons/noPic.png"));
        }
    }

    public static void clearSession() {
        loggedInUsername = null;
        loggedInUserRole = null;
        permissions = null;
        profileImagePath = null;
    }
}
