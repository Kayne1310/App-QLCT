package com.example.projectappqlct;

import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.projectappqlct.Adapter.ExpenseHomeAdapter;
import com.example.projectappqlct.Adapter.TransactionRecentAdapter;
import com.example.projectappqlct.Helper.DemoBase;
import com.example.projectappqlct.Helper.MyMarkerView;
import com.example.projectappqlct.Helper.QueryCallBack;
import com.example.projectappqlct.Model.Expense;
import com.example.projectappqlct.Model.GroupExpense;
import com.example.projectappqlct.Model.TransactionItem;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView amountBudget, moneyexpense, txtpercent;
    private BarChart barChart;
    private FirebaseAuth auth;
    private FirebaseUser user;


    private RecyclerView expenseRecyclerView, transactionRecyclerView;
    private ExpenseHomeAdapter expenseAdapter;
    private TransactionRecentAdapter transactionAdapter;
    private List<Expense> expenseList;
    private TextView emptyTextView,nameUser;
    private TextView emptyTransaction;


    // Variables for bar data sets
    BarDataSet barDataSet1, barDataSet2;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // ArrayList for storing entries
    ArrayList<BarEntry> barEntries;

    // Creating a string array for displaying days
    private List<String> xValues = Arrays.asList("Last Month", "This Month");

    private TextView tvX, tvY;


    public HomeFragment() {
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);





        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        expenseRecyclerView = view.findViewById(R.id.expense);
        transactionRecyclerView = view.findViewById(R.id.transaction);
        barChart = view.findViewById(R.id.chart1);
        nameUser=view.findViewById(R.id.nameUser);

        barChart.setVisibility(View.INVISIBLE);



        expenseRecyclerView.setVisibility(View.INVISIBLE);
        transactionRecyclerView.setVisibility(View.INVISIBLE);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String userString = user.getUid();
        db.collection("users").document(userString)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){
                            DocumentSnapshot document=task.getResult();
                            if(document.exists()){
                                String nameuser=document.getString("name");
                                nameUser.setText(nameuser);
                            }
                        }
                        else {
                            Log.w(TAG,"error get username",task.getException());
                        }
                    }
                });

        final int totalQueries = 2; // Số lượng truy vấn (ở đây là Budgets và Expenses)
        final int[] queryCounter = {totalQueries}; // Dùng mảng để giữ giá trị có thể thay đổi

        // Hàm kiểm tra khi tất cả các truy vấn hoàn tất
        Runnable onAllQueriesComplete = () -> {
            if (queryCounter[0] == 0) { // Khi queryCounter giảm xuống 0
                progressBar.setVisibility(View.GONE); // Ẩn ProgressBar
                expenseRecyclerView.setVisibility(View.VISIBLE); // Hiển thị các RecyclerView
                transactionRecyclerView.setVisibility(View.VISIBLE);
                barChart.setVisibility(View.VISIBLE);
            }

        };

        /// lay totalBudget
        queryData("Budgets", new QueryCallBack() {
            @Override
            public void onQueryCompleteTotal(int totalAmount) {

                NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
                String formattedAmount = numberFormat.format(totalAmount);

                amountBudget = view.findViewById(R.id.money);
                amountBudget.setText(formattedAmount + " VND");

                queryCounter[0]--; // Giảm queryCounter khi hoàn tất truy vấn
                onAllQueriesComplete.run();
            }

            @Override
            public void onQueryCompleteExpense(int totalExpenseThisMonth, int totalExpenseLastMonth, List<Expense> allExpensesThisMonth,Map<String, GroupExpense> groupDataMap) {

            }


        });


        //lay total Expense
        queryData("Expenses", new QueryCallBack() {
            @Override
            public void onQueryCompleteTotal(int totalAmount) {

            }

            @Override
            public void onQueryCompleteExpense(int totalExpenseThisMonth, int totalExpenseLastMonth, List<Expense> allExpensesThisMonth,Map<String, GroupExpense> groupDataMap) {

                //Handle Recycerview Expense
                NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
                String formattedAmount = numberFormat.format(totalExpenseThisMonth);

                moneyexpense = view.findViewById(R.id.moneyexpense);
                moneyexpense.setText(String.valueOf(formattedAmount + " VND"));
                txtpercent = view.findViewById(R.id.txtpercent);


                // Kiểm tra các trường hợp cho totalExpenseThisMonth và totalExpenseLastMonth
                if (totalExpenseLastMonth == 0) {
                    if (totalExpenseThisMonth > 0) {
                        // Nếu tháng này có chi tiêu nhưng tháng trước không có
                        txtpercent.setText("100.00%");
                        txtpercent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_trending_up_24, 0, 0, 0);
                    } else {
                        // Cả hai tháng đều không có chi tiêu
                        txtpercent.setText("0.00%");
                    }
                } else if (totalExpenseThisMonth == 0) {
                    // Nếu tháng này không có chi tiêu nhưng tháng trước có
                    txtpercent.setText("0.00%");
                    txtpercent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_trending_down_24, 0, 0, 0);
                } else {
                    // Cả hai tháng đều có chi tiêu
                    float percent = ((float) (totalExpenseThisMonth - totalExpenseLastMonth) / totalExpenseLastMonth) * 100;
                    percent = Math.min(percent, 100); // Đảm bảo phần trăm không vượt quá 100%

                    // Đặt giá trị phần trăm vào TextView
                    txtpercent.setText(String.format(Locale.US, "%.2f%%", percent));

                    if (totalExpenseThisMonth > totalExpenseLastMonth) {
                        // Nếu tháng này chi tiêu nhiều hơn tháng trước
                        txtpercent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_trending_up_24, 0, 0, 0);
                    } else {
                        // Nếu tháng này chi tiêu ít hơn hoặc bằng tháng trước
                        txtpercent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_trending_down_24, 0, 0, 0);
                    }
                }

                //bar Chart


                // Preparing data entries for last month and this month
                ArrayList<BarEntry> entries = new ArrayList<>();
                entries.add(new BarEntry(0, totalExpenseLastMonth));  // Chi tiêu tháng trước
                entries.add(new BarEntry(1, totalExpenseThisMonth));  // Chi tiêu tháng này

                // Customizing Y-axis
                YAxis yAxisLeft = barChart.getAxisLeft();
                yAxisLeft.setAxisMinimum(0f);  // Bắt đầu từ 0
                yAxisLeft.setDrawGridLines(false);  // Ẩn đường lưới
                yAxisLeft.setDrawAxisLine(false);   // Ẩn trục Y bên trái
                yAxisLeft.setEnabled(false);        // Không hiển thị nhãn bên trục Y trái

                // Disabling right Y-axis
                YAxis yAxisRight = barChart.getAxisRight();
                yAxisRight.setEnabled(false);

                // Customizing X-axis
                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);  // Ẩn đường lưới dọc
                xAxis.setDrawAxisLine(false);
                xAxis.setGranularity(1f);       // Đảm bảo chỉ số không bị lặp
                xAxis.setLabelCount(xValues.size());// Ẩn trục X

                // Setting up data set
                BarDataSet dataSet = new BarDataSet(entries, "");
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                dataSet.setValueTypeface(Typeface.SANS_SERIF);
                dataSet.setValueTextSize(12f); // Hiển thị giá trị trên các thanh


                // Tạo ValueFormatter tùy chỉnh để thêm "VND"
                dataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getBarLabel(BarEntry barEntry) {
                        float value = barEntry.getY();
                        if (value >= 1_000_000) {
                            return String.format("%.1fM", value / 1000000); // Triệu
                        } else if (value >= 1_000) {
                            return String.format("%.0fK", value / 1000); // Nghìn
                        } else {
                            return String.valueOf((int) value); // Giá trị nguyên nếu nhỏ hơn 1000
                        }
                    }
                });

                // Assigning data to chart
                BarData barData = new BarData(dataSet);
                barData.setBarWidth(0.3f);
                barChart.setData(barData);

                // Disable chart description
                barChart.getDescription().setEnabled(false);
                barChart.getLegend().setEnabled(false);

                // Refresh chart
                barChart.invalidate();


                // Initialize RecyclerViews

                emptyTextView = view.findViewById(R.id.emptyTextView);

                // Add data to expenseList here
                expenseAdapter = new ExpenseHomeAdapter(groupDataMap, totalExpenseThisMonth);
                expenseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                expenseRecyclerView.setAdapter(expenseAdapter);
                checkEmptyData(groupDataMap, emptyTextView, expenseRecyclerView);


                // Add data to transactionList here

                transactionAdapter = new TransactionRecentAdapter(allExpensesThisMonth);


                //Handle Recyclerview Transaction recent


                emptyTransaction = view.findViewById(R.id.emptyTransaction);

                transactionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                transactionRecyclerView.setAdapter(transactionAdapter);

                checkEmptyData(allExpensesThisMonth, emptyTransaction, transactionRecyclerView);
                queryCounter[0]--; // Giảm queryCounter khi hoàn tất truy vấn
                onAllQueriesComplete.run();


            }


        });


        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(dataUpdateReceiver,
                new IntentFilter("DATA_UPDATED"));


        // Inflate the layout for this fragment
        return view;


    }

    //Hien thi text neu recylerview nodata
    private <T> void checkEmptyData(T data, TextView emptyTextView, RecyclerView recyclerView) {
        boolean isEmpty = false;

        // Kiểm tra nếu data là List
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            isEmpty = list.isEmpty();
        }
        // Kiểm tra nếu data là Map
        else if (data instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) data;
            isEmpty = map.isEmpty();
        }

        // Cập nhật hiển thị cho TextView và RecyclerView
        if (isEmpty) {
            emptyTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


    public void queryData(String collectionName, QueryCallBack callBack) {

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String userString = user.getUid();

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);

        int previousMonth;
        int previousYear;

        // Tính tháng trước
        if (currentMonth == 1) {
            previousMonth = 12;
            previousYear = currentYear - 1;
        } else {
            previousMonth = currentMonth - 1;
            previousYear = currentYear;
        }

        db.collection("users").document(userString)
                .collection(collectionName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int totalAmount = 0; // Dành cho Budgets
                            int totalThisMonth = 0; // Dành cho Expenses tháng hiện tại
                            int totalLastMonth = 0; // Dành cho Expenses tháng trước


                            List<Expense> allExpensesThisMonth = new ArrayList<>();
                            Map<String, GroupExpense> groupDataMap = new HashMap<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String calendarDate = document.getString("calendar");

                                try {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                    Date date = dateFormat.parse(calendarDate);
                                    Calendar docCalendar = Calendar.getInstance();
                                    docCalendar.setTime(date);

                                    int docMonth = docCalendar.get(Calendar.MONTH) + 1;
                                    int docYear = docCalendar.get(Calendar.YEAR);


                                    Integer amount = document.getLong("amount").intValue();


                                    String group = document.getString("group");
                                    String icon = document.getString("icon");
                                    String note = document.getString("note");
                                    if (amount != null) {

                                        Expense expense = new Expense(amount, calendarDate, group, icon, note);

                                        // Gộp amount của các document có cùng group và lưu icon


                                        // Tính tổng cho Budgets hoặc Expenses
                                        if (collectionName.equals("Budgets")) {
                                            if (docMonth == currentMonth && docYear == currentYear) {
                                                totalAmount += amount;

                                            }
                                        } else if (collectionName.equals("Expenses")) {
                                            if (docMonth == currentMonth && docYear == currentYear) {

                                                totalThisMonth += amount;
                                                allExpensesThisMonth.add(expense);

                                                if (groupDataMap.containsKey(group)) {
                                                    groupDataMap.get(group).addAmount(amount);
                                                } else {
                                                    groupDataMap.put(group, new GroupExpense(amount, icon, group));
                                                }
                                            }
                                            if (docMonth == previousMonth && docYear == previousYear) {
                                                totalLastMonth += amount;
                                            }

                                        }
                                    }
                                } catch (ParseException e) {
                                    Log.e(TAG, "Lỗi định dạng ngày tháng", e);
                                }
                            }


                            // Gọi callback phù hợp
                            if (collectionName.equals("Budgets")) {
                                callBack.onQueryCompleteTotal(totalAmount);
                            } else if (collectionName.equals("Expenses")) {
                                callBack.onQueryCompleteExpense(totalThisMonth, totalLastMonth, allExpensesThisMonth,groupDataMap);
                            }
                        } else {
                            Log.d(TAG, "Lỗi khi truy vấn dữ liệu: ", task.getException());
                        }
                    }
                });
    }


    private BroadcastReceiver dataUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("DATA_UPDATED".equals(intent.getAction())) {
                // Gọi lại queryData để tải lại dữ liệu
                queryData("Budgets", new QueryCallBack() {
                    @Override
                    public void onQueryCompleteTotal(int totalAmount) {
                        // Cập nhật lại view cho tổng ngân sách
                        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
                        String formattedAmount = numberFormat.format(totalAmount);
                        amountBudget.setText(formattedAmount + " VND");
                    }

                    @Override
                    public void onQueryCompleteExpense(int totalExpenseThisMonth, int totalExpenseLastMonth, List<Expense> allExpensesThisMonth,Map<String, GroupExpense> groupDataMap) {
                        // Không cần xử lý ở đây vì là truy vấn Budgets
                    }
                });

                queryData("Expenses", new QueryCallBack() {
                    @Override
                    public void onQueryCompleteTotal(int totalAmount) {

                    }

                    @Override
                    public void onQueryCompleteExpense(int totalExpenseThisMonth, int totalExpenseLastMonth, List<Expense> allExpensesThisMonth,Map<String, GroupExpense> groupDataMap) {

                        //Handle Recycerview Expense
                        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
                        String formattedAmount = numberFormat.format(totalExpenseThisMonth);


                        moneyexpense.setText(String.valueOf(formattedAmount + " VND"));


                        // Kiểm tra các trường hợp cho totalExpenseThisMonth và totalExpenseLastMonth
                        if (totalExpenseLastMonth == 0) {
                            if (totalExpenseThisMonth > 0) {
                                // Nếu tháng này có chi tiêu nhưng tháng trước không có
                                txtpercent.setText("100.00%");
                                txtpercent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_trending_up_24, 0, 0, 0);
                            } else {
                                // Cả hai tháng đều không có chi tiêu
                                txtpercent.setText("0.00%");
                            }
                        } else if (totalExpenseThisMonth == 0) {
                            // Nếu tháng này không có chi tiêu nhưng tháng trước có
                            txtpercent.setText("0.00%");
                            txtpercent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_trending_down_24, 0, 0, 0);
                        } else {
                            // Cả hai tháng đều có chi tiêu
                            float percent = ((float) (totalExpenseThisMonth - totalExpenseLastMonth) / totalExpenseLastMonth) * 100;
                            percent = Math.min(percent, 100); // Đảm bảo phần trăm không vượt quá 100%

                            // Đặt giá trị phần trăm vào TextView
                            txtpercent.setText(String.format(Locale.US, "%.2f%%", percent));

                            if (totalExpenseThisMonth > totalExpenseLastMonth) {
                                // Nếu tháng này chi tiêu nhiều hơn tháng trước
                                txtpercent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_trending_up_24, 0, 0, 0);
                            } else {
                                // Nếu tháng này chi tiêu ít hơn hoặc bằng tháng trước
                                txtpercent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_trending_down_24, 0, 0, 0);
                            }
                        }

                        //bar Chart
                        // Preparing data entries for last month and this month
                        ArrayList<BarEntry> entries = new ArrayList<>();
                        entries.add(new BarEntry(0, totalExpenseLastMonth));  // Chi tiêu tháng trước
                        entries.add(new BarEntry(1, totalExpenseThisMonth));  // Chi tiêu tháng này

                        // Customizing Y-axis
                        YAxis yAxisLeft = barChart.getAxisLeft();
                        yAxisLeft.setAxisMinimum(0f);  // Bắt đầu từ 0
                        yAxisLeft.setDrawGridLines(false);  // Ẩn đường lưới
                        yAxisLeft.setDrawAxisLine(false);   // Ẩn trục Y bên trái
                        yAxisLeft.setEnabled(false);        // Không hiển thị nhãn bên trục Y trái

                        // Disabling right Y-axis
                        YAxis yAxisRight = barChart.getAxisRight();
                        yAxisRight.setEnabled(false);

                        // Customizing X-axis
                        XAxis xAxis = barChart.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setDrawGridLines(false);  // Ẩn đường lưới dọc
                        xAxis.setDrawAxisLine(false);
                        xAxis.setGranularity(1f);       // Đảm bảo chỉ số không bị lặp
                        xAxis.setLabelCount(xValues.size());// Ẩn trục X

                        // Setting up data set
                        BarDataSet dataSet = new BarDataSet(entries, "");
                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        dataSet.setValueTypeface(Typeface.SANS_SERIF);
                        dataSet.setValueTextSize(12f); // Hiển thị giá trị trên các thanh


                        // Tạo ValueFormatter tùy chỉnh để thêm "VND"
                        dataSet.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getBarLabel(BarEntry barEntry) {
                                float value = barEntry.getY();
                                if (value >= 1_000_000) {
                                    return String.format("%.1fM", value / 1000000); // Triệu
                                } else if (value >= 1_000) {
                                    return String.format("%.0fK", value / 1000); // Nghìn
                                } else {
                                    return String.valueOf((int) value); // Giá trị nguyên nếu nhỏ hơn 1000
                                }
                            }
                        });

                        // Assigning data to chart
                        BarData barData = new BarData(dataSet);
                        barData.setBarWidth(0.3f);
                        barChart.setData(barData);

                        // Disable chart description
                        barChart.getDescription().setEnabled(false);
                        barChart.getLegend().setEnabled(false);

                        // Refresh chart
                        barChart.invalidate();

                        // Initialize RecyclerViews

                        // Add data to expenseList here
                        expenseAdapter = new ExpenseHomeAdapter(groupDataMap, totalExpenseThisMonth);
                        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        expenseRecyclerView.setAdapter(expenseAdapter);
                        checkEmptyData(groupDataMap, emptyTextView, expenseRecyclerView);

                        // Add data to transactionList here

                        transactionAdapter = new TransactionRecentAdapter(allExpensesThisMonth);


                        //Handle Recyclerview Transaction recent

                        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        transactionRecyclerView.setAdapter(transactionAdapter);

                        checkEmptyData(allExpensesThisMonth, emptyTransaction, transactionRecyclerView);


                    }


                });


            }
        }
    };


}