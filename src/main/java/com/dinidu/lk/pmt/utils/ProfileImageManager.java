package com.dinidu.lk.pmt.utils;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import javafx.scene.image.Image;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ProfileImageManager {
    @Getter
    private static Image currentProfileImage;
    private static final List<ImageUpdateListener> listeners = new ArrayList<>();
    private static final String PROFILE_IMAGES_DIR = "D:/PL/Java/GDSE/PMS_Layered_Architecture/src/main/resources/asserts/images/profile/";

    static UserBO userBO = (UserBO) BOFactory.
            getInstance().
                getBO(BOFactory.BOTypes.USER);

    static {
        try {
            Files.createDirectories(Paths.get(PROFILE_IMAGES_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface ImageUpdateListener {
        void onImageUpdate(Image newImage);
    }

    public static void saveProfileImage(File sourceFile, String username) throws IOException {
        System.out.println("Saving profile image for user: " + username);
        String fileExtension = getFileExtension(sourceFile.getName());
        String uniqueFilename = username + "_" + System.currentTimeMillis() + fileExtension;
        System.out.println("Unique filename: " + uniqueFilename);
        Path destinationPath = Paths.get(PROFILE_IMAGES_DIR, uniqueFilename);
        System.out.println("Destination path: " + destinationPath);
        Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.COPY_ATTRIBUTES);

        Image newImage = new Image(destinationPath.toUri().toString());
        setCurrentProfileImage(newImage);

        saveImagePathToUser(username, destinationPath.toString());
    }

    private static void saveImagePathToUser(String username, String imagePath) {
        try {
            userBO.updateProfileImagePath(username, imagePath);
            SessionUser.setProfileImagePath(imagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setCurrentProfileImage(Image newImage) {
        currentProfileImage = newImage;
        SessionUser.setProfileImage(newImage);
        notifyListeners();
    }

    private static void notifyListeners() {
        for (ImageUpdateListener listener : listeners) {
            listener.onImageUpdate(currentProfileImage);
        }
    }

    public static void addListener(ImageUpdateListener listener) {
        listeners.add(listener);
    }

    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : ".jpg";
    }
}
