package com.example.projectappqlct.Model;

public class User {
    public String username;
    public String password;
    public String name;
    public String age;
    public String address;
    public String sex;

    // Cần có constructor mặc định để Firestore
    public User() {
    }

    public User(String username, String password, String name, String age, String address, String sex) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.age = age;
        this.address = address;
        this.sex = sex;
    }
}
