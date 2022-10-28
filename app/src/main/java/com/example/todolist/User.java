package com.example.todolist;

public class User {

    public String email, password, username, gender;

    public User(){

    }

    public User(String email, String password, String username, String gender){
        this.email = email;
        this.password = password;
        this.username = username;
        this.gender = gender;
    }
}
