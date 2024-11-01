package com.example.projectappqlct.Model;

public class Expense {
   // ID from Firestore document
    private int amount;
    private String calendar;
    private String group;
    private String icon;
    private String note;

    // Constructor with parameters
    public Expense(int amount, String calendar, String group, String icon, String note) {
        this.amount = amount;
        this.calendar = calendar;
        this.group = group;
        this.icon = icon;
        this.note = note;
    }

    public Expense(){

    }

    public String getNote() {
        return note;
    }


    public void setNote(String note) {
        this.note = note;
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

