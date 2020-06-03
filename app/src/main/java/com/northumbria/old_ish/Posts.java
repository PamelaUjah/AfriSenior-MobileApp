package com.northumbria.old_ish;

public class Posts {

    public String uid, fullName, date, description, time, profileImage, postImage, postStatus;

    public Posts(){


    }

    public Posts(String uid, String fullName, String date, String description, String time, String profileImage, String postImage) {
        this.uid = uid;
        this.fullName = fullName;
        this.date = date;
        this.description = description;
        this.time = time;
        this.profileImage = profileImage;
        this.postImage = postImage;
        this.postStatus = "has uploaded a new post.";
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = "  " + date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return "  " + time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }
}
