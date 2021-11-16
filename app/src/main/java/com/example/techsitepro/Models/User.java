package com.example.techsitepro.Models;

public class User {
    private String userid;
    private String username;
    private String fullname;
    private String email;
    private String profileimage;
    private String bio;

    public User() {

    }

    public User(String userid, String username, String fullname, String email, String profileimage, String bio) {
        this.userid = userid;
        this.username = username;
        this.fullname = fullname;
        this.email = email;
        this.profileimage = profileimage;
        this.bio = bio;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
