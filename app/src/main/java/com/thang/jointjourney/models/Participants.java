package com.thang.jointjourney.models;

public class Participants {
    private String username;
    private String userProfilePhoto;
    private String status;


    public Participants() {
    }

    public Participants(String username, String userProfilePhoto, String status) {
        this.username = username;
        this.userProfilePhoto = userProfilePhoto;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserProfilePhoto() {
        return userProfilePhoto;
    }

    public void setUserProfilePhoto(String userProfilePhoto) {
        this.userProfilePhoto = userProfilePhoto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Participants{" +
                "username='" + username + '\'' +
                ", userProfilePhoto='" + userProfilePhoto + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
