package com.example.projectappqlct.Fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.anychart.AnyChartView;
import com.example.projectappqlct.ViewPagerAdapter.ExpenseViewPagerAdapter;
import com.example.projectappqlct.Model.Expense;
import com.example.projectappqlct.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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


    private int currentTabPosition = -1;
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
        int defaultPosition = adapter.getCurrentMonthYearPosition();
        if (defaultPosition != -1) { // Nếu tìm thấy vị trí của tháng-năm hiện tại
            viewPager.setCurrentItem(defaultPosition, false);
        }


        viewPager.setAdapter(adapter);
        // Kiểm tra nếu có intent từ DetailExpense


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

        //load lai khi create expense  o history
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(expenseAddedReceiver, new IntentFilter("EXPENSE_ADDED"));
        // Load data from Firestore and setup tabs
        loadHistoryAndSetupTabs();
        // Quay lại tab đã lưu nếu có
        if (currentTabPosition != -1 && currentTabPosition < monthYearList.size()) {
            viewPager.setCurrentItem(currentTabPosition, false);
        }

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

        // Sử dụng SimpleDateFormat để phân tích ngày
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (Expense expense : expenses) {
            String monthYear = expense.getCalendar().substring(3); // Get month/year from "dd/MM/yyyy"

            // Chuyển đổi ngày từ chuỗi sang đối tượng Date
            Date expenseDate = null;
            try {
                expenseDate = format.parse(expense.getCalendar());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Kiểm tra nếu danh sách chưa có tháng/năm
            if (!expenseByMonthYear.containsKey(monthYear)) {
                expenseByMonthYear.put(monthYear, new ArrayList<>());
            }

            // Thêm chi tiêu vào danh sách
            expenseByMonthYear.get(monthYear).add(expense);
        }

        // Sắp xếp các chi tiêu trong từng tháng/năm theo ngày từ gần nhất đến xa nhất
        for (String monthYear : expenseByMonthYear.keySet()) {
            List<Expense> expenseList = expenseByMonthYear.get(monthYear);

            // Sắp xếp bằng cách sử dụng Comparator
            Collections.sort(expenseList, new Comparator<Expense>() {
                @Override
                public int compare(Expense e1, Expense e2) {
                    try {
                        Date date1 = format.parse(e1.getCalendar());
                        Date date2 = format.parse(e2.getCalendar());
                        return date2.compareTo(date1); // So sánh theo thứ tự giảm dần
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0; // Nếu có lỗi xảy ra, không thay đổi vị trí
                    }
                }
            });
        }
    }




    private void setupTabLayoutAndViewPagerHistory() {
        monthYearList = new ArrayList<>(expenseByMonthYear.keySet());
        Collections.sort(monthYearList);

        adapter.updateData(monthYearList, expenseByMonthYear);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            String monthYear = monthYearList.get(position);
            tab.setText(monthYear);  // Set month/year as tab title
        }).attach();



        // Chuyển đến tab tháng-năm hiện tại
        int defaultPosition = adapter.getCurrentMonthYearPosition();
        if (defaultPosition != -1) {
            viewPager.setCurrentItem(defaultPosition, false);
        }


    }


    private void showDefaultTab() {
        List<String> defaultTab = Collections.singletonList("no data");
        adapter.updateData(defaultTab, new HashMap<>());

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText("No data");
        }).attach();
    }

    private BroadcastReceiver expenseAddedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Khi nhận được thông báo EXPENSE_ADDED, tải lại dữ liệu

            loadHistoryAndSetupTabs();

        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Huỷ đăng ký BroadcastReceiver để tránh rò rỉ bộ nhớ
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(expenseAddedReceiver);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e("Test Load","History Fragment");
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("Test reload","History Fragment");
        loadHistoryAndSetupTabs();
    }




}