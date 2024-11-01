package com.example.projectappqlct;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behaviorResumeOnlyCurrentFragment) {
        super(fm, behaviorResumeOnlyCurrentFragment);
        // Khởi tạo các Fragment chỉ một lần
        fragmentList.add(new HomeFragment());
        fragmentList.add(new HistoryFragment());
        fragmentList.add(new CreateFragment());
        fragmentList.add(new BudgetFragment());
        fragmentList.add(new ProfileFragment());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // Trả về instance đã được tạo trước đó
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size(); // Sử dụng kích thước danh sách để xác định số lượng tab
    }

}
