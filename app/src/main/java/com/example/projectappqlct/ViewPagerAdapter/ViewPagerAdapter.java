package com.example.projectappqlct.ViewPagerAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.projectappqlct.Fragment.BudgetFragment;
import com.example.projectappqlct.Fragment.HistoryFragment;
import com.example.projectappqlct.Fragment.HomeFragment;
import com.example.projectappqlct.Fragment.ProfileFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behaviorResumeOnlyCurrentFragment) {
        super(fm, behaviorResumeOnlyCurrentFragment);
        // Khởi tạo các Fragment chỉ một lần
        fragmentList.add(new HomeFragment());
        fragmentList.add(new HistoryFragment());
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
