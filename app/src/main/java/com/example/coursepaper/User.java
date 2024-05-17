package com.example.coursepaper;

public class User {
    public String username;
    public String email;
    public boolean isAdmin;

    public User() {}

    public User(String username, String email, boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
    }
}
