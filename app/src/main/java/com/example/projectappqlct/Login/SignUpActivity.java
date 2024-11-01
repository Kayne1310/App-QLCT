package com.example.projectappqlct.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectappqlct.Model.User;


import com.example.projectappqlct.Model.User;

import com.example.projectappqlct.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword, nameEditText, ageEditText, addressEditText, sexEditText; // Xóa phoneEditText
    private Button signupButton;
    private TextView loginRedirectText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Khởi tạo Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Ánh xạ các EditText
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        nameEditText = findViewById(R.id.name);
        ageEditText = findViewById(R.id.age);

        addressEditText = findViewById(R.id.address);
        sexEditText = findViewById(R.id.sex);

        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String user = signupEmail.getText().toString().trim();
                final String pass = signupPassword.getText().toString().trim();
                final String name = nameEditText.getText().toString().trim();
                final String ageString = ageEditText.getText().toString().trim();
                int age=Integer.parseInt(ageString);
                final String address = addressEditText.getText().toString().trim();
                final String sex = sexEditText.getText().toString().trim();

                // Kiểm tra dữ liệu hợp lệ
                if (user.isEmpty()) {
                    signupEmail.setError("Email cannot be empty");
                    return;
                }
                if (pass.isEmpty()) {
                    signupPassword.setError("Password cannot be empty");
                    return;
                }
                if (name.isEmpty()) {
                    nameEditText.setError("Name cannot be empty");
                    return;
                }
                if (ageString.isEmpty()) {
                    ageEditText.setError("Age cannot be empty");
                    return;
                }
                if (address.isEmpty()) {
                    addressEditText.setError("Address cannot be empty");
                    return;
                }
                if (sex.isEmpty()) {
                    sexEditText.setError("Sex cannot be empty");
                    return;
                }

                // Chuyển đổi chuỗi tuổi (age) thành số nguyên


                // Tạo tài khoản mới
                auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = auth.getCurrentUser().getUid();
                            User newUser = new User(user, pass, name, age, address, sex); // Cập nhật thông tin người dùng

                            // Thêm người dùng vào Firestore trong collection 'users'
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users").document(userId)
                                    .set(newUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this, "User added to Firestore successfully", Toast.LENGTH_SHORT).show();
                                                // Chuyển sang trang login hoặc màn hình chính
                                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "Failed to add user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(SignUpActivity.this, "SignUp Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }

}
