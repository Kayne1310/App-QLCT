package com.example.projectappqlct.Model;

public class TransactionItem {

    private String group;
    private String date;
    private String amount;
    private int progress;

    public TransactionItem() {
    }

    public TransactionItem(String group, String date, String amount, int progress) {
        this.group = group;
        this.date = date;
        this.amount = amount;
        this.progress = progress;
    }


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
