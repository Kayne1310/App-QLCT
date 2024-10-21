package com.example.projectappqlct;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projectappqlct.Helper.FragmentHelper;
import com.example.projectappqlct.Model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfile extends AppCompatActivity {
    private EditText  etPassword, etName, etAge, etAddress, etSex;
    private TextView etUsername;
    private Button btnSave;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });

        db = FirebaseFirestore.getInstance();

        etUsername=findViewById(R.id.username);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.name);
        etAge = findViewById(R.id.age);
        etAddress = findViewById(R.id.address);
        etSex = findViewById(R.id.sex);
        btnSave = findViewById(R.id.saveprofile);

        // Kiểm tra nếu Intent có dữ liệu để hiển thị fragment nào


        LinearLayout back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(EditProfile.this, MainActivity.class);
                intent.putExtra("showFragment", "profile"); // Truyền thông tin về fragment
                startActivity(intent);
                finish();

            }
        });



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getHint().toString().trim();
                String password = etPassword.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String age = etAge.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String sex = etSex.getText().toString().trim();

                // Tạo một HashMap để lưu trữ dữ liệu
                User user = new User(username, password, name, age, address, sex);

                // Lưu dữ liệu vào Firestore
                db.collection("users") // Tên collection là "users"
                        .add(user) // Thêm tài liệu mới
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(EditProfile.this, "Data saved successfully: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.i("check",e.getMessage());
                            Toast.makeText(EditProfile.this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

    }
}