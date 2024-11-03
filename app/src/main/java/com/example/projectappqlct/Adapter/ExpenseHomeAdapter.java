package com.example.projectappqlct.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projectappqlct.Model.GroupExpense;
import com.example.projectappqlct.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExpenseHomeAdapter extends RecyclerView.Adapter<ExpenseHomeAdapter.ExpenseViewHolder> {

    private Map<String, GroupExpense> expenseMap;
    private List<String> groupKeys;
    private int totalExpenseThisMonth;

    public ExpenseHomeAdapter(Map<String, GroupExpense> expenseMap, int totalExpenseThisMonth) {
        this.expenseMap = expenseMap;
        this.groupKeys = List.copyOf(expenseMap.keySet()); // Lấy danh sách các nhóm từ Map
        this.totalExpenseThisMonth = totalExpenseThisMonth;
    }

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder holder, int position) {
        // Lấy key của nhóm hiện tại
        String groupKey = groupKeys.get(position);
        GroupExpense groupExpense = expenseMap.get(groupKey);

        if (groupExpense != null) {
            holder.textViewGroup.setText(groupExpense.getGroupName());
            NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
            String formattedAmount = numberFormat.format(groupExpense.getTotalAmount());
            holder.textViewAmount.setText(formattedAmount + " VND");

            // Tính phần trăm chi tiêu của nhóm so với tổng chi tiêu trong tháng
            double percentage = ((double) groupExpense.getTotalAmount() / totalExpenseThisMonth) * 100;
            holder.textViewPercentage.setText(String.format(Locale.US, "%.2f%%", percentage));

            // Đặt icon cho ImageView
            int iconResId = holder.itemView.getContext().getResources().getIdentifier(
                    groupExpense.getIcon(), "drawable", holder.itemView.getContext().getPackageName());

            if (iconResId != 0) {
                holder.imgViewIcon.setImageResource(iconResId);
            } else {
                Log.e("IconError", "Icon not found: " + groupExpense.getIcon());
            }
        }
    }

    @Override
    public int getItemCount() {
        return groupKeys.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView textViewGroup, textViewAmount, textViewPercentage;
        ImageView imgViewIcon;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            textViewGroup = itemView.findViewById(R.id.textViewGroup);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
            textViewPercentage = itemView.findViewById(R.id.textViewPercentage);
            imgViewIcon = itemView.findViewById(R.id.imgViewIcon);
        }
    }
}
