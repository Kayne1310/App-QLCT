package com.example.projectappqlct;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import com.example.projectappqlct.Model.Expense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseViewPagerAdapter extends FragmentStateAdapter {
    private List<String> monthYearList = new ArrayList<>();
    private Map<String, List<Expense>> expenseByMonthYear = new HashMap<>();


    public ExpenseViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public void updateData(List<String> monthYearList, Map<String, List<Expense>> expenseByMonthYear) {
        this.monthYearList = new ArrayList<>(monthYearList);
        this.expenseByMonthYear = new HashMap<>(expenseByMonthYear);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String monthYear = monthYearList.get(position);

        // Nếu là tab mặc định "Create Expense", trả về DefaultTabFragment
        if (monthYear.equals("no data")) {
            return new DefaultTabFragmentExpense();
        } else {
            // Nếu là tab có dữ liệu, trả về TabFragment với danh sách ngân sách tương ứng
            List<Expense> expenses = expenseByMonthYear.get(monthYear);
            return TabFragment_History.newInstance(monthYear, expenses);  // Pass monthYear here
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
}
