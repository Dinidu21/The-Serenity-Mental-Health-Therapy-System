package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.config.FactoryConfiguration;
import com.dinidu.lk.pmt.dao.custom.UserDAO;
import com.dinidu.lk.pmt.entity.Permissions;
import com.dinidu.lk.pmt.entity.Roles;
import com.dinidu.lk.pmt.entity.Users;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import javafx.scene.image.Image;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UserDAOImpl implements UserDAO {
    //Working
    @Override
    public boolean save(Users entity) {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get a Hibernate session from FactoryConfiguration
            session = FactoryConfiguration.getInstance().getSession();

            // Begin transaction
            transaction = session.beginTransaction();

            // Hash the password using BCrypt
            String hashedPassword = BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt(12));
            entity.setPassword(hashedPassword);

            // Save the entity
            session.save(entity);

            // Commit the transaction
            transaction.commit();

            return true;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error saving user: " + e.getMessage());
            CustomErrorAlert.showAlert("Error saving user", e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return false;
    }

    //Working
    @Override
    public String verifyUser(String username, String password) {
        Session session = null;
        try {
            // Get a Hibernate session
            session = FactoryConfiguration.getInstance().getSession();

            // Query the Users entity by username
            Users user = session.createQuery("FROM Users WHERE username = :username", Users.class)
                    .setParameter("username", username)
                    .uniqueResult();

            // Check if user exists
            if (user != null) {
                String storedHashedPassword = user.getPassword();

                // Verify the password using BCrypt
                if (BCrypt.checkpw(password, storedHashedPassword)) {
                    return "SUCCESS";
                } else {
                    return "INVALID_PASSWORD";
                }
            } else {
                return "INVALID_USERNAME";
            }
        } catch (Exception e) {
            System.err.println("Error verifying user: " + e.getMessage());
            CustomErrorAlert.showAlert("Error verifying user", e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return "ERROR";
    } //Working

    //Working
    @Override
    public boolean isEmailRegistered(String email) {
        Session session = null;
        try {
            // Get a Hibernate session
            session = FactoryConfiguration.getInstance().getSession();

            // Query to count users with the given email
            Long count = session.createQuery("SELECT COUNT(*) FROM Users WHERE email = :email", Long.class)
                    .setParameter("email", email)
                    .uniqueResult();

            // Return true if count > 0, false otherwise
            return count != null && count > 0;

        } catch (Exception e) {
            System.err.println("Error checking email registration: " + e.getMessage());
            CustomErrorAlert.showAlert("Error checking email registration", e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return false;
    }
    //Working

    @Override
    public Users getUserDetailsByUsername(String loggedInUsername) {
        Session session = null;
        try {
            // Get a Hibernate session
            session = FactoryConfiguration.getInstance().getSession();

            // Query the Users entity by username
            Users user = session.createQuery("FROM Users WHERE username = :username", Users.class)
                    .setParameter("username", loggedInUsername)
                    .uniqueResult();

            // Initialize the role field to avoid LazyInitializationException
            if (user != null) {
                Hibernate.initialize(user.getRole());
            }

            return user;

        } catch (Exception e) {
            System.err.println("Error retrieving user details: " + e.getMessage());
            CustomErrorAlert.showAlert(e.getMessage(), e.getMessage());
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    //Working

    @Override
    public boolean update(Users entity) {
        Session session = null;
        Transaction transaction = null;

        try {
            // Get a Hibernate session
            session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();

            // Fetch the existing user by username
            Users existingUser = session.createQuery("FROM Users WHERE username = :username", Users.class)
                    .setParameter("username", entity.getUsername())
                    .uniqueResult();

            // Check if the user exists
            if (existingUser == null) {
                System.out.println("No user found with username: " + entity.getUsername());
                return false;
            }

            // Check if there are any fields to update
            boolean hasUpdates = false;

            // Update only non-null fields from the provided entity
            if (entity.getFullName() != null) {
                existingUser.setFullName(entity.getFullName());
                hasUpdates = true;
            }
            if (entity.getEmail() != null) {
                existingUser.setEmail(entity.getEmail());
                hasUpdates = true;
            }
            if (entity.getPhoneNumber() != null) {
                existingUser.setPhoneNumber(entity.getPhoneNumber());
                hasUpdates = true;
            }

            // If no fields were updated, return false
            if (!hasUpdates) {
                System.out.println("No fields to update for user: " + entity.getUsername());
                return false;
            }

            // Persist the updated entity
            session.update(existingUser);
            transaction.commit();

            return true;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error updating user: " + e.getMessage());
            CustomErrorAlert.showAlert("Error updating user", e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return false;
    }

    //Working
    @Override
    public boolean updatePasswordUsingEmail(String userEmail, String newPassword) {
        Session session = null;
        Transaction transaction = null;

        try {
            // Get a Hibernate session
            session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();

            // Fetch the user by email
            Users user = session.createQuery("FROM Users WHERE email = :email", Users.class)
                    .setParameter("email", userEmail)
                    .uniqueResult();

            // Check if user exists
            if (user == null) {
                System.out.println("No user found with email: " + userEmail);
                return false;
            }

            // Hash the new password and update the user
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
            user.setPassword(hashedPassword);

            // Persist the update
            session.update(user);
            transaction.commit();

            return true;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error updating password using email: " + e.getMessage());
           CustomErrorAlert.showAlert("Error updating password using email", e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return false;
    }

    //Working
    @Override
    public boolean updatePassword(String loggedInUsername, String password) {
        Session session = null;
        Transaction transaction = null;

        try {
            // Get a Hibernate session
            session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();

            // Fetch the user by username
            Users user = session.createQuery("FROM Users WHERE username = :username", Users.class)
                    .setParameter("username", loggedInUsername)
                    .uniqueResult();

            // Check if user exists
            if (user == null) {
                System.out.println("No user found with username: " + loggedInUsername);
                return false;
            }

            // Hash the new password and update the user
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
            user.setPassword(hashedPassword);

            // Persist the update
            session.update(user);
            transaction.commit();

            return true;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error updating password: " + e.getMessage());
            CustomErrorAlert.showAlert("Error updating password", e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return false;
    }

    //Working
    @Override
    public String getProfileImagePath(String username) {
        Session session = null;

        try {
            // Get a Hibernate session
            session = FactoryConfiguration.getInstance().getSession();

            // Fetch the user by username
            Users user = session.createQuery("FROM Users WHERE username = :username", Users.class)
                    .setParameter("username", username)
                    .uniqueResult();

            // Return the profile image path or null if user not found
            return user != null ? user.getProfileImagePath() : null;

        } catch (Exception e) {
            System.err.println("Error retrieving profile image path: " + e.getMessage());
            CustomErrorAlert.showAlert(e.getMessage(), e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return null;
    }

    //Working
    @Override
    public String getUserEmail(String loggedInUsername) {
        Session session = null;

        try {
            // Get a Hibernate session
            session = FactoryConfiguration.getInstance().getSession();

            // Fetch the user by username
            Users user = session.createQuery("FROM Users WHERE username = :username", Users.class)
                    .setParameter("username", loggedInUsername)
                    .uniqueResult();

            // Return the email or null if user not found
            return user != null ? user.getEmail() : null;

        } catch (Exception e) {
            System.err.println("Error retrieving user email: " + e.getMessage());
            CustomErrorAlert.showAlert("Error retrieving user email", e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return null;
    }

    //Working
    @Override
    public void updateProfileImagePath(String username, String imagePath) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();

            Users user = session.createQuery("FROM Users WHERE username = :username", Users.class)
                    .setParameter("username", username)
                    .uniqueResult();

            if (user != null) {
                user.setProfileImagePath(imagePath);
                session.update(user);
                transaction.commit();
            } else {
                throw new Exception("User not found with username: " + username);
            }

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error updating profile image path: " + e.getMessage());
            CustomErrorAlert.showAlert("Error updating profile image path", e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    //Working Checked 1 Usage
    @Override
    public Long getUserIdByUsername(String username) {
        Session session = null;

        try {
            session = FactoryConfiguration.getInstance().getSession();

            Users user = session.createQuery("FROM Users WHERE username = :username", Users.class)
                    .setParameter("username", username)
                    .uniqueResult();

            return user != null ? user.getId() : null;

        } catch (Exception e) {
            System.err.println("Error retrieving user ID: " + e.getMessage());
            CustomErrorAlert.showAlert("Error retrieving user ID", e.getMessage());
         } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return null;
    }

    //Working Checked 2 Usage
    @Override
    public String getUserFullNameById(Long userId) {
        Session session = null;

        try {
            session = FactoryConfiguration.getInstance().getSession();

            Users user = session.get(Users.class, userId);

            return user != null ? user.getFullName() : null;

        } catch (Exception e) {
            System.err.println("Error retrieving user full name: " + e.getMessage());
            CustomErrorAlert.showAlert("Error retrieving user full name", e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return null;
    }

    // Working Checked 2 Usage
    @Override
    public Image getUserProfilePicByUserId(Long createdBy) {
        Session session = null;

        try {
            session = FactoryConfiguration.getInstance().getSession();

            System.out.println("===Inside getUserProfilePicByUserId method=== ");
            String defaultImagePath = "D:/PL/Java/GDSE/PMS_Layered_Architecture/src/main/resources/asserts/icons/noPic.png";

            Users user = session.get(Users.class, createdBy);
            String imagePath = user != null ? user.getProfileImagePath() : null;

            if (imagePath != null && new File(imagePath).exists()) {
                return new Image(new FileInputStream(imagePath));
            } else {
                System.out.println("No profile image found or invalid path. Using default image.");
                if (defaultImagePath != null && new File(defaultImagePath).exists()) {
                    return new Image(defaultImagePath);
                } else {
                    throw new FileNotFoundException("Default image not found at: " + defaultImagePath);
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("Error loading image: " + e.getMessage());
            CustomErrorAlert.showAlert("Error loading image", e.getMessage());
        } catch (Exception e) {
            System.err.println("Error retrieving profile picture: " + e.getMessage());
            CustomErrorAlert.showAlert("Error retrieving profile picture", e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return null;
    }    // Working

    @Override
    public boolean updateUserStatus(String username, String status) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();

            Users user = session.createQuery("FROM Users WHERE username = :username", Users.class)
                    .setParameter("username", username)
                    .uniqueResult();

            if (user != null) {
                user.setStatus(Users.Status.valueOf(status));
                session.update(user);
                transaction.commit();
                return true;
            }
            return false;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error updating user status: " + e.getMessage());
            CustomErrorAlert.showAlert("Error updating user status", e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return false;
    }
    // Working
    @Override
    public Long getUserIdByFullName(String selectedMemberName) {
        Session session = null;

        try {
            session = FactoryConfiguration.getInstance().getSession();

            Users user = session.createQuery("FROM Users WHERE fullName = :fullName", Users.class)
                    .setParameter("fullName", selectedMemberName)
                    .uniqueResult();

            return user != null ? user.getId() : null;

        } catch (Exception e) {
            System.err.println("Error retrieving user ID by full name: " + e.getMessage());
            CustomErrorAlert.showAlert("Error retrieving user ID by full name", e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return null;
    }
    // Working
    @Override
    public String getUserEmailById(Long id) {
        Session session = null;

        try {
            session = FactoryConfiguration.getInstance().getSession();

            Users user = session.get(Users.class, id);

            return user != null ? user.getEmail() : null;

        } catch (Exception e) {
            System.err.println("Error retrieving user email by ID: " + e.getMessage());
            CustomErrorAlert.showAlert("Error retrieving user email by ID", e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return null;
    }

    // Working
    @Override
    public String getUserEmailByFullName(String selectedUser) {
        Session session = null;

        try {
            session = FactoryConfiguration.getInstance().getSession();

            Users user = session.createQuery("FROM Users WHERE fullName = :fullName", Users.class)
                    .setParameter("fullName", selectedUser)
                    .uniqueResult();

            return user != null ? user.getEmail() : null;

        } catch (Exception e) {
            System.err.println("Error retrieving user email by full name: " + e.getMessage());
            CustomErrorAlert.showAlert(e.getMessage(), e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return null;
    }
    // Working
    @Override
    public String getUserNameByEmail(String email) {
        Session session = null;

        try {
            session = FactoryConfiguration.getInstance().getSession();

            Users user = session.createQuery("FROM Users WHERE email = :email", Users.class)
                    .setParameter("email", email)
                    .uniqueResult();

            return user != null ? user.getFullName() : null;

        } catch (Exception e) {
            System.err.println("Error retrieving user name by email: " + e.getMessage());
            CustomErrorAlert.showAlert("Error retrieving user name by email", e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return null;
    }


    /// Role and Permission

    // Working
    @Override
    public boolean updateUserRole(int roleId, String username, Session session) {
        try {
            // Load the user by username
            Users user = session.createQuery("FROM Users WHERE username = :username", Users.class)
                    .setParameter("username", username)
                    .uniqueResult();

            if (user == null) {
                return false;
            }

            // Load the new role
            Roles role = session.get(Roles.class, roleId);
            if (role == null) {
                return false;
            }

            // Update the user's role
            user.setRole(role);
            session.update(user);

            return true;
        } catch (Exception e) {
            System.err.println("Error updating user role: " + e.getMessage());
            throw e;
        }
    }
    // Working
    @Override
    public boolean deletePermissionsInCurrentRole(String username, Session session) {
        try {
            // Load the user by username
            Users user = session.createQuery("FROM Users WHERE username = :username", Users.class)
                    .setParameter("username", username)
                    .uniqueResult();

            if (user == null || user.getRole() == null) {
                return false;
            }

            // Clear the permissions from the role
            user.getRole().getPermissions().clear();
            session.update(user.getRole());

            return true;
        } catch (Exception e) {
            System.err.println("Error deleting permissions: " + e.getMessage());
            CustomErrorAlert.showAlert("ERROR", "Error deleting permissions.");
        }
        return false;
    }

    // Working
    @Override
    public boolean insertPermissionsInCurrentRole(int roleId, long   permissionId, Session session) {
        try {
            // Load the role
            Roles role = session.get(Roles.class, roleId);
            if (role == null) {
                return false;
            }

            // Load the permission entity from the database using the permissionId
            Permissions permissionEntity = session.get(Permissions.class, permissionId);
            if (permissionEntity == null) {
                return false;
            }

            // Add the permission entity to the role's permissions set
            role.getPermissions().add(permissionEntity);
            session.update(role);

            return true;
        } catch (Exception e) {
            System.err.println("Error inserting permission: " + e.getMessage());
            throw e;
        }
    }


    /////////////////// no need to Impl
    @Override
    public boolean insert(Users user) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String idOrName) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<Users> fetchAll() {
        return List.of();
    }

    @Override
    public Map<String, String> getAllNames() {
        return Map.of();
    }

    @Override
    public List<Users> searchByName(String searchQuery) {
        return List.of();
    }

    @Override
    public Long getIdByName(String taskName) {
        return 0L;
    }
}
