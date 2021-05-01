package com.example.kub_dorkar.model;

public class Users1 {
    String username,email,oassword;

    public Users1() {
    }

    public Users1(String username, String email, String oassword) {
        this.username = username;
        this.email = email;
        this.oassword = oassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOassword() {
        return oassword;
    }

    public void setOassword(String oassword) {
        this.oassword = oassword;
    }
}
