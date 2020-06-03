package com.northumbria.old_ish;

public class FindFriends {

    public String ProfileImage, FullName, ProfileStatus;

    public FindFriends(){

    }
    public FindFriends(String profileImage, String fullName, String profileStatus) {
        this.ProfileImage = profileImage;
        this.FullName = fullName;
        this.ProfileStatus = profileStatus;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getProfileStatus() {
        return ProfileStatus;
    }

    public void setProfileStatus(String profileStatus) {
        ProfileStatus = profileStatus;
    }

}
