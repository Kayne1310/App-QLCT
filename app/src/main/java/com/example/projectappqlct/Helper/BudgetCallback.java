package com.example.projectappqlct.Helper;

import com.example.projectappqlct.Model.Budget;

import java.util.List;

public interface BudgetCallback {
    void onCallback(List<Budget> budgetList);
}
