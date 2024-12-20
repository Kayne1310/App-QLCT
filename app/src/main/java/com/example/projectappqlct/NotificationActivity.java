package com.example.projectappqlct;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;



import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.projectappqlct.Adapter.NotificationAdapter;
import com.example.projectappqlct.Model.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;



public class NotificationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private TextView cancelTextView; // Khai báo TextView Cancel

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        db = FirebaseFirestore.getInstance();

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String userString = user.getUid();
        notificationList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Tháng bắt đầu từ 0, nên +1
        int currentYear = calendar.get(Calendar.YEAR);

        db.collection("users").document(userString)
                .collection("Notifications")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Notification> notificationList = new ArrayList<>();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String title = document.getString("title");
                                String content = document.getString("content");
                                String time = document.getString("time");

                                try {
                                    Date notificationDate = dateFormat.parse(time);

                                    Calendar notificationCalendar = Calendar.getInstance();
                                    notificationCalendar.setTime(notificationDate);
                                    int notificationMonth = notificationCalendar.get(Calendar.MONTH) + 1;
                                    int notificationYear = notificationCalendar.get(Calendar.YEAR);

                                    // Kiểm tra nếu thông báo thuộc tháng hiện tại
                                    if (notificationMonth == currentMonth && notificationYear == currentYear) {
                                        Notification notification = new Notification(title, content, time);
                                        notificationList.add(notification);
                                    }

                                } catch (ParseException e) {
                                    Log.e(TAG, "Lỗi định dạng ngày tháng", e);
                                }
                            }

                            // Sắp xếp danh sách notificationList theo ngày tháng năm
                            Collections.sort(notificationList, new Comparator<Notification>() {
                                @Override
                                public int compare(Notification n1, Notification n2) {
                                    try {
                                        Date date1 = dateFormat.parse(n1.getTime());
                                        Date date2 = dateFormat.parse(n2.getTime());
                                        return date2.compareTo(date1);
                                    } catch (ParseException e) {
                                        Log.e(TAG, "Lỗi định dạng ngày tháng", e);
                                        return 0;
                                    }
                                }
                            });

                            // Cập nhật adapter và recyclerView
                            adapter = new NotificationAdapter(notificationList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error Notification", task.getException());
                        }
                    }
                });

        // Khởi tạo danh sách thông báo


        // Khởi tạo TextView Cancel và thiết lập OnClickListener
        cancelTextView = findViewById(R.id.textViewCancel);
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
                intent.putExtra("showFragment", "profile");
                startActivity(intent);
                overridePendingTransition(0, R.anim.exit_to_right);
                finish(); // Kết thúc NotificationActivity và trở về trang trước đó
            }
        });


    }


}


