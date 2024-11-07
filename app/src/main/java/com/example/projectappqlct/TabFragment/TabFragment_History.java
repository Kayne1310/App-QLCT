package com.example.projectappqlct.TabFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;
import java.text.DecimalFormat;

import com.example.projectappqlct.Adapter.ExpenseAdapter;
import com.example.projectappqlct.Model.Expense;
import com.example.projectappqlct.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabFragment_History#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragment_History extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Expense> expenseList;
    private static final String ARG_EXPENSES = "Expenses";
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userString;
    private String monthYear;
    private TextView totalBudgetinMonth, totalExpenseinMonth, totalBiMandEiM;
    private ProgressBar loadingHistory;
    private RecyclerView recyclerView;// Add this field to store the monthYear

    public TabFragment_History() {
        // Required empty public constructor
    }

//    public static TabFragment_History newInstance(List<Expense> expenses) {
//        TabFragment_History fragment_history = new TabFragment_History();
//        Bundle args = new Bundle();
//        args.putSerializable(ARG_EXPENSES, new ArrayList<>(expenses));
//        fragment_history.setArguments(args);
//        return fragment_history;
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment TabFragment_History.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static TabFragment_History newInstance(String param1, String param2) {
//        TabFragment_History fragment = new TabFragment_History();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }


    public static TabFragment_History newInstance(String monthYear, List<Expense> expenses) {
        TabFragment_History fragment = new TabFragment_History();
        Bundle args = new Bundle();
        args.putSerializable("Expenses", (Serializable) expenses);// Pass monthYear as an argument
        args.putString("monthYear", monthYear);
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

        if (getArguments() != null) {
            monthYear = getArguments().getString("monthYear");
            expenseList = (List<Expense>) getArguments().getSerializable(ARG_EXPENSES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_history, container, false);

        totalBudgetinMonth = view.findViewById(R.id.totalBudgetinMonth);
        totalExpenseinMonth = view.findViewById(R.id.totalExpenseinMonth);
        totalBiMandEiM = view.findViewById(R.id.total_BiMandEiM);
        loadingHistory = view.findViewById(R.id.loadingHistory);
        // recycleview budget
         recyclerView = view.findViewById(R.id.recyclerViewExpenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new ExpenseAdapter(getActivity(), expenseList));

        recyclerView.setVisibility(View.GONE);
        loadingHistory.setVisibility(View.VISIBLE);

        loadMonthlyData(monthYear); // Load data based on monthYear
        return view;
    }

    private void loadMonthlyData(String monthYear) {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Log.e("AuthError", "User is not logged in");
            // Ẩn ProgressBar nếu không có người dùng
            loadingHistory.setVisibility(View.GONE);
            return;
        }

        userString = user.getUid();

        // Initialize totals
        final double[] totalBudgetAmount = {0.0};
        final double[] totalExpenseAmount = {0.0};

        // Query Budgets for the specified monthYear
        db.collection("users").document(userString)
                .collection("Budgets")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String calendar = document.getString("calendar");
                            if (calendar != null && calendar.contains(monthYear)) {
                                Double amount = document.getDouble("amount");
                                if (amount != null) {
                                    totalBudgetAmount[0] += amount;
                                }
                            }
                        }
                        totalBudgetinMonth.setText(formatCurrency(totalBudgetAmount[0]) + " VND");

                        // Query Expenses for the specified monthYear after calculating budgets
                        db.collection("users").document(userString)
                                .collection("Expenses")
                                .get()
                                .addOnCompleteListener(expenseTask -> {
                                    if (expenseTask.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : expenseTask.getResult()) {
                                            String calendar = document.getString("calendar");
                                            if (calendar != null && calendar.contains(monthYear)) {
                                                Double amount = document.getDouble("amount");
                                                if (amount != null) {
                                                    totalExpenseAmount[0] += amount;
                                                }
                                            }
                                        }
                                        totalExpenseinMonth.setText(formatCurrency(totalExpenseAmount[0]) + " VND");

                                        // Calculate difference and update totalBiMandEiM
                                        double difference = totalBudgetAmount[0] - totalExpenseAmount[0];
                                        totalBiMandEiM.setText(formatCurrency(difference) + " VND");

                                        recyclerView.setVisibility(View.VISIBLE);
                                    } else {
                                        Log.e("ExpenseError", "Error querying expense data", expenseTask.getException());
                                    }
                                    loadingHistory.setVisibility(View.GONE);
                                });
                    } else {
                        Log.e("BudgetError", "Error querying budget data", task.getException());
                        loadingHistory.setVisibility(View.GONE);
                    }

                });
    }

    private String formatCurrency(double amount) {
        return new DecimalFormat("#,##0.00").format(amount);
    }

}