package com.example.projectappqlct;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

import com.example.projectappqlct.Model.Expense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>{
    private List<Expense> ListExpense;
    private Context mContext;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userString;


    public ExpenseAdapter(Context context, List<Expense> listExpense) {
        this.mContext = context;
        // Khởi tạo ListExpense nếu nó null
        this.ListExpense = listExpense != null ? listExpense : new ArrayList<>();
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense_history, parent, false);
        return new ExpenseAdapter.ExpenseViewHolder(view);
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        final Expense expense = ListExpense.get(position);
        if (expense == null) {
            return; // Tránh NullPointerException
        }

        // Lấy ID của icon từ tên chuỗi
        int iconResId = holder.itemView.getContext().getResources().getIdentifier(expense.getIcon(), "drawable", holder.itemView.getContext().getPackageName());
        if (iconResId != 0) {  // Kiểm tra nếu tìm thấy ID icon
            holder.imgviewIcon.setImageResource(iconResId);  // Thiết lập icon từ XML drawable cho ImageView
        } else {
            Log.e("IconError", "Icon not found: " + expense.getIcon());  // Log nếu không tìm thấy icon
        }

        // Định dạng số tiền thành kiểu tiền tệ
        String formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(expense.getAmount());
        holder.textviewAmount.setText(formattedAmount + " VND");

        holder.textviewGroup.setText(expense.getGroup());

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickToDetail(expense);
            }
        });

        // Lấy chuỗi ngày từ Firestore
        String dayString = expense.getCalendar(); // Chuỗi ngày dạng "dd/MM/yyyy"

        // Định dạng SimpleDateFormat phù hợp với chuỗi ngày của bạn
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            // Chuyển chuỗi ngày thành Date
            Date date = format.parse(dayString);

            // Tạo Calendar để lấy ngày hiện tại
            Calendar todayCalendar = Calendar.getInstance();
            String formattedToday = format.format(todayCalendar.getTime()); // Định dạng ngày hiện tại

            // Lấy ngày hôm qua
            Calendar yesterdayCalendar = Calendar.getInstance();
            yesterdayCalendar.add(Calendar.DAY_OF_YEAR, -1);
            String formattedYesterday = format.format(yesterdayCalendar.getTime()); // Định dạng ngày hôm qua

            // Format ngày từ Firestore để so sánh
            String formattedDate = format.format(date);

            // Kiểm tra nếu là "Hôm nay", "Hôm qua", hoặc lấy thứ trong tuần
            if (formattedDate.equals(formattedToday)) {
                holder.textviewToday.setText("Hôm nay");
            } else if (formattedDate.equals(formattedYesterday)) {
                holder.textviewToday.setText("Hôm qua");
            } else {
                // Lấy thứ trong tuần nếu không phải hôm nay hoặc hôm qua
                Calendar dayCalendar = Calendar.getInstance(); // Khai báo dayCalendar
                dayCalendar.setTime(date);

                String[] daysOfWeek = {"Chủ Nhật", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy"};
                String dayOfWeek = daysOfWeek[dayCalendar.get(Calendar.DAY_OF_WEEK) - 1];

                holder.textviewToday.setText(dayOfWeek);
            }



            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            userString = user.getUid();

            db.collection("users").document(userString) // `document` ở đây là phương thức, không phải biến
                    .collection("Budgets")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Sử dụng `document` để truy xuất dữ liệu
                                if (expense.getCalendar() != null && expense.getCalendar().length() >= 7) {
                                    String month = expense.getCalendar().substring(3, 5); // Lấy tháng từ chuỗi "dd/MM/yyyy"
                                    holder.textviewMonth.setText("Tháng " + month);
                                }
                            }
                        }
                    });



            // Hiển thị ngày
            holder.textviewDay.setText(formattedDate);

        } catch (ParseException e) {
            e.printStackTrace();
            holder.textviewToday.setText("Ngày không hợp lệ");
        }
    }

    private void onClickToDetail(Expense expense) {
        Intent intent = new Intent(mContext, DetailExpense.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Object_expense", expense);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return ListExpense != null ? ListExpense.size() : 0;
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgviewIcon;
        private TextView textviewGroup, textviewAmount, textviewDay, textviewMonth, textviewToday;
        private LinearLayout layoutItem;


        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            imgviewIcon = itemView.findViewById(R.id.iconExpense);
            textviewGroup = itemView.findViewById(R.id.groupExpense);
            textviewAmount = itemView.findViewById(R.id.amountExpense);
            layoutItem = itemView.findViewById(R.id.layout_item_expense);
            textviewDay = itemView.findViewById(R.id.dayExpense);
            textviewMonth = itemView.findViewById(R.id.monthExpense);
            textviewToday = itemView.findViewById(R.id.todayExpense);
        }
    }
}
