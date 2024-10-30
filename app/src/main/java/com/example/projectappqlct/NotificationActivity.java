package com.example.projectappqlct;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectappqlct.Model.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private TextView cancelTextView; // Khai báo TextView Cancel

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo danh sách thông báo
        notificationList = new ArrayList<>();
        notificationList.add(new Notification("New Message", "You have a new message", "10:00 AM"));
        notificationList.add(new Notification("Update Available", "A new update is available", "12:00 PM"));

        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        // Khởi tạo TextView Cancel và thiết lập OnClickListener
        cancelTextView = findViewById(R.id.textViewCancel);
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Kết thúc NotificationActivity và trở về trang trước đó
            }
        });
    }
}
