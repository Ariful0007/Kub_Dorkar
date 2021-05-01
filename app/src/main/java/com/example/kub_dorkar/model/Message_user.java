package com.example.kub_dorkar.model;

public class Message_user {
    String user_id,finalUserName,finalUserSkills,finalUserAbout,finalUserPhone,finalUserEmail,image;

    public Message_user() {
    }

    public Message_user(String user_id, String finalUserName, String finalUserSkills,
                        String finalUserAbout, String finalUserPhone, String finalUserEmail, String image) {
        this.user_id = user_id;
        this.finalUserName = finalUserName;
        this.finalUserSkills = finalUserSkills;
        this.finalUserAbout = finalUserAbout;
        this.finalUserPhone = finalUserPhone;
        this.finalUserEmail = finalUserEmail;
        this.image = image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFinalUserName() {
        return finalUserName;
    }

    public void setFinalUserName(String finalUserName) {
        this.finalUserName = finalUserName;
    }

    public String getFinalUserSkills() {
        return finalUserSkills;
    }

    public void setFinalUserSkills(String finalUserSkills) {
        this.finalUserSkills = finalUserSkills;
    }

    public String getFinalUserAbout() {
        return finalUserAbout;
    }

    public void setFinalUserAbout(String finalUserAbout) {
        this.finalUserAbout = finalUserAbout;
    }

    public String getFinalUserPhone() {
        return finalUserPhone;
    }

    public void setFinalUserPhone(String finalUserPhone) {
        this.finalUserPhone = finalUserPhone;
    }

    public String getFinalUserEmail() {
        return finalUserEmail;
    }

    public void setFinalUserEmail(String finalUserEmail) {
        this.finalUserEmail = finalUserEmail;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
