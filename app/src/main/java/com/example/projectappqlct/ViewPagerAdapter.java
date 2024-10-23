package com.example.projectappqlct;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {


    public ViewPagerAdapter(@NonNull FragmentManager fm, int behaviorResumeOnlyCurrentFragment) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){

            case 0:
                return new HomeFragment();

            case 1:
                return new HistoryFragment();
            case 2:
                return new CreateFragment();
            case 3:
                return new BudgetFragment();
            case 4:
                return  new ProfileFragment();
            default:
                return new HistoryFragment();
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
