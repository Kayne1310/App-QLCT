package com.example.projectappqlct.Model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class Budget implements Serializable {
    private String id;
    private int amount;
    private String calendar;
    private String group;
    private String icon;


    public Budget(){}

    // Constructor with parameters
    public Budget(int amount, String calendar, String group, String icon) {
        this.amount = amount;
        this.calendar = calendar;
        this.group = group;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getters and setters for other fields
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCalendar() {
        return calendar;
    }

    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}

