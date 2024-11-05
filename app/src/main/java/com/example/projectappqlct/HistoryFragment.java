package com.example.projectappqlct;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.projectappqlct.Model.Budget;
import com.example.projectappqlct.Model.Expense;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private AnyChartView anyChartView;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ExpenseViewPagerAdapter adapter;
    private Map<String, List<Expense>> expenseByMonthYear = new HashMap<>();
    private List<String> monthYearList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userString;
    private TextView totalBudgetTextView;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        tabLayout = view.findViewById(R.id.tabLayoutHistory);
        viewPager = view.findViewById(R.id.viewPagerHistory);
        db = FirebaseFirestore.getInstance();

        // Create adapter for ViewPager
        adapter = new ExpenseViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            Log.e("AuthError", "User is not logged in");
            return view;
        }

        userString = user.getUid();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize the TextView here
        totalBudgetTextView = view.findViewById(R.id.totalAllBudget);

        db.collection("users").document(userString)
                .collection("Budgets")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double totalBudget = 0.0;
                        for (DocumentSnapshot document : task.getResult()) {
                            Double amount = document.getDouble("amount"); // Get the amount field directly
                            if (amount != null) {
                                totalBudget += amount;
                            }
                        }
                        Log.d("TotalBudget", "Total Budget: " + totalBudget);

                        // Format the totalBudget to currency format without currency symbol
                        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00"); // Format with comma for thousands and 2 decimal places
                        String formattedBudget = decimalFormat.format(totalBudget);

                        // Set the text on the TextView here
                        totalBudgetTextView.setText(formattedBudget + " VND");
                    } else {
                        Log.e("BudgetError", "Error querying budget data", task.getException());
                    }
                });


        // Load data from Firestore and setup tabs
        loadHistoryAndSetupTabs();

        return view;
    }


    public void loadHistoryAndSetupTabs() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userString = user.getUid();
        if (user != null) {

            db.collection("users").document(userString)
                    .collection("Expenses")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<Expense> expenses = queryDocumentSnapshots.toObjects(Expense.class);
                            groupHistorysByMonthYear(expenses);
                            setupTabLayoutAndViewPagerHistory(); // Setup tabs and ViewPager

                        } else {
                            // No data, show default tab
                            showDefaultTab();

                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreError", e.getMessage());
                        showDefaultTab(); // Show default tab on error
                    });
        } else {
            // User not logged in, show default tab
            showDefaultTab();
        }
    }


    private void groupHistorysByMonthYear(List<Expense> expenses) {
        expenseByMonthYear.clear();

        for (Expense expense : expenses) {
            String monthYear = expense.getCalendar().substring(3); // Get month/year from "dd/MM/yyyy"
            if (!expenseByMonthYear.containsKey(monthYear)) {
                expenseByMonthYear.put(monthYear, new ArrayList<>());
            }
            expenseByMonthYear.get(monthYear).add(expense);
        }
    }



    private void setupTabLayoutAndViewPagerHistory() {
        monthYearList = new ArrayList<>(expenseByMonthYear.keySet());
        Collections.sort(monthYearList);

        adapter.updateData(monthYearList, expenseByMonthYear);
        viewPager.setAdapter(null);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            String monthYear = monthYearList.get(position);
            tab.setText(monthYear);  // Set month/year as tab title
        }).attach();
    }


    private void showDefaultTab() {
        List<String> defaultTab = Collections.singletonList("no data");
        adapter.updateData(defaultTab, new HashMap<>());

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText("No data");
        }).attach();
    }




}