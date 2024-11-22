package com.example.oblong;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String profilePicture;
    private String userType;

    public User(String id, String name, String phone, String email, String profilePicture, String userType) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.profilePicture = profilePicture;
        this.userType = userType;
    }
    // Alternate 2
    public User(String id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    // Alternate 3
    public User(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
