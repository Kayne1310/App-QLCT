package com.example.projectappqlct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectappqlct.Model.Option;

import java.util.List;

public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.OptionViewHolder> {
    private List<Option> optionList;
    private View.OnClickListener onItemClickListener;

    public OptionAdapter(List<Option> optionList, View.OnClickListener onItemClickListener) {
        this.optionList = optionList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_option, parent, false);
        return new OptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
        Option option = optionList.get(position);

        if (option != null) {
            int iconResId = holder.itemView.getContext().getResources().getIdentifier(option.getIcon(), "drawable", holder.itemView.getContext().getPackageName());
            holder.optionIcon.setImageResource(iconResId);
            holder.optionName.setText(option.getName());
            holder.itemView.setTag(option); // Gán Option vào tag
            // Thêm sự kiện click cho item
            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(v);
                    // Chuyển icon và name qua tag của button
                    v.setTag(option); // Gán Option vào tag
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    static class OptionViewHolder extends RecyclerView.ViewHolder {
        ImageView optionIcon;
        TextView optionName;

        public OptionViewHolder(@NonNull View itemView) {
            super(itemView);
            optionIcon = itemView.findViewById(R.id.iconImageView);
            optionName = itemView.findViewById(R.id.nameTextView);
        }
    }
}
