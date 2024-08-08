package com.hitachi.library.payload;

import com.hitachi.library.entity.Role;

import java.util.List;

public class UserDTO {
    private String userName;
    private String email;
    private List<Role> roles;


    public UserDTO(String userName, String email, List<Role> roles) {
        this.userName = userName;
        this.email = email;
        this.roles = roles;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
