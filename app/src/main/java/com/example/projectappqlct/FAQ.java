package com.example.projectappqlct;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectappqlct.Model.FAQItem;

import java.util.ArrayList;
import java.util.List;

public class FAQ extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        // Initialize RecyclerView
        RecyclerView faqRecyclerView = findViewById(R.id.faqRecyclerView);
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create sample FAQ data
        List<FAQItem> faqList = new ArrayList<>();

        faqList.add(new FAQItem("What is CampusExpenseManager?", "CampusExpense Manager is a mobile app for user that helps track spending, set monthly budgets for categories (food, entertainment, education, etc.) and provides detailed reports to manage personal finances more effectively."));
        faqList.add(new FAQItem("How can I track my expense?", "To add an expense, in the middle of the bottom navigation bar on the home page, select the button with the plus symbol and enter the details such as amount, date, notes and select the appropriate category (like rent, food, etc.) and click the \"Add Expense\" button."));
        faqList.add(new FAQItem("Can I create more categories?", "Yes, our app supports creating more subcategories for existing categories. The app supports popular icons"));
        faqList.add(new FAQItem("How do I create a budget in the app?", "To create a budget, go to \"Budget\" on the bottom navigation bar on the home screen, and then select \"Add New Budget\", enter the amount you want to set for each category (such as food, transportation, entertainment) and tap \"Save\". Your budget will be tracked automatically throughout the month."));
        faqList.add(new FAQItem("Does the app provide expense statistics?", "Yes. The app will provide monthly spending charts and reports, including total spending, budget balance, and details of each category for you to track."));
        faqList.add(new FAQItem("When will I receive notifications?", "You will receive notifications when your spending reaches or exceeds the budget you set for categories. This notification helps you better control your spending."));
        faqList.add(new FAQItem("Can I edit my account information?", "Yes. You can click on Profile at the bottom navigation bar on the home page. Then select editProfile to edit personal information such as Email, name, Age and other account details."));
        faqList.add(new FAQItem("How do I view my expense history?", "You can view your spending history by going to “History” in the bottom navigation bar on the home page, which displays recorded expenses over time."));
        faqList.add(new FAQItem("How can I change my account password?", "To change your account password, you can click on the Profile section at the bottom navigation bar on the home page. Then select “change password”, then enter your old password and enter your new password and finally click the “change password” button."));


        // Set up adapter
        FAQAdapter adapter = new FAQAdapter(faqList);
        faqRecyclerView.setAdapter(adapter);

        ImageView backIcon = findViewById(R.id.backIcon);

        // Thiết lập sự kiện click
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Điều hướng đến ProfileFragment
                finish(); // Đóng Activity hiện tại (FAQActivity)
            }
        });
    }

}