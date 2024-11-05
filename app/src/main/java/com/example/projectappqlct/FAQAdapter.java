package com.example.projectappqlct;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectappqlct.Model.FAQItem;
import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {
    private List<FAQItem> faqList;

    // Constructor
    public FAQAdapter(List<FAQItem> faqList) {
        this.faqList = faqList;
    }

    // ViewHolder class to hold each FAQ item view
    public static class FAQViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView;
        TextView answerTextView;
        ImageView faqArrow;
        LinearLayout faqQuestionContainer, questionLayout;
        View faqDivider;

        public FAQViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.faq_question);
            answerTextView = itemView.findViewById(R.id.faq_answer);
            faqArrow = itemView.findViewById(R.id.faq_arrow);
            faqQuestionContainer = itemView.findViewById(R.id.faq_question_container);
            questionLayout = itemView.findViewById(R.id.question_layout);
            faqDivider = itemView.findViewById(R.id.faq_divider);
        }
    }

    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each FAQ item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_item, parent, false);
        return new FAQViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQViewHolder holder, int position) {
        FAQItem faqItem = faqList.get(position);

        // Set question and answer text
        holder.questionTextView.setText(faqItem.getQuestion());
        holder.answerTextView.setText(faqItem.getAnswer());

        // Control visibility of answer based on expansion state
        boolean isExpanded = faqItem.isExpanded();
        holder.answerTextView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        // Change background color and arrow direction based on expansion state
        holder.questionLayout.setBackgroundColor(isExpanded ? Color.parseColor("#E0F8E0") : Color.WHITE);
        holder.faqArrow.setImageResource(isExpanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
        holder.faqDivider.setVisibility(isExpanded ? View.GONE : View.VISIBLE);

        // Toggle expansion state on item click
        holder.faqQuestionContainer.setOnClickListener(v -> {
            faqItem.setExpanded(!isExpanded);
            notifyItemChanged(position); // Update the item to reflect the new state
        });
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }
}
