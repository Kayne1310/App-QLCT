package com.example.projectappqlct.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projectappqlct.Model.Expense;
import com.example.projectappqlct.Model.TransactionItem;
import com.example.projectappqlct.R;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionRecentAdapter extends RecyclerView.Adapter<TransactionRecentAdapter.TransactionViewHolder>
{

    private List<Expense> expenses;

    public TransactionRecentAdapter(List<Expense> expenses) {
        this.expenses = expenses;
        Collections.sort(this.expenses, (expense1, expense2) -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date date1 = dateFormat.parse(expense1.getCalendar());
                Date date2 = dateFormat.parse(expense2.getCalendar());
                return date2.compareTo(date1); // Sắp xếp giảm dần (gần nhất trước)
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_transactions, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        Expense item = expenses.get(position);
        // Hiển thị group
        holder.textViewGroup.setText(item.getGroup());

        // Định dạng lại ngày tháng theo kiểu "EEEE dd MMMM yyyy"
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = originalFormat.parse(item.getCalendar());

            SimpleDateFormat newFormat = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.getDefault());
            String formattedDate = newFormat.format(date);
            holder.itemCalendar.setText(formattedDate); // Hiển thị ngày đã định dạng
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Định dạng amount
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        String formattedAmount = numberFormat.format(item.getAmount());
        holder.textviewAmount.setText(formattedAmount + " VND");

        // Hiển thị icon từ tên icon trong Expense
        int iconResId = holder.itemView.getContext().getResources().getIdentifier(item.getIcon(), "drawable", holder.itemView.getContext().getPackageName());
        if (iconResId != 0) {
            holder.imgViewIcon.setImageResource(iconResId);
        } else {
            Log.e("IconError", "Icon not found: " + item.getIcon());
        }


        // You can set an icon or add click listeners here if needed
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView textViewGroup, itemCalendar, textviewAmount;

        ImageView imgViewIcon;
        public TransactionViewHolder(View itemView) {
            super(itemView);
            textViewGroup = itemView.findViewById(R.id.textViewGroup);
            itemCalendar = itemView.findViewById(R.id.itemCalendar);
            textviewAmount = itemView.findViewById(R.id.textviewAmount);

            imgViewIcon = itemView.findViewById(R.id.imgViewIcon);

        }
    }


}
