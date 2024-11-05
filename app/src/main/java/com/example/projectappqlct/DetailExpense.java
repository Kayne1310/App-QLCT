package com.example.projectappqlct;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.projectappqlct.Model.Expense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class DetailExpense extends AppCompatActivity {
    private TextView detailItemGr, detailItemAm, detailItemCa, tvEdit, tvbtnDelete;
    private ImageView detailImgicon;
    LinearLayout textViewBack;
    private Expense expense;
    private FirebaseFirestore db;
    private Dialog dialog;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userString;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_expense);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // nút back
        textViewBack = findViewById(R.id.back);
        textViewBack.setOnClickListener(v -> {
            // Chuyển sang màn hình chi tiết item
            Intent intent = new Intent(DetailExpense.this, MainActivity.class);
            intent.putExtra("DetailExpense", "DetailExpense");
            startActivity(intent);
            finish();

        });

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }

        expense = (Expense) bundle.get("Object_expense");
        detailItemGr = findViewById(R.id.tv_group);
        detailItemGr.setText(expense.getGroup());

        detailItemAm = findViewById(R.id.tv_amount);
        // Định dạng số tiền với dấu phẩy
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        String formattedAmount = numberFormat.format(expense.getAmount());
        // Cập nhật TextView với định dạng tiền tệ
        detailItemAm.setText(formattedAmount + " VND");

        detailImgicon = findViewById(R.id.tv_icon);
        // Lấy tên icon từ budget và chuyển thành resource ID
        int iconResId = getResources().getIdentifier(expense.getIcon(), "drawable", getPackageName());
        // Set icon vào ImageView
        detailImgicon.setImageResource(iconResId);

        detailItemCa = findViewById(R.id.tv_calendar);
        detailItemCa.setText(expense.getCalendar());

        db = FirebaseFirestore.getInstance();
        expense = (Expense) getIntent().getSerializableExtra("Object_expense");
        // Khởi tạo các nút
        tvEdit = findViewById(R.id.tv_edit);
        tvbtnDelete = findViewById(R.id.tv_delete);

        // Thiết lập sự kiện khi nhấn nút Edit
        tvEdit.setOnClickListener(v -> openEditDialog());

        // Thiết lập sự kiện khi nhấn nút Delete
        tvbtnDelete.setOnClickListener(v -> deleteBudget());
    }



    private void openEditDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_createexpense);

        // Thiết lập sự kiện khi nhấn nút Cancel
        TextView backToFragment = dialog.findViewById(R.id.textViewCancel);
        backToFragment.setOnClickListener(v -> {
            dialog.dismiss();
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
        Button buttonSubmit = dialog.findViewById(R.id.btnAdd);
        Button buttonNote = dialog.findViewById(R.id.btnSelectedNote);

        // Đổ dữ liệu hiện tại vào các trường
        // Chuyển chuỗi icon thành resourceId và hiển thị trong ImageView
        int resourceId = dialog.getContext().getResources().getIdentifier(
                expense.getIcon(), // chuỗi icon từ Firestore
                "drawable",       // loại tài nguyên
                dialog.getContext().getPackageName()
        );

        if (resourceId != 0) {
            imageViewGr.setImageResource(resourceId); // Đặt icon vào ImageView
        } else {
            // Xử lý nếu resourceId không tồn tại
            imageViewGr.setImageResource(R.drawable.baseline_drive_file_rename_outline_24); // icon mặc định nếu cần
        }
        editTextAmount.setText(String.valueOf(expense.getAmount()));
        editTextCalendar.setText(expense.getCalendar());
        buttonSelect.setText(expense.getGroup());
        buttonNote.setText(expense.getNote());

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

        // Xử lý khi nhấn Submit để cập nhật expense
        buttonSubmit.setOnClickListener(v -> {
            String newAmount = editTextAmount.getText().toString().trim();
            String newCalendar = editTextCalendar.getText().toString().trim();
            String newGroup = buttonSelect.getText().toString().trim();
            String newNote = buttonNote.getText().toString().trim();

            // Cập nhật expense với dữ liệu mới
            expense.setAmount(Integer.parseInt(newAmount));
            expense.setCalendar(newCalendar);
            expense.setGroup(newGroup);
            expense.setNote(newNote);


            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            userString = user.getUid();
            // Cập nhật budget trong Firestore
            db.collection("users").document(userString)
                    .collection("Expenses")
                    .document(expense.getId())
                    .set(expense)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(DetailExpense.this, "Expense updated successfully!", Toast.LENGTH_SHORT).show();
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
                .collection("Expenses")
                .document(expense.getId())

                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Truyền kết quả về BudgetFragment
                    // Chuyển sang màn hình chi tiết item
                    Intent intent = new Intent(DetailExpense.this, MainActivity.class);
                    intent.putExtra("DetailExpense", "DetailExpense");
                    startActivity(intent);
                    Toast.makeText(DetailExpense.this, "Expense deleted successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng activity sau khi xóa thành công
                })
                .addOnFailureListener(e -> Log.e("TAG", "Error deleting document", e));
    }
}