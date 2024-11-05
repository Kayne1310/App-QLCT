package com.example.projectappqlct;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.projectappqlct.Model.Budget;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private BudgetViewPagerAdapter adapter;
    private Map<String, List<Budget>> budgetByMonthYear = new HashMap<>();
    private FirebaseFirestore db;
    private List<String> monthYearList = new ArrayList<>();

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

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        db = FirebaseFirestore.getInstance();

        // Create adapter for ViewPager
        adapter = new BudgetViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Load data from Firestore and setup tabs
        loadBudgetsAndSetupTabs();

        return view;
    }

    public void loadBudgetsAndSetupTabs() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userString = user.getUid();
        if (user != null) {
//            budgetByMonthYear.clear();
//            monthYearList.clear();
            db.collection("users").document(userString)
                    .collection("Budgets")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<Budget> budgets = queryDocumentSnapshots.toObjects(Budget.class);
                            groupBudgetsByMonthYear(budgets);
                            setupTabLayoutAndViewPager(); // Setup tabs and ViewPager

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

    private void groupBudgetsByMonthYear(List<Budget> budgets) {
        budgetByMonthYear.clear();

        for (Budget budget : budgets) {
            String monthYear = budget.getCalendar().substring(3); // Get month/year from "dd/MM/yyyy"
            if (!budgetByMonthYear.containsKey(monthYear)) {
                budgetByMonthYear.put(monthYear, new ArrayList<>());
            }
            budgetByMonthYear.get(monthYear).add(budget);
        }
    }



    private void setupTabLayoutAndViewPager() {
        monthYearList = new ArrayList<>(budgetByMonthYear.keySet());
        Collections.sort(monthYearList);

        adapter.updateData(monthYearList, budgetByMonthYear);
        // Tháo và gán lại adapter
        viewPager.setAdapter(null);
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(monthYearList.get(position));
        }).attach();
    }

    private void showDefaultTab() {
        List<String> defaultTab = Collections.singletonList("Create Budget");
        adapter.updateData(defaultTab, new HashMap<>());

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText("Create Budget");
        }).attach();
    }

    public void addBudgetData(Budget budget) {
        String monthYear = budget.getCalendar().substring(3);

        if (!budgetByMonthYear.containsKey(monthYear)) {
            budgetByMonthYear.put(monthYear, new ArrayList<>());
            monthYearList.add(monthYear);
            Collections.sort(monthYearList);
        }
        budgetByMonthYear.get(monthYear).add(budget);

        adapter.updateData(monthYearList, budgetByMonthYear);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(monthYearList.get(position));
        }).attach();
    }


//    private void showDefaultTab() {
//        List<String> defaultTab = Collections.singletonList("Create Budget");
//        adapter.setData(defaultTab, new HashMap<>()); // Update adapter data for default tab
//        adapter.notifyDataSetChanged(); // Notify adapter to refresh data
//
//        // Setup TabLayoutMediator for default tab
//        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
//            tab.setText("Create Budget");
//        }).attach();
//    }


// big update
//    private void setupTabLayoutAndViewPager() {
//        List<String> monthYearList = new ArrayList<>(budgetByMonthYear.keySet());
//        Collections.sort(monthYearList);
//
//        adapter.setData(monthYearList, budgetByMonthYear);
//
//        // Tháo và gán lại adapter
//        viewPager.setAdapter(null);
//        viewPager.setAdapter(adapter);
//
//        // Thiết lập lại TabLayoutMediator
//        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
//            tab.setText(monthYearList.get(position));
//        }).attach();
//    }




}


