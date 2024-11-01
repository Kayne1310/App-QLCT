package com.example.projectappqlct;

import static android.icu.lang.UCharacter.toLowerCase;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BudgetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BudgetFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public BudgetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificantFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BudgetFragment newInstance(String param1, String param2) {
        BudgetFragment fragment = new BudgetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        // Tablayout
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        // Tạo adapter cho ViewPager
        BudgetViewPagerAdapter adapter = new BudgetViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Kết nối TabLayout với ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("T1");
                    break;
                case 1:
                    tab.setText("T2");
                    break;
//                case 2:
//                    tab.setText("Tháng 3");
//                    break;
//                case 3:
//                    tab.setText("Tháng 4");
//                    break;
//                case 4:
//                    tab.setText("Tháng 5");
//                    break;
//                case 5:
//                    tab.setText("Tháng 6");
//                    break;
//                case 6:
//                    tab.setText("Tháng 7");
//                    break;
//                case 7:
//                    tab.setText("Tháng 8");
//                    break;
//                case 8:
//                    tab.setText("Tháng 9");
//                    break;
//                case 9:
//                    tab.setText("Tháng 10");
//                    break;
//                case 10:
//                    tab.setText("Tháng 11");
//                    break;
//                case 11:
//                    tab.setText("Tháng 12");
//                    break;
            }
        }).attach();


        return view;
    }






}


