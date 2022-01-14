package com.main.fitness.data.Model;

public class AppUser {
    private String uid;
    private String displayName;
    private String email;
    private String photoUrl;
    private String phoneNumber;

    public AppUser(){} // Needed for Firebase

    @Override
    public String toString() {
        return "AppUser{" +
                "uid='" + uid + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    // getters
    public String getUid() {
        return this.uid;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }



    // setters
    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}