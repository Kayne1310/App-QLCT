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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projectappqlct.Helper.FragmentHelper;
import com.example.projectappqlct.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfile extends AppCompatActivity {
    private EditText etName, etAge, etAddress, etSex;
    private TextView etUsername;
    private Button btnSave;
    private FirebaseFirestore db;
    private FirebaseUser userid = FirebaseAuth.getInstance().getCurrentUser();
    private String useridString = userid.getUid();

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

        etUsername = findViewById(R.id.username);

        etName = findViewById(R.id.name);
        etAge = findViewById(R.id.age);
        etAddress = findViewById(R.id.address);
        etSex = findViewById(R.id.sex);
        btnSave = findViewById(R.id.saveprofile);


        //Get UserId


        if (userid != null) {


            db.collection("users").document(useridString).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {


                            long age = document.getLong("age");
                            String ageString = String.valueOf(age);

                            etName.setHint(document.getString("name"));
                            etSex.setHint(document.getString("sex"));
                            etUsername.setHint(document.getString("username"));
                            etAge.setHint(ageString);
                            etAddress.setHint(document.getString("address"));

                        } else {
                            Toast.makeText(EditProfile.this, "No Data", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(EditProfile.this, "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
        // Kiểm tra nếu Intent có dữ liệu để hiển thị fragment nào


        LinearLayout back = findViewById(R.id.back);
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
                // Kiểm tra nếu các EditText có giá trị, nếu không thì lấy từ Hint
                String username = etUsername.getText().toString().trim().isEmpty()
                        ? etUsername.getHint().toString().trim()
                        : etUsername.getText().toString().trim();


                String name = etName.getText().toString().trim().isEmpty()
                        ? etName.getHint().toString().trim()
                        : etName.getText().toString().trim();

                String age = etAge.getText().toString().trim().isEmpty()
                        ? etAge.getHint().toString().trim()
                        : etAge.getText().toString().trim();
                int ageParsed = Integer.parseInt(age);

                String address = etAddress.getText().toString().trim().isEmpty()
                        ? etAddress.getHint().toString().trim()
                        : etAddress.getText().toString().trim();

                String sex = etSex.getText().toString().trim().isEmpty()
                        ? etSex.getHint().toString().trim()
                        : etSex.getText().toString().trim();

                // Tạo một HashMap để lưu trữ dữ liệu
                User user = new User(username, name, ageParsed, address, sex);

                // Lưu dữ liệu vào Firestore
                db.collection("users").document(useridString)// Tên collection là "users"12
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Toast.makeText(EditProfile.this,   "Update Sucessful !", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e->{
                            Toast.makeText(EditProfile.this, "Failed Update"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                        // Thêm tài liệu mới

            }
        });

    }
}