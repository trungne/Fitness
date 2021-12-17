package com.main.fitness.data.Model;

public class AppUser {
    private final String uid;
    private final String displayName;
    private final String email;
    private final String photoUrl;
    private final String phoneNumber;
    private final int workoutScore;
    private final UserExperience userExperience;

    public AppUser(String uid, String displayName, String email, String photoUrl, String phoneNumber, int workoutScore, UserExperience userExperience){
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.phoneNumber = phoneNumber;
        this.workoutScore = workoutScore;
        this.userExperience = userExperience;
    }

    public String getUid() {
        return uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getWorkoutScore() {
        return workoutScore;
    }

    public UserExperience getUserExperience() {
        return userExperience;
    }
}