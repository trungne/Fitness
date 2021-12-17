package com.main.fitness.data.Model;

public class User {
    private String UID;
    private String displayName;
    private int phone;
    private String photoURL;
    private int score;
    private int difficulty;

    public User(String uID, String displayName, int phone, String photoURL, int score, int difficulty) {
        this.UID = uID;
        this.phone = phone;
        this.photoURL = photoURL;
        this.score = score;
        this.difficulty = difficulty;
    }

    public String getUID() {
        return UID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPhone() {
        return phone;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public int getScore() {
        return score;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
