package com.example.projectappqlct.Detail;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.projectappqlct.Adapter.BudgetAdapter;
import com.example.projectappqlct.Helper.DemoBase;
import androidx.activity.EdgeToEdge;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projectappqlct.MainActivity;
import com.example.projectappqlct.Model.Budget;
import com.example.projectappqlct.Model.Expense;
import com.example.projectappqlct.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.widget.ProgressBar;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class DetailActivity extends DemoBase {
    private TextView detailItemGr, detailItemAm, detailItemCa, textViewDeficit, textViewTotalExpense, textViewSuggestedDailyExpense, textViewActualDailyExpense;
    private Button btnEdit, btnDelete;
    private Budget budget; // Biến để lưu thông tin budget
    private FirebaseFirestore db;
    private Dialog dialog;
    private FirebaseUser user;
    private String userString;
    LinearLayout textViewBack;
    private ImageView detailImgicon;
    private FirebaseAuth auth;
    private BarChart chart;
    private final int count = 12;
    private ProgressBar progressBarExpense;
    private Map<String, Integer> expenses = new HashMap<>(); // Lưu amount expense cho từng ngày
    private List<String> days = new ArrayList<>(); // Danh sách các ngày (dd/MM)


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewBack = findViewById(R.id.textviewBack);
        textViewBack.setOnClickListener(v -> {
            // Chuyển sang màn hình chi tiết item
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            intent.putExtra("DetailActivity", "DetailActivity");
            startActivity(intent);
            overridePendingTransition(0, R.anim.exit_to_right);
            finish();

        });

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        chart = findViewById(R.id.chart1);

        budget = (Budget) bundle.get("Object_budget");
        detailItemGr = findViewById(R.id.detail_item_group);
        detailItemGr.setText(budget.getGroup());

        detailItemAm = findViewById(R.id.detail_item_amount);
        // Định dạng số tiền với dấu phẩy
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        String formattedAmount = numberFormat.format(budget.getAmount());
        // Cập nhật TextView với định dạng tiền tệ
        detailItemAm.setText(formattedAmount + " VND");

        detailImgicon = findViewById(R.id.detail_item_imgIcon);
        int iconResId = getResources().getIdentifier(budget.getIcon(), "drawable", getPackageName());  // Lấy tên icon từ budget và chuyển thành resource ID
        detailImgicon.setImageResource(iconResId);  // Set icon vào ImageView

        detailItemCa = findViewById(R.id.detail_item_calendar);
        detailItemCa.setText(budget.getCalendar());

        db = FirebaseFirestore.getInstance();
        budget = (Budget) getIntent().getSerializableExtra("Object_budget");
        // Khởi tạo các nút
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);


        // Thiết lập sự kiện khi nhấn nút Edit
        btnEdit.setOnClickListener(v -> openEditDialog());

        // Thiết lập sự kiện khi nhấn nút Delete
        btnDelete.setOnClickListener(v -> deleteBudget());

        // Chart
        setupChart();

        loadExpensesData();

        // Ánh xạ các View
        progressBarExpense = findViewById(R.id.progressBar1);
        textViewDeficit = findViewById(R.id.textViewDeficit1);
        textViewTotalExpense = findViewById(R.id.TextViewTotalExpense1);
        textViewSuggestedDailyExpense = findViewById(R.id.textViewSuggestedDailyExpense);
        textViewActualDailyExpense = findViewById(R.id.textViewActualDailyExpense);


        // Khởi tạo Firestore và FirebaseAuth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        // Lấy dữ liệu Budget từ Intent
        budget = (Budget) getIntent().getSerializableExtra("Object_budget");

        if (budget != null) {
            int budgetAmount = budget.getAmount();
            int daysInMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
            // Tính "Nên chi hàng ngày"
            int suggestedDailyExpense = budgetAmount / daysInMonth;
            textViewSuggestedDailyExpense.setText(NumberFormat.getNumberInstance(Locale.US).format(suggestedDailyExpense) + " VND");


            progressBarExpense.setMax(budgetAmount); // Đặt max cho ProgressBar

            fetchMatchingExpenses(budget, totalExpense -> {
                getDaysWithExpenses(user.getUid(), budget.getGroup(), days -> {
                    int daysSinceStart = days; // Nhận số ngày từ callback


                    // Kiểm tra nếu tổng chi phí lớn hơn 0 và số ngày trôi qua lớn hơn 0
                    int actualDailyExpense = (totalExpense > 0 && daysSinceStart > 0) ? (totalExpense / daysSinceStart) : 0;

                    textViewActualDailyExpense.setText(NumberFormat.getNumberInstance(Locale.US).format(actualDailyExpense) + " VND");
                });


                // Cập nhật các TextView khác
                updateExpenseViews(budgetAmount, totalExpense);

                progressBarExpense.setProgress(totalExpense); // Cập nhật tiến độ của ProgressBar

                // Hiển thị tổng chi phí lên textViewTotalExpense
                String formattedTotalExpense = NumberFormat.getNumberInstance(Locale.US).format(totalExpense);
                textViewTotalExpense.setText(formattedTotalExpense + " VND");

                // Tính toán bội chi và cập nhật TextView
                int deficit = budgetAmount - totalExpense;
                String formattedDeficit = NumberFormat.getNumberInstance(Locale.US).format(deficit);
                textViewDeficit.setText(formattedDeficit + " VND");

                if (deficit < 0) {
                    textViewDeficit.setTextColor(ContextCompat.getColor(this, R.color.red));
                } else {
                    textViewDeficit.setTextColor(ContextCompat.getColor(this, R.color.green));
                }


            });


        }
    }


    private void fetchMatchingExpenses(Budget budget, BudgetAdapter.OnExpenseFetchListener listener) {
        if (user == null) {
            Log.e("AuthError", "Người dùng chưa đăng nhập");
            listener.onFetch(0);
            return;
        }

        userString = user.getUid();

        // Lấy chuỗi MM/yyyy từ calendar của Budget
        String budgetMonthYear = budget.getCalendar().substring(3); // Giả sử calendar có định dạng dd/MM/yyyy

        db.collection("users").document(userString)
                .collection("Expenses")
                .whereEqualTo("group", budget.getGroup())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int totalExpense = 0;
                        for (DocumentSnapshot document : task.getResult()) {
                            Expense expense = document.toObject(Expense.class);

                            // Lấy chuỗi MM/yyyy từ calendar của Expense
                            String expenseMonthYear = expense.getCalendar().substring(3); // Giả sử calendar có định dạng dd/MM/yyyy

                            // So sánh MM/yyyy của Budget và Expense
                            if (expenseMonthYear.equals(budgetMonthYear)) {
                                totalExpense += expense.getAmount();
                            }
                        }
                        listener.onFetch(totalExpense);
                    } else {
                        Log.e("ExpenseError", "Lỗi khi truy vấn dữ liệu expense", task.getException());
                        listener.onFetch(0);
                    }
                });
    }


    private void getDaysWithExpenses(String userId, String budgetGroup, OnDaysFetchedListener listener) {
        db = FirebaseFirestore.getInstance();
        Set<String> uniqueDays = new HashSet<>();

        db.collection("users").document(userId).collection("Expenses")
                .whereEqualTo("group", budgetGroup)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Expense expense = document.toObject(Expense.class);
                            if (expense != null) {
                                String date = expense.getCalendar();
                                uniqueDays.add(date);
                            }
                        }
                        // Gọi callback với số ngày đã tìm được
                        listener.onDaysFetched(uniqueDays.size());
                    } else {
                        Log.e("FirestoreError", "Lỗi khi truy vấn dữ liệu expenses", task.getException());
                        listener.onDaysFetched(0); // Gọi callback với giá trị 0 nếu có lỗi
                    }
                });
    }


    public interface OnDaysFetchedListener {
        void onDaysFetched(int days);
    }


    private void updateExpenseViews(int budgetAmount, int totalExpense) {
        // Cập nhật các TextView và ProgressBar liên quan đến chi phí
        progressBarExpense.setMax(budgetAmount);
        progressBarExpense.setProgress(totalExpense);

        String formattedTotalExpense = NumberFormat.getNumberInstance(Locale.US).format(totalExpense);
        textViewTotalExpense.setText("Total Expense: " + formattedTotalExpense + " VND");

        int deficit = budgetAmount - totalExpense;
        String formattedDeficit = NumberFormat.getNumberInstance(Locale.US).format(deficit);
        textViewDeficit.setText("Remaining " + formattedDeficit + " VND");

        textViewDeficit.setTextColor(deficit < 0 ? ContextCompat.getColor(this, R.color.red) : ContextCompat.getColor(this, R.color.green));
    }


    @Override
    protected void saveToGallery() {

    }


    private void setupChart() {
        // Initialize chart as a BarChart
        BarChart chart = findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false); // Disable description text
        chart.setBackgroundColor(Color.WHITE); // Set background color
        chart.setDrawGridBackground(false); // Disable grid background
        chart.setDrawBarShadow(false); // Disable bar shadow
        chart.getLegend().setEnabled(false); // Disable legend if not needed


        // Configure right Y-axis (disable it)
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        // Configure left Y-axis (disable it for a cleaner look)
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(false);

        // Configure X-axis settings
        XAxis xAxis = chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Display X-axis at the bottom
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);// Remove vertical grid lines
        xAxis.setAxisMinimum(0f); // Start X-axis from 0 for proper alignment
        xAxis.setGranularity(1f); // Ensure even spacing of labels
        xAxis.setTextSize(14f); // Increase text size for X-axis labels

        // Set up the value formatter for X-axis
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int index = (int) value;
                if (index >= 0 && index < days.size()) {
                    String date = days.get(index);
                    // Giả sử chuỗi ngày được lưu theo định dạng "dd/MM/yyyy"
                    // Tách chuỗi để chỉ lấy "dd/MM"
                    if (date.length() >= 5) {
                        return date.substring(0, 5); // Lấy phần "dd/MM" từ "dd/MM/yyyy"
                    }
                }
                return "";
            }
        });

        // Create and configure BarData for the chart
        BarData barData = new BarData();
        barData.setBarWidth(0.3f); // Adjust bar width for narrower columns

        // Center values on top of bars
        barData.setValueTextSize(12f); // Set text size for values on bars
        barData.setValueTextColor(Color.BLACK); // Set text color for values on bars
        chart.setExtraOffsets(30f, 20f, 10f, 20f);
        // Set the data for the BarChart and refresh it
        chart.setData(barData);
        chart.invalidate(); // Refresh the chart
    }


    // Phương thức để lấy dữ liệu expenses từ Firestore
    private void loadExpensesData() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            String userString = user.getUid();

            // Giả sử bạn có biến `currentBudgetGroup` là nhóm ngân sách bạn muốn so sánh
            String currentBudgetGroup = budget.getGroup(); // Lấy group từ ngân sách

            db.collection("users").document(userString).collection("Expenses")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Expense expense = document.toObject(Expense.class);
                                if (expense != null) {
                                    // Kiểm tra nếu nhóm của expense khớp với nhóm của budget
                                    if (expense.getGroup().equals(currentBudgetGroup)) {
                                        // Lấy calendar từ Firestore dưới dạng chuỗi
                                        String date = expense.getCalendar(); // Dùng trực tiếp chuỗi ngày/tháng
                                        int amount = expense.getAmount();

                                        // Lưu dữ liệu vào map
                                        expenses.put(date, amount);
                                        if (!days.contains(date)) {
                                            days.add(date); // Thêm ngày vào danh sách nếu chưa tồn tại
                                        }
                                    }
                                }
                            }

                            // Tạo biểu đồ sau khi có dữ liệu
                            BarData barData = generateBarData(chart);
                            if (chart != null) {
                                chart.setData(barData);
                                Log.i("BarData", "Generated BarData: " + barData.toString());
                                chart.invalidate();  // Refresh the chart
                            } else {
                                Log.e("ChartError", "BarChart is not initialized.");
                            }
                        } else {
                            Log.e("ExpenseError", "Lỗi khi truy vấn dữ liệu expenses", task.getException());
                        }
                    });
        } else {
            Log.e("AuthError", "Người dùng chưa đăng nhập");
        }
    }


