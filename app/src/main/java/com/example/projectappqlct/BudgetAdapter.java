package com.example.projectappqlct;

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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectappqlct.Model.Budget;
import com.example.projectappqlct.Model.Expense;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    private List<Budget> ListBudget;
    private Context mContext;

    public BudgetAdapter(Context context, List<Budget> listBudget) {
        this.mContext = context;
        // Khởi tạo ListBudget nếu nó null
        this.ListBudget = listBudget != null ? listBudget : new ArrayList<>();
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

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

        // Chuyển đổi từ int (hoặc số nguyên) sang chuỗi
        holder.textviewGroup.setText(budget.getGroup());

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickToDetail(budget);
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

    // Phương thức cập nhật dữ liệu
//    public void updateData(List<Budget> newBudgetList) {
//        ListBudget.clear(); // Xóa dữ liệu cũ
//        if (newBudgetList != null) {
//            ListBudget.addAll(newBudgetList); // Thêm dữ liệu mới
//        }
//        notifyDataSetChanged(); // Cập nhật RecyclerView
//    }

    public class BudgetViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgviewIcon;
        private TextView textviewGroup, textviewAmount;
        private LinearLayout layoutItem;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            imgviewIcon = itemView.findViewById(R.id.imgViewIcon);
            textviewGroup = itemView.findViewById(R.id.textViewGroup);
            textviewAmount = itemView.findViewById(R.id.textViewAmount);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }
}