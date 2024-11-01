package com.example.projectappqlct.Model;

public class Option {
    private String icon; // Resource ID của icon
    private String name; // Tên nhóm

    public Option(String icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public Option(){}

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }
}
