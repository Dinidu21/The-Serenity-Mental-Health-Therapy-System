package com.dinidu.lk.pmt.entity;

import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Users implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Roles role;

    @Column(name = "profile_image_path")
    private String profileImagePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE'")
    private Status status = Status.ACTIVE;

    @Transient
    public UserRole getUserRole() {
        if (role == null || role.getRoleName() == null) {
            System.err.println("Role is null for user: " + username);
            return null;
        }
        String roleName = role.getRoleName().trim();
        System.out.println("Raw role_name from DB: '" + roleName + "'");
        try {
            return UserRole.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid role name: '" + roleName + "'");
            return null;
        }
    }

    public Users(String username, String fullName, String password, String email, String phoneNumber) {
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public enum Status {
        ACTIVE, INACTIVE
    }
}