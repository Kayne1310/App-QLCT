package com.example.projectappqlct.Fragment;


import static android.content.ContentValues.TAG;
import android.app.ActivityOptions;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.projectappqlct.ChangePassword;
import com.example.projectappqlct.EditProfile;
import com.example.projectappqlct.FAQ;
import com.example.projectappqlct.Authentication.LoginActivity;
import com.example.projectappqlct.MainActivity;
import com.example.projectappqlct.NotificationActivity;
import com.example.projectappqlct.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView email;
    private FirebaseFirestore db;
    private TextView username;
    private LinearLayout lnMyChart;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String userString=user.getUid();
        if(user!=null){
            String getEmailUser=user.getEmail();
             email=view.findViewById(R.id.textEmail);
             email.setText(getEmailUser);

        }

        // Tham chiếu tới LinearLayout
        lnMyChart = view.findViewById(R.id.LnMychart);
        // Thiết lập sự kiện click
        lnMyChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang FragmentHome khi click
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        db = FirebaseFirestore.getInstance();
        username = view.findViewById(R.id.username);
        db.collection("users").document(userString)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String usernameString = document.getString("name");
                                username.setText(usernameString);
                            }
                        } else {
                            Log.w(TAG, "Error get username", task.getException());
                        }
                    }
                });


        LinearLayout faq = view.findViewById(R.id.faq_question);
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FAQ.class);
                // Tạo một ActivityOptions để thực hiện animation khi mở Activity
                ActivityOptions options = ActivityOptions.makeCustomAnimation(
                        getActivity(), R.anim.enter_from_right,  R.anim.stay);
                // Bắt đầu Activity với animation custom
                startActivity(intent, options.toBundle());
            }
        });


        LinearLayout logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đăng xuất khỏi Firebase
                FirebaseAuth.getInstance().signOut();

                // Cập nhật trạng thái đăng nhập trong SharedPreferences
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();

                // Chuyển người dùng về màn hình Login
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(
                        getActivity(), R.anim.enter_from_right,  R.anim.stay);
                // Bắt đầu Activity với animation custom
                startActivity(intent, options.toBundle());

                getActivity().finish();
            }
        });



        lnMyChart = view.findViewById(R.id.LnMychart);
        lnMyChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang FragmentHome khi click
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

            }
        });

        LinearLayout Notificant=view.findViewById(R.id.notificant);
        Notificant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở NotificationActivity từ Fragment
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                // Tạo một ActivityOptions để thực hiện animation khi mở Activity
                ActivityOptions options = ActivityOptions.makeCustomAnimation(
                        getActivity(), R.anim.enter_from_right,  R.anim.stay);
                // Bắt đầu Activity với animation custom
                startActivity(intent, options.toBundle());
            }
        });


        LinearLayout editProfile = view.findViewById(R.id.editprofile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfile.class);
                // Tạo một ActivityOptions để thực hiện animation khi mở Activity
                ActivityOptions options = ActivityOptions.makeCustomAnimation(
                        getActivity(), R.anim.enter_from_right,  R.anim.stay);
                // Bắt đầu Activity với animation custom
                startActivity(intent, options.toBundle());
            }
        });


        LinearLayout changepwd = view.findViewById(R.id.changepassword);

        changepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePassword.class);
                // Tạo một ActivityOptions để thực hiện animation khi mở Activity
                ActivityOptions options = ActivityOptions.makeCustomAnimation(
                        getActivity(), R.anim.enter_from_right,  R.anim.stay);
                // Bắt đầu Activity với animation custom
                startActivity(intent, options.toBundle());
            }
        });
        return view;


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("Test reload","Profile Fragment");
    }

}