//    // Phương thức định dạng ngày (chuyển từ Date sang chuỗi dd/MM)
//    private String formatDate(Date date) {
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
//        return sdf.format(date);
//    }

    // Tạo dữ liệu cho biểu đồ cột
    private BarData generateBarData(BarChart chart) {
        List<BarEntry> entries = new ArrayList<>();

        // Populate entries from the expenses map
        for (int i = 0; i < days.size(); i++) {
            String date = days.get(i);
            Integer amount = expenses.get(date);
            if (amount != null) {
                entries.add(new BarEntry(i, amount));
            }
        }

        // Create the BarDataSet
        BarDataSet dataSet = new BarDataSet(entries, "Expenses");
        dataSet.setColor(Color.rgb(75, 192, 192));

        // Custom ValueFormatter for the BarDataSet
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Format numbers as "1K" for thousands and "1M" for millions
                if (value >= 1_000_000) {
                    return String.format("%.1fM", value / 1_000_000); // Triệu
                } else if (value >= 1_000) {
                    return String.format("%.1fK", value / 1_000); // Nghìn
                } else {
                    return String.valueOf((int) value); // Giá trị nguyên nếu nhỏ hơn 1000
                }
            }
        });

        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f); // Set the text size for the values

        // Create BarData with the dataset
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.3f); // Adjust bar width for narrower columns

        return barData;
    }


    private void openEditDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_createbudget);

        TextView backToFragment = dialog.findViewById(R.id.textViewCancel);
        backToFragment.setOnClickListener(v -> {
            dialog.dismiss();  // Đóng hộp thoại 1
        });

        // Thiết lập dialog
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            window.setWindowAnimations(R.style.DialogAnimation);
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);
            params.gravity = Gravity.TOP;
            window.setAttributes(params);
        }

        // Tham chiếu đến các thành phần trong dialog
        ImageView imageViewGr = dialog.findViewById(R.id.imageViewGr);
        EditText editTextAmount = dialog.findViewById(R.id.editTextAmount);
        EditText editTextCalendar = dialog.findViewById(R.id.editTextCalendar);
        Button buttonSelect = dialog.findViewById(R.id.btnSelectedOption);
        Button buttonSubmit = dialog.findViewById(R.id.btnAddBudget);

        // Đổ dữ liệu hiện tại vào các trường
        // Chuyển chuỗi icon thành resourceId và hiển thị trong ImageView
        int resourceId = dialog.getContext().getResources().getIdentifier(
                budget.getIcon(), // chuỗi icon từ Firestore
                "drawable",       // loại tài nguyên
                dialog.getContext().getPackageName()
        );

        if (resourceId != 0) {
            imageViewGr.setImageResource(resourceId); // Đặt icon vào ImageView
        } else {
            // Xử lý nếu resourceId không tồn tại
            imageViewGr.setImageResource(R.drawable.baseline_drive_file_rename_outline_24); // icon mặc định nếu cần
        }
        editTextAmount.setText(String.valueOf(budget.getAmount()));
        editTextCalendar.setText(budget.getCalendar());
        buttonSelect.setText(budget.getGroup());

        // Xử lý chọn ngày mới bằng DatePicker
        editTextCalendar.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) ->
                            editTextCalendar.setText(String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)),
                    year, month, day);
            datePickerDialog.show();
        });

        // Xử lý khi nhấn Submit để cập nhật budget
        buttonSubmit.setOnClickListener(v -> {
            String newAmount = editTextAmount.getText().toString().trim();
            String newCalendar = editTextCalendar.getText().toString().trim();
            String newGroup = buttonSelect.getText().toString().trim();

            // Cập nhật budget với dữ liệu mới
            budget.setAmount(Integer.parseInt(newAmount));
            budget.setCalendar(newCalendar);
            budget.setGroup(newGroup);


            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            userString = user.getUid();
            // Cập nhật budget trong Firestore
            db.collection("users").document(userString)
                    .collection("Budgets")
                    .document(budget.getId())
                    .set(budget)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(DetailActivity.this, "Budget updated successfully!", Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();  // Đóng dialog sau khi cập nhật thành công
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> Log.e("TAG", "Error updating document", e));
        });

        dialog.show();  // Hiển thị dialog
    }


    private void deleteBudget() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userString = user.getUid();

        db.collection("users").document(userString)
                .collection("Budgets")
                .document(budget.getId())

                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Truyền kết quả về BudgetFragment
                    // Chuyển sang màn hình chi tiết item
                    Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                    intent.putExtra("DetailActivity", "DetailActivity");
                    startActivity(intent);
                    Toast.makeText(DetailActivity.this, "Budget deleted successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng activity sau khi xóa thành công
                })
                .addOnFailureListener(e -> Log.e("TAG", "Error deleting document", e));
    }


}