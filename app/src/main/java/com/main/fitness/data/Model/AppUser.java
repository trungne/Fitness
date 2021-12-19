package com.main.fitness.data.Model;

import android.text.TextUtils;

public class AppUser {
    private String uid;
    private String displayName;
    private String email;
    private String photoUrl;
    private String phoneNumber;
    private Integer workoutScore;
    private UserLevel userLevel;

    public AppUser(){} // Needed for Firebase

    public AppUser(String uid, String displayName, String email, String photoUrl, String phoneNumber, int workoutScore, UserLevel userLevel){
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.phoneNumber = phoneNumber;
        this.workoutScore = workoutScore;
        this.userLevel = userLevel;
    }

    public boolean hasRequiredInformation(){
        return      !TextUtils.isEmpty(this.uid)
                && !TextUtils.isEmpty(this.displayName)
                && this.userLevel != null;
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "uid='" + uid + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", workoutScore=" + workoutScore +
                ", userLevel=" + userLevel +
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

    public Integer getWorkoutScore() {
        return this.workoutScore;
    }

    public UserLevel getUserLevel() {
        return this.userLevel;
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

    public void setWorkoutScore(Integer workoutScore) {
        this.workoutScore = workoutScore;
    }

    public void setUserLevel(UserLevel userLevel) {
        this.userLevel = userLevel;
    }
}