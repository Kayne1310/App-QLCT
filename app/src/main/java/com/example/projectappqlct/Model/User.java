package com.example.projectappqlct.Model;

public class User {
    private String username;
    private String password;
    private String name;
    private int age;
    private String address;
    private String sex; // Có thể là "Male", "Female", v.v.

    // Constructor không tham số (cần thiết cho Firestore)
    public User() {
    }

    // Constructor đầy đủ
    public User(String username, String password, String name, int age, String address, String sex) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.age = age;
        this.address = address;
        this.sex = sex;
    }

    public User(String username,  String name, int age, String address, String sex) {
        this.username = username;
        this.name = name;
        this.age = age;
        this.address = address;
        this.sex = sex;
    }

    // Getters và setters cho từng thuộc tính
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
