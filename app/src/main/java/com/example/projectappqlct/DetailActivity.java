package com.example.projectappqlct;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.example.projectappqlct.Helper.DemoBase;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;


import com.example.projectappqlct.Helper.DemoBase;
import com.example.projectappqlct.Model.Budget;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.StringValue;

import java.util.ArrayList;
import java.util.Calendar;

public class DetailActivity extends DemoBase {
    private TextView detailItemGr, detailItemAm, detailItemCa;
    private Button btnEdit, btnDelete;
    private Budget budget; // Biến để lưu thông tin budget
    private FirebaseFirestore db;
    private Dialog dialog;
    private FirebaseUser user;
    private String userString;
    LinearLayout textViewBack;
    private ImageView detailImgicon;
    private FirebaseAuth auth;
    private CombinedChart chart;
    private final int count = 12;



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
            intent.putExtra("DetailActivity","DetailActivity");
            startActivity(intent);
            finish();

        });


        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }

        budget = (Budget) bundle.get("Object_budget");
        detailItemGr = findViewById(R.id.detail_item_group);
        detailItemGr.setText(budget.getGroup());

        detailItemAm = findViewById(R.id.detail_item_amount);
        detailItemAm.setText(String.valueOf(budget.getAmount() + " VND"));

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


        ////// chart
        setTitle("CombinedChartActivity");

        chart = findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);

// Draw only bars
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR
        });

        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false); // Disable the right Y-axis

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // Set minimum to zero

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Show X-axis only at the bottom
        xAxis.setDrawGridLines(false); // Remove vertical grid lines
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return months[(int) value % months.length];
            }
        });

        CombinedData data = new CombinedData();
        data.setData(generateBarData()); // Set only bar data
        data.setValueTypeface(tfLight);


        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        chart.setData(data);
        chart.invalidate();
    }


    @Override
    protected void saveToGallery() {

    }

//    private LineData generateLineData() {
//
//        LineData d = new LineData();
//
//        ArrayList<Entry> entries = new ArrayList<>();
//
//        for (int abc = 0; abc < count; abc++)
//            entries.add(new Entry(abc + 0.5f, getRandom(15, 5)));
//
//        LineDataSet set = new LineDataSet(entries, "Line DataSet");
//        set.setColor(Color.rgb(240, 238, 70));
//        set.setLineWidth(2.5f);
//        set.setCircleColor(Color.rgb(240, 238, 70));
//        set.setCircleRadius(5f);
//        set.setFillColor(Color.rgb(240, 238, 70));
//        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        set.setDrawValues(true);
//        set.setValueTextSize(10f);
//        set.setValueTextColor(Color.rgb(240, 238, 70));
//
//        set.setAxisDependency(YAxis.AxisDependency.LEFT);
//        d.addDataSet(set);
//
//        return d;
//    }

    private BarData generateBarData() {

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int abc = 0; abc < count; abc++) {
            entries.add(new BarEntry(abc, getRandom(25, 25))); // Giá trị của mỗi cột
        }

        // Tạo BarDataSet với màu #22AC2B và màu khi nhấn là #ADD8E6
        BarDataSet set = new BarDataSet(entries, "Bar 1");
        set.setColor(Color.parseColor("#22AC2B")); // Màu xanh lá cây #22AC2B
        set.setValueTextColor(Color.parseColor("#22AC2B"));
        set.setValueTextSize(10f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        // Thiết lập màu xanh dương nhạt khi nhấn
        set.setHighLightColor(Color.parseColor("#5969F5FF")); // Màu xanh dương nhạt #ADD8E6

        BarData d = new BarData(set);
        d.setBarWidth(0.45f); // Độ rộng cột

        return d;
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
                        dialog.dismiss();  // Đóng dialog sau khi cập nhật thành công
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
                    Toast.makeText(DetailActivity.this, "Budget deleted successfully!", Toast.LENGTH_SHORT).show();
                    Log.i("check delte","delete co chay k");
                    finish(); // Đóng activity sau khi xóa thành công
                })
                .addOnFailureListener(e -> Log.e("TAG", "Error deleting document", e));
    }





}