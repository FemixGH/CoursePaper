package com.example.coursepaper;

public class User {
    public String username;
    public String email;
    public boolean isAdmin;
    public String imageUrl;

    public User() {
        this.imageUrl = "";
    }

    public User(String username, String email, boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
        this.imageUrl = "";
    }

    public User(String username, String email, boolean isAdmin, String imageUrl) {
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
        this.imageUrl = imageUrl;
    }
}
