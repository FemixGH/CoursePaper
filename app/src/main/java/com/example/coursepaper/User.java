package com.example.coursepaper;

public class User {
    public String id, username, email, password;


    public User() {
    }

    public User(String id, String name, String password, String email) {
        this.id = id;
        this.username = name;
        this.password = password;
        this.email = email;

    }
}