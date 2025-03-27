package com.dinidu.lk.pmt.dto;

import com.dinidu.lk.pmt.utils.permissionTypes.Permission;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import lombok.*;

import java.util.Set;

@NoArgsConstructor
@ToString
@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
    private String username;
    private String fullName;
    private String password;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private Set<Permission> permissions;

    public UserDTO(String username, String fullName, String password, String email, String phoneNumber) {
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = null;
        this.permissions = null;
    }
}
