package com.example.projectappqlct.TabFragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.projectappqlct.Fragment.BudgetFragment;
import com.example.projectappqlct.Model.Budget;
import com.example.projectappqlct.R;
import com.example.projectappqlct.ViewPagerAdapter.BudgetViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DefaultTabFragmentBudget#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DefaultTabFragmentBudget extends TabFragment_Budget {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Dialog dialog1;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private BudgetViewPagerAdapter adapter;
    private FirebaseFirestore db;
    private Map<String, List<Budget>> budgetByMonthYear = new HashMap<>();
    private BudgetFragment budgetFragment;

    public DefaultTabFragmentBudget() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DefaultTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DefaultTabFragmentBudget newInstance(String param1, String param2) {
        DefaultTabFragmentBudget fragment = new DefaultTabFragmentBudget();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_default_tab, container, false);
        // Khởi tạo BudgetFragment

        Button createBudgetButton = view.findViewById(R.id.btnCreateBudget);
        createBudgetButton.setOnClickListener(v -> {
           showDialog1();


        });

        return view;
    }

}