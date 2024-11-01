package com.example.projectappqlct;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectappqlct.Model.Budget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetViewPagerAdapter extends FragmentStateAdapter {
    private List<String> monthYearList = new ArrayList<>();
    private Map<String, List<Budget>> budgetByMonthYear = new HashMap<>();

    public BudgetViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    // Cập nhật dữ liệu và buộc ViewPager làm mới
//    public void setData(List<String> monthYearList, Map<String, List<Budget>> budgetByMonthYear) {
//        this.monthYearList = new ArrayList<>(monthYearList); // Sao chép dữ liệu để tránh tham chiếu ngoài ý muốn
//        this.budgetByMonthYear = new HashMap<>(budgetByMonthYear); // Sao chép dữ liệu
//
//        notifyDataSetChanged(); // Gọi để ViewPager làm mới các fragment
//    }

    public void updateData(List<String> monthYearList, Map<String, List<Budget>> budgetByMonthYear) {
        this.monthYearList = new ArrayList<>(monthYearList);
        this.budgetByMonthYear = new HashMap<>(budgetByMonthYear);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String monthYear = monthYearList.get(position);

        // Nếu là tab mặc định "Create Budget", trả về DefaultTabFragment
        if (monthYear.equals("Create Budget")) {
            return new DefaultTabFragment();
        } else {
            // Nếu là tab có dữ liệu, trả về TabFragment với danh sách ngân sách tương ứng
            List<Budget> budgets = budgetByMonthYear.get(monthYear);
            return TabFragment.newInstance(budgets);
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



