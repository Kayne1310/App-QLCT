package com.example.projectappqlct;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectappqlct.Model.Budget;

import java.util.List;

public class BudgetADT extends RecyclerView.Adapter<BudgetADT.BudgetViewHolder>{
    private List<Budget> ListBudget;

    public BudgetADT(List<Budget> listBudget) {
        this.ListBudget = listBudget;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget,parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        Budget budget = ListBudget.get(position);
        if(budget == null){
            return;
        }

        holder.imgviewIcon.setImageResource(Integer.parseInt(budget.getIcon()));
        holder.textviewGroup.setText(budget.getGroup());
        holder.textviewAmount.setText(budget.getAmount());
    }

    @Override
    public int getItemCount() {
        if(ListBudget != null){
            return ListBudget.size();
        }
        return 0;
    }

    public class BudgetViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgviewIcon;
        private TextView textviewGroup, textviewAmount;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            imgviewIcon = itemView.findViewById(R.id.imgViewIcon);
            textviewGroup = itemView.findViewById(R.id.textViewGroup);
            textviewAmount = itemView.findViewById(R.id.textViewAmount);
        }
    }
}
