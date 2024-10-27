package com.example.projectappqlct;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class BudgetViewPagerAdapter extends FragmentStateAdapter {

    public BudgetViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TabFragment1(); // Fragment cho Tab 1
//            case 1:
//                return new TabFragment2(); // Fragment cho Tab 2
//            case 2:
//                return new TabFragment3(); // Fragment cho Tab 2
//            case 3:
//                return new TabFragment4(); // Fragment cho Tab 2
//            case 4:
//                return new TabFragment5(); // Fragment cho Tab 2
//            case 5:
//                return new TabFragment6(); // Fragment cho Tab 2
//            case 6:
//                return new TabFragment7(); // Fragment cho Tab 2
//            case 7:
//                return new TabFragment8(); // Fragment cho Tab 2
//            case 8:
//                return new TabFragment9(); // Fragment cho Tab 2
//            case 9:
//                return new TabFragment10(); // Fragment cho Tab 2
//            case 10:
//                return new TabFragment11(); // Fragment cho Tab 2
//            case 11:
//                return new TabFragment12(); // Fragment cho Tab 2
            default:
                return new TabFragment2(); // Fragment cho Tab 3
        }
    }

    @Override
    public int getItemCount() {
        return 12; // Số lượng tab
    }
}
