package com.example.projectappqlct.Helper;

import com.example.projectappqlct.Model.Expense;
import com.example.projectappqlct.Model.GroupExpense;

import java.util.List;
import java.util.Map;

public interface QueryCallBack
{
    void onQueryCompleteTotal(int totalAmount);

    void onQueryCompleteExpense(int totalExpenseThisMonth, int totalExpenseLastMonth, List<Expense> allExpensesThisMonth, Map<String, GroupExpense> groupDataMap);
}
