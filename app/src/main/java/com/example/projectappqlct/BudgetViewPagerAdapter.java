package com.example.projectappqlct;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectappqlct.Model.Budget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BudgetViewPagerAdapter extends FragmentStateAdapter {
    private List<String> monthYearList = new ArrayList<>();
    private Map<String, List<Budget>> budgetByMonthYear = new HashMap<>();

    public BudgetViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public void updateData(List<String> monthYearList, Map<String, List<Budget>> budgetByMonthYear) {
        this.monthYearList = new ArrayList<>(monthYearList);
        this.budgetByMonthYear = new HashMap<>(budgetByMonthYear);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String monthYear = monthYearList.get(position);

        if (monthYear.equals("Create Budget")) {
            return new DefaultTabFragmentBudget();
        } else {
            List<Budget> budgets = budgetByMonthYear.get(monthYear);
            return TabFragment_Budget.newInstance(budgets, monthYear); // Truyền monthYear
        }
    }


    @Override
    public int getItemCount() {
        return monthYearList.size();
    }

    // Xác định ID duy nhất cho mỗi tab dựa trên vị trí
    @Override
    public long getItemId(int position) {
        return monthYearList.get(position).hashCode();
    }

    // Xác nhận xem một fragment đã tồn tại cần được giữ lại hay hủy
    @Override
    public boolean containsItem(long itemId) {
        for (String monthYear : monthYearList) {
            if (monthYear.hashCode() == itemId) {
                return true;
            }
        }
        return false;
    }

    public int getCurrentMonthYearPosition() {
        // Lấy tháng và năm hiện tại dưới dạng "MM/yyyy"
        String currentMonthYear = new SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());

        // Tìm vị trí của tháng và năm hiện tại trong monthYearList
        return monthYearList.indexOf(currentMonthYear);
    }


}



