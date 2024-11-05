package com.example.projectappqlct.Model;

public class GroupExpense {
    private int totalAmount;
    private String icon;
    private String groupName;

    public GroupExpense(int amount, String icon, String groupName) {
        this.totalAmount = amount;
        this.icon = icon;
        this.groupName = groupName;
    }

    public void addAmount(int amount) {
        this.totalAmount += amount;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public String getIcon() {
        return icon;
    }

    public String getGroupName() {
        return groupName;
    }
}
