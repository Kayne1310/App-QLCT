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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectappqlct.Model.Budget;
import com.example.projectappqlct.Model.Expense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    private List<Budget> ListBudget;
    private Context mContext;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userString;

    public BudgetAdapter(Context context, List<Budget> listBudget) {
        this.mContext = context;
        // Khởi tạo ListBudget nếu nó null
        this.ListBudget = listBudget != null ? listBudget : new ArrayList<>();
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        final Budget budget = ListBudget.get(position);
        if (budget == null) {
            return; // Tránh NullPointerException
        }

        // Lấy ID của icon từ tên chuỗi
        int iconResId = holder.itemView.getContext().getResources().getIdentifier(budget.getIcon(), "drawable", holder.itemView.getContext().getPackageName());

        if (iconResId != 0) {  // Kiểm tra nếu tìm thấy ID icon
            holder.imgviewIcon.setImageResource(iconResId);  // Thiết lập icon từ XML drawable cho ImageView
        } else {
            Log.e("IconError", "Icon not found: " + budget.getIcon());  // Log nếu không tìm thấy icon
        }

        // Định dạng số tiền thành kiểu tiền tệ
        String formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(budget.getAmount());
        holder.textviewAmount.setText(formattedAmount + " VND");

        holder.textviewGroup.setText(budget.getGroup());

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickToDetail(budget);
            }
        });


        // Thiết lập ProgressBar với max là amount của Budget
        int budgetAmount = budget.getAmount();
        holder.progressBarExpense.setMax(budgetAmount);

        // Truy vấn và tính tổng chi phí từ các Expense khớp với Budget
        fetchMatchingExpenses(budget, totalExpense -> {
            // Cập nhật ProgressBar với tổng chi phí từ Expense
            holder.progressBarExpense.setProgress(totalExpense);

            // Tính toán và cập nhật số tiền bội chi
            int deficit = budgetAmount - totalExpense;
            String formattedDeficit = NumberFormat.getNumberInstance(Locale.US).format(deficit);

            holder.textViewDeficit.setText("Remaining " + formattedDeficit + " VND");

            if (deficit < 0) {
                holder.textViewDeficit.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            } else {
                holder.textViewDeficit.setTextColor(ContextCompat.getColor(mContext, R.color.green));
            }



            // Kiểm tra và hiển thị Toast nếu chi phí đạt hoặc vượt ngân sách
            if (totalExpense >= budgetAmount) {
                Toast.makeText(mContext, "Chi phí của bạn đã bằng hoặc lớn hơn ngân sách", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onClickToDetail(Budget budget) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Object_budget", budget);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return ListBudget != null ? ListBudget.size() : 0;
    }


    public class BudgetViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgviewIcon;
        private TextView textviewGroup, textViewDeficit, textviewAmount;
        private LinearLayout layoutItem;
        private ProgressBar progressBarExpense; // Ánh xạ ProgressBar

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            imgviewIcon = itemView.findViewById(R.id.imgViewIcon);
            textviewGroup = itemView.findViewById(R.id.textViewGroup);
            textviewAmount = itemView.findViewById(R.id.textViewAmount);
            layoutItem = itemView.findViewById(R.id.layout_item);
            progressBarExpense = itemView.findViewById(R.id.progressBar);
            textViewDeficit = itemView.findViewById(R.id.textViewDeficit);
        }
    }

    // xử lý việc truy vấn và tính tổng chi phí từ các Expense khớp với Budget
    private void fetchMatchingExpenses(Budget budget, OnExpenseFetchListener listener) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userString = user.getUid();

        if (user == null) {
            Log.e("AuthError", "Người dùng chưa đăng nhập");
            listener.onFetch(0);
            return;
        }

        String budgetMonthYear = budget.getCalendar().substring(3); // Lấy MM/yyyy từ dd/MM/yyyy

        db.collection("users").document(userString)
                .collection("Expenses")
                .whereEqualTo("group", budget.getGroup())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int totalExpense = 0;
                        for (DocumentSnapshot document : task.getResult()) {
                            Expense expense = document.toObject(Expense.class);
                            String expenseMonthYear = expense.getCalendar().substring(3); // Lấy MM/yyyy từ dd/MM/yyyy

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



    public interface OnExpenseFetchListener {
        void onFetch(int totalExpense);
    }


}