package com.example.projectappqlct;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {
    private EditText etOldPassowrd,etNewPassword;
    private Button savePassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        LinearLayout back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(ChangePassword.this, MainActivity.class);
                intent.putExtra("ChangePassword", "ChangePassword"); // Truyền thông tin về fragment
                startActivity(intent);
                finish();

            }
        });


        //logic change password

        etOldPassowrd=findViewById(R.id.etOldPassword);
        etNewPassword=findViewById(R.id.etNewPassword);
        savePassword=findViewById(R.id.savepassword);

        savePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldPassword=etOldPassowrd.getText().toString().trim();
                String newPassword=etNewPassword.getText().toString().trim();

                if(oldPassword.isEmpty()||newPassword.isEmpty()){
                    Toast.makeText(ChangePassword.this  , "Please Enter Old Password and New Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth=FirebaseAuth.getInstance();

                FirebaseUser user=auth.getCurrentUser();

                auth.signInWithEmailAndPassword(user.getEmail(),oldPassword)
                        .addOnCompleteListener(task -> {
                           if(task.isSuccessful()){
                               user.updatePassword(newPassword).addOnCompleteListener(updateTask->{
                                   if(updateTask.isSuccessful()){
                                       Toast.makeText(ChangePassword.this, "Change Password Successful!", Toast.LENGTH_SHORT).show();
                                   }
                                   else{
                                       Toast.makeText(ChangePassword.this, "Change Password Failed", Toast.LENGTH_SHORT).show();
                                   }
                               });
                           }
                           else{
                               Toast.makeText(ChangePassword.this, "Old Password not correct", Toast.LENGTH_SHORT).show();
                           }
                        });
            }
        });



    }


}