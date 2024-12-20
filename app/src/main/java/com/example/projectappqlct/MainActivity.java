package com.example.projectappqlct;


import static com.example.projectappqlct.Helper.NotificationHelper.sendNotificationToUser;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.text.TextWatcher;

import com.example.projectappqlct.Adapter.OptionAdapter;
import com.example.projectappqlct.Authentication.LoginActivity;
import com.example.projectappqlct.Model.Expense;
import com.example.projectappqlct.Model.Option;
import com.example.projectappqlct.Model.Notification;
import com.example.projectappqlct.ViewPagerAdapter.BudgetViewPagerAdapter;
import com.example.projectappqlct.ViewPagerAdapter.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private EditText editTextAmount, editTextDate;
    private Dialog dialog1, dialog2, dialog3, dialog4, dialog5;
    private Button btnSelectedOption, buttonSelect, buttonSelectNote;
    private Drawable selectedIconDrawable, icon;
    private ImageView imgiconclick, imageViewGr, imgSelectedOption;
    private String selectedIconTag;
    private BudgetViewPagerAdapter adapter;
    private OptionAdapter optionAdapter;
    private List<Option> optionList = new ArrayList<>();
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Kiểm tra trạng thái đăng nhập
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        Log.d("MainActivity", "User logged in: " + isLoggedIn);
        if (!isLoggedIn) {
            // Nếu chưa đăng nhập, chuyển về LoginActivity
            Log.d("MainActivity", "Redirecting to LoginActivity");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Đóng MainActivity để không quay lại
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);


        // Kiểm tra Intent để xác định Fragment cần hiển thị
        // route tu activity editprofile ve main acitivty ra route sang fragment profile
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("showFragment")) {
            String fragmentToShow = intent.getStringExtra("showFragment");
            if ("profile".equals(fragmentToShow)) {
                viewPager.setCurrentItem(3); // 4 là chỉ số của ProfileFragment
                bottomNavigationView.getMenu().findItem(R.id.menu_profile).setChecked(true);
            }
        }


        //route tu activity changepassword ve main acitivy roi route sang fragment profile
        if (intent != null && intent.hasExtra("ChangePassword")) {
            String fragmentToShow = intent.getStringExtra("ChangePassword");
            if ("ChangePassword".equals(fragmentToShow)) {
                viewPager.setCurrentItem(3); // 4 là chỉ số của ProfileFragment
                bottomNavigationView.getMenu().findItem(R.id.menu_profile).setChecked(true);
            }
        }
        //route tu activity DetailActivity ve main acitivy roi route sang fragment budget
        if (intent != null && intent.hasExtra("DetailActivity")) {
            String fragmentToShow = intent.getStringExtra("DetailActivity");
            if ("DetailActivity".equals(fragmentToShow)) {
                viewPager.setCurrentItem(2); // 3 là chỉ số của BudgetFragment
                bottomNavigationView.getMenu().findItem(R.id.menu_budget).setChecked(true);
            }
        }

        //route tu activity DetailExpense ve main acitivy roi route sang fragment history
        if (intent != null && intent.hasExtra("DetailExpense")) {
            String fragmentToShow = intent.getStringExtra("DetailExpense");
            if ("DetailExpense".equals(fragmentToShow)) {
                viewPager.setCurrentItem(1); // 3 là chỉ số của BudgetFragment
                bottomNavigationView.getMenu().findItem(R.id.menu_history).setChecked(true);
            }
        }


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.menu_history).setChecked(true);
                        break;

                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.menu_budget).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.menu_profile).setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_home) {
                    viewPager.setCurrentItem(0);
                    return true;
                } else if (id == R.id.menu_history) {
                    viewPager.setCurrentItem(1);
                    return true;
                } else if (id == R.id.menu_create) {
                    showDialog1();
                    return true;
                } else if (id == R.id.menu_budget) {
                    viewPager.setCurrentItem(2);
                    return true;
                } else if (id == R.id.menu_profile) {
                    viewPager.setCurrentItem(3);
                }
                return false;
            }

            // Phương thức tạo dialog với layout và kích thước tùy chỉnh
            private Dialog createDialog(int layoutResId) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(layoutResId);
                Window window = dialog.getWindow();
                if (window != null) {
                    WindowManager.LayoutParams params = window.getAttributes();
                    window.setWindowAnimations(R.style.DialogAnimation);  // Thiết lập animation
                    params.width = WindowManager.LayoutParams.MATCH_PARENT;
                    params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);  // Chiều cao 90% màn hình
                    params.gravity = Gravity.TOP;  // Đặt vị trí ở trên cùng
                    window.setAttributes(params);
                }
                return dialog;
            }

            // Hiển thị hộp thoại 1
            private void showDialog1() {
                if (dialog1 == null) {  // Khởi tạo dialog1 nếu chưa khởi tạo
                    dialog1 = createDialog(R.layout.dialog_createexpense);
                }

                buttonSelect = dialog1.findViewById(R.id.btnSelectedOption);
                buttonSelect.setOnClickListener(v -> {
                    dialog1.dismiss();  // Đóng dialog 1
                    showDialog2();      // Mở dialog 2
                });

                buttonSelectNote = dialog1.findViewById(R.id.btnSelectedNote);
                buttonSelectNote.setOnClickListener(v -> {
                    dialog1.dismiss();  // Đóng dialog 1
                    showDialog5();      // Mở dialog 2
                });

                TextView backToFragment = dialog1.findViewById(R.id.textViewCancel);
                backToFragment.setOnClickListener(v -> {
                    dialog1.dismiss();  // Đóng hộp thoại 1
                });
                // Tham chiếu đến Button ghi chú

                // Tham chiếu đến Button trong Dialog 1
                btnSelectedOption = dialog1.findViewById(R.id.btnSelectedOption);

                // Tham chiếu đến các EditText

                editTextAmount = dialog1.findViewById(R.id.editTextAmount);
                editTextDate = dialog1.findViewById(R.id.editTextCalendar);
                imageViewGr = dialog1.findViewById(R.id.imageViewGr);

                editTextAmount.addTextChangedListener(new TextWatcher() {
                    private String currentText = "";

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!s.toString().equals(currentText)) {
                            editTextAmount.removeTextChangedListener(this);

                            // Xóa dấu phẩy và khoảng trắng khỏi chuỗi
                            String cleanString = s.toString().replaceAll("[,]", "");
                            if (cleanString.isEmpty()) {
                                currentText = "";
                                editTextAmount.setText("");
                                editTextAmount.setSelection(0);  // Đảm bảo con trỏ về đầu
                                editTextAmount.addTextChangedListener(this); // Thêm lại TextWatcher trước khi thoát
                                return;
                            }

                            try {
                                // Chuyển chuỗi thành số nguyên
                                double parsed = Double.parseDouble(cleanString);
                                // Định dạng số với dấu phẩy ngăn cách hàng nghìn
                                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                                symbols.setGroupingSeparator(',');
                                DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);

                                String formatted = decimalFormat.format(parsed);
                                currentText = formatted;

                                editTextAmount.setText(formatted);
                                editTextAmount.setSelection(formatted.length());

                            } catch (NumberFormatException e) {
                                Log.e("TextWatcher", "Invalid number format", e);
                            }

                            editTextAmount.addTextChangedListener(this);
                        }
                    }
                });


                // Lấy ngày hiện tại
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                // Đặt ngày hiện tại làm hint
                editTextDate.setHint(String.format("%02d/%02d/%04d", day, month + 1, year));
                editTextDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Tạo DatePickerDialog
                        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                // Đặt ngày đã chọn vào EditText
                                editTextDate.setText(String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear));
                            }
                        }, year, month, day);
                        datePickerDialog.show();
                    }
                });
                Button buttonSubmit = dialog1.findViewById(R.id.btnAdd); // Nút submit
                buttonSubmit.setOnClickListener(v -> {
                    String Amount = editTextAmount.getText().toString().replaceAll("[,]", "").trim();
                    if (Amount.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int AmountInt = Integer.parseInt(Amount);
                    String Calendar = editTextDate.getText().toString().trim().isEmpty() ? editTextDate.getHint().toString().trim() : editTextDate.getText().toString().trim();
                    String Group = buttonSelect.getText().toString().trim();
                    if (Group.isEmpty() || Group.equals("Select Group")) {
                        Toast.makeText(MainActivity.this, "Please enter a valid group", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    String Note = buttonSelectNote.getText().toString().trim();
                    String Icon = null;
                    if (btnSelectedOption.getTag() != null && !btnSelectedOption.getTag().toString().isEmpty()) {
                        Icon = btnSelectedOption.getTag().toString(); // lấy tag icon từ button
                    } else if (imageViewGr.getTag() != null) {
                        Icon = imageViewGr.getTag().toString(); // lấy tag icon từ imageView
                    } else {
                        // Xử lý trường hợp cả hai tag đều null hoặc rỗng.
                        Icon = "defaultIcon"; // Gán giá trị mặc định nếu cần.
                    }
                    // Lấy tên icon từ Tag;
                    Expense expense = new Expense(AmountInt, Calendar, Group, Icon, Note); // Đổi tên từ budget thành expense

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    String userString = user.getUid();


                    // Thêm Expense vào Firestore
                    db.collection("users").document(userString)
                            .collection("Expenses")
                            .add(expense)
                            .addOnSuccessListener(documentReference -> {
                                String documentId = documentReference.getId();
                                documentReference.update("id", documentId)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.i("check", "Document ID updated successfully");

                                            // Đóng dialog sau khi thêm thành công
                                            dialog1.dismiss();
                                            resetDialogFields();

                                            // Kiểm tra vị trí hiện tại của ViewPager và gửi Intent tương ứng
                                            int currentPage = viewPager.getCurrentItem();
                                            Intent updateIntent = null;

                                            if (currentPage == 0) {
                                                // Nếu ViewPager đang ở vị trí 0, gửi Intent cho trang đầu
                                                updateIntent = new Intent("DATA_UPDATED");
                                            } else if (currentPage == 1) {
                                                // Nếu ViewPager đang ở vị trí 1, gửi Intent cho trang 2
                                                updateIntent = new Intent("EXPENSE_ADDED");

                                            } else if
                                            (currentPage == 2) {
                                                updateIntent = new Intent("Load_Buget");


                                            }
                                            if (currentPage != 3) {
                                                // Gửi Intent dựa trên trang hiện tại
                                                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(updateIntent);
                                            }

                                            Toast.makeText(MainActivity.this, "Add expense successful", Toast.LENGTH_SHORT).show();
                                            checkBudgetLimit(getApplicationContext(), userString, expense.getGroup(), expense.getAmount(), expense.getCalendar());
                                        })
                                        .addOnFailureListener(e -> Log.i("check", "Error updating document ID", e));

                            })
                            .addOnFailureListener(e -> Log.i("check", "Error adding document", e));


                });
                dialog1.show();

            }


            // Hiển thị hộp thoại 2
            private void showDialog2() {
                if (dialog2 == null) {  // Khởi tạo dialog2 nếu chưa khởi tạo
                    dialog2 = createDialog(R.layout.dialog_selectgroup);
                }

                TextView textViewGrn = dialog2.findViewById(R.id.tv_Grn);
                textViewGrn.setOnClickListener(v -> {

                    dialog2.dismiss();  // Đóng dialog 2
                    showDialog3();      // Mở dialog 3
                });

                TextView backToDialog1 = dialog2.findViewById(R.id.textViewBack1);
                backToDialog1.setOnClickListener(v -> {
                    dialog2.dismiss();  // Đóng hộp thoại 2
                    showDialog1();
                    // Quay lại hộp thoại 1
                });

                // Tìm RecyclerView
                RecyclerView recyclerView = dialog2.findViewById(R.id.recyclerViewOption);
                if (recyclerView == null) {
                    Log.e("ShowDialog2", "RecyclerView is null! Check the layout file.");
                    return; // Hoặc xử lý lỗi nếu cần
                }

                // Khởi tạo LayoutManager cho RecyclerView nếu chưa có
                if (recyclerView.getLayoutManager() == null) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                }

                // Khởi tạo Adapter nếu chưa có
                if (optionAdapter == null) {
                    optionAdapter = new OptionAdapter(optionList, v -> {
                        int position = recyclerView.getChildAdapterPosition(v);
                        Option selectedOption = optionList.get(position);
                        handleRecyclerViewSelection(selectedOption);
                    });
                    recyclerView.setAdapter(optionAdapter);
                }


                // Tải dữ liệu từ Firestore và thiết lập Adapter sau khi dữ liệu đã tải xong
                loadOptionsFromFirestore();

                // Cài đặt sự kiện click cho item trong RecyclerView
                View.OnClickListener itemClickListener = v -> {
                    Option selectedOption = (Option) v.getTag(); // Lấy Option từ tag
                    if (selectedOption != null) {
                        // Cập nhật button select trong dialog1
                        Button btnSelectedOption = dialog1.findViewById(R.id.btnSelectedOption);
                        btnSelectedOption.setText(selectedOption.getName()); // Cập nhật tên

                        // Cập nhật icon và gán tag cho ImageView
                        ImageView imageViewGr = dialog1.findViewById(R.id.imageViewGr);
                        int iconResId = MainActivity.this.getResources().getIdentifier(selectedOption.getIcon(), "drawable", MainActivity.this.getPackageName());
                        imageViewGr.setTag(selectedOption.getIcon()); // Gán tag cho ImageView
                        imageViewGr.setImageResource(iconResId);

                        // Đóng dialog2
                        if (dialog2 != null && dialog2.isShowing()) {
                            dialog2.dismiss();
                        }

                        // Mở dialog1
                        showDialog1();  // Hoặc gọi phương thức để mở dialog1
                    }
                };

                // Tạo adapter và gán listener
                optionAdapter = new OptionAdapter(optionList, itemClickListener);
                recyclerView.setAdapter(optionAdapter);


                // Listener chung cho tất cả các Button trong Dialog 2
                View.OnClickListener optionClickListener = v -> {
                    handleOptionSelected((TextView) v);   // Cập nhật text và mở lại Dialog 1
                };
                dialog2.findViewById(R.id.tv_Eat).setOnClickListener(optionClickListener);
                dialog2.findViewById(R.id.tv_Shopping).setOnClickListener(optionClickListener);
                dialog2.findViewById(R.id.tv_Family).setOnClickListener(optionClickListener);
                dialog2.findViewById(R.id.tv_Healthy).setOnClickListener(optionClickListener);
                dialog2.findViewById(R.id.tv_Vehicle).setOnClickListener(optionClickListener);
                dialog2.findViewById(R.id.tv_Sport).setOnClickListener(optionClickListener);
                dialog2.findViewById(R.id.tv_Edu).setOnClickListener(optionClickListener);
                dialog2.findViewById(R.id.tv_Entertainment).setOnClickListener(optionClickListener);
                dialog2.findViewById(R.id.tv_Gifts).setOnClickListener(optionClickListener);
                dialog2.findViewById(R.id.tv_Invest).setOnClickListener(optionClickListener);
                dialog2.findViewById(R.id.tv_Makeup).setOnClickListener(optionClickListener);
                dialog2.findViewById(R.id.tv_Donate).setOnClickListener(optionClickListener);
                dialog2.findViewById(R.id.tv_Other).setOnClickListener(optionClickListener);
                dialog2.show();
            }


            // Hiển thị hộp thoại 2
            private void showDialog3() {
                if (dialog3 == null) {  // Khởi tạo dialog3 nếu chưa khởi tạo
                    dialog3 = createDialog(R.layout.dialog_newgroup);
                }

                // Chọn icon
                imgiconclick = dialog3.findViewById(R.id.imgicon);
                imgiconclick.setOnClickListener(v -> {
                    showDialog4();  // Mở dialog 4
                });

                // Chọn text
                EditText editTextOption = dialog3.findViewById(R.id.editTextNameGr);

                // Nút Lưu
                Button buttonSave = dialog3.findViewById(R.id.btnSave);
                buttonSave.setOnClickListener(v -> {
                    String selectedText = editTextOption.getText().toString().trim();

                    // Kiểm tra tên option không trống
                    if (selectedText.isEmpty()) {
                        Toast.makeText(dialog3.getContext(), "Tên option không được để trống!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Xác định icon (hoặc đặt mặc định nếu không chọn icon)
                    String selectedIconTag;
                    ImageView imageViewIcon = dialog3.findViewById(R.id.imgicon);

                    if (imageViewIcon != null && imageViewIcon.getTag() != null) {
                        selectedIconTag = imageViewIcon.getTag().toString(); // Lấy tag icon
                    } else {
                        selectedIconTag = "baseline_drive_file_rename_outline_24"; // Icon mặc định
                    }

                    // Tạo option mới với icon (dưới dạng chuỗi) và tên
                    Option newOption = new Option(selectedIconTag, selectedText);

                    // Lưu option mới vào Firestore
                    saveOptionToFirestore(newOption);

                    // Đóng dialog3
                    dialog3.dismiss();
                    showDialog2();
                });

                TextView backToDialog1 = dialog3.findViewById(R.id.textViewBack2);
                backToDialog1.setOnClickListener(v -> {
                    dialog3.dismiss();  // Đóng hộp thoại 3
                    showDialog2();      // Quay lại hộp thoại 2
                });

                dialog3.show();
            }

            private void saveOptionToFirestore(Option option) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                auth = FirebaseAuth.getInstance();
                user = auth.getCurrentUser();

                if (user != null) {
                    String userString = user.getUid();

                    // Lưu option mới vào Firestore trong collection "options"
                    db.collection("users")
                            .document(userString)
                            .collection("Options")
                            .add(option)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(MainActivity.this, "Option đã được lưu!", Toast.LENGTH_SHORT).show();

                                // Sau khi lưu, tải lại danh sách options để cập nhật RecyclerView trong dialog2
                                loadOptionsFromFirestore();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(MainActivity.this, "Không thể lưu option.", Toast.LENGTH_SHORT).show()
                            );
                }
            }


            // Hiển thị hộp thoại 4
            private void showDialog4() {
                if (dialog4 == null) {  // Khởi tạo dialog4 nếu chưa khởi tạo
                    dialog4 = createDialog(R.layout.dialog_selecticon);
                }

                TextView backToDialog1 = dialog4.findViewById(R.id.textViewBack3);
                backToDialog1.setOnClickListener(v -> {
                    dialog4.dismiss();  // Đóng hộp thoại 4
                });

                // Listener cho các icon trong dialog4
                View.OnClickListener optionClickListener = v -> {
                    handleOptionSelectedIcon((ImageView) v);   // Cập nhật icon đã chọn
                };

                dialog4.findViewById(R.id.imageView1).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView2).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView3).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView4).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView5).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView6).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView7).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView8).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView9).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView10).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView11).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView12).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView13).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView14).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView15).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView16).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView17).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView18).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView19).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView20).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView21).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView22).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView23).setOnClickListener(optionClickListener);
                dialog4.findViewById(R.id.imageView24).setOnClickListener(optionClickListener);

                dialog4.show();
            }

            private void showDialog5() {
                if (dialog5 == null) {  // Khởi tạo dialog5 nếu chưa khởi tạo
                    dialog5 = createDialog(R.layout.dialog_description);
                }

                // Tham chiếu đến nút quay lại từ Dialog 5
                TextView backToDialog5 = dialog5.findViewById(R.id.textViewCancelNote);
                EditText editTextNote = dialog5.findViewById(R.id.editTextNote); // Ghi chú trong Dialog 5

                // Xử lý khi nhấn nút quay lại từ Dialog 5
                backToDialog5.setOnClickListener(v -> {
                    // Lưu ghi chú lại trước khi quay về Dialog 1
                    String note = editTextNote.getText().toString().trim();

                    // Cập nhật TextView hoặc EditText trong Dialog 1 với ghi chú
                    Button buttonSelectNote = dialog1.findViewById(R.id.btnSelectedNote);
                    buttonSelectNote.setText(note); // Hiển thị ghi chú đã lưu ở Dialog 1

                    dialog5.dismiss();  // Đóng dialog 5
                    showDialog1();      // Mở lại dialog 1
                });

                // Hiển thị dialog5
                dialog5.show();
            }


            private void handleOptionSelected(TextView selectedButton) {
                if (dialog2 != null && dialog2.isShowing()) {  // Đóng dialog2 nếu đang mở
                    dialog2.dismiss();
                }

                // Lấy text từ Button được nhấn
                String selectedText = selectedButton.getText().toString();
                if (btnSelectedOption != null) {
                    btnSelectedOption.setText(selectedText);// Cập nhật text trong Dialog 1
                    btnSelectedOption.setTag(removeSpacesAndToLower(selectedText));// Cập nhật tag trong Dialog 1
                }

                // Lấy icon từ Button được nhấn (compound drawable bên trái)
                icon = selectedButton.getCompoundDrawables()[0];
                imgSelectedOption = dialog1.findViewById(R.id.imageViewGr);

                if (imgSelectedOption != null && icon != null) {
                    imgSelectedOption.setImageDrawable(icon);  // Gán icon vào ImageView của Dialog 1
                }

                if (dialog1 != null) {  // Mở lại Dialog 1
                    dialog1.show();
                }
            }

            private void handleOptionSelectedIcon(ImageView selectedIcon) {
                // Đóng dialog4 nếu nó đang mở
                if (dialog4 != null && dialog4.isShowing()) {
                    dialog4.dismiss();
                }

                // Lấy icon từ ImageView được chọn
                selectedIconDrawable = selectedIcon.getDrawable();  // Lưu lại Drawable của icon được chọn

                // Cập nhật icon vào ImageView của Dialog 3
                ImageView imgiconclick = dialog3.findViewById(R.id.imgicon);
                if (imgiconclick != null && selectedIconDrawable != null) {
                    selectedIconTag = (String) selectedIcon.getTag();
                    imgiconclick.setTag(selectedIconTag); // Cập nhật icon
                    imgiconclick.setImageDrawable(selectedIconDrawable);

                }

            }

            public String removeSpacesAndToLower(String input) {
                if (input == null) {
                    return null; // Xử lý trường hợp chuỗi đầu vào là null
                }
                return input.replaceAll("\\s+", "").toLowerCase(); // Thay thế dấu cách và chuyển thành chữ thường
            }


            // Hàm để reset các trường trong dialog
            private void resetDialogFields() {
                //  editTextAmount.setText("0"); // Reset số tiền
                editTextAmount.getText().clear();
                buttonSelect.setHint("Select Group"); // Reset nhóm
                buttonSelectNote.setHint("Add Note"); // Reset ghi chú
                btnSelectedOption.setTag(null); // Reset icon button
                imageViewGr.setTag(null); // Reset tag của ImageView
                imageViewGr.setImageResource(R.drawable.baseline_groups_2_24); // Hiển thị lại icon mặc định
                imageViewGr = findViewById(R.id.imageViewGr);  // Nếu bạn cần khởi tạo lại imageViewGr, hãy đặt nó ở đầu phương thức
            }


            //     Tải dữ liệu từ Firestore và cập nhật RecyclerView
            private void loadOptionsFromFirestore() {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                auth = FirebaseAuth.getInstance();
                user = auth.getCurrentUser();
                userString = user.getUid();

                db.collection("users").document(userString)
                        .collection("Options").get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            optionList.clear();
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                Option option = doc.toObject(Option.class);
                                optionList.add(option);
                            }

                            // Thông báo Adapter cập nhật lại dữ liệu
                            if (optionAdapter != null) {
                                optionAdapter.notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(MainActivity.this, "Failed to load options.", Toast.LENGTH_SHORT).show();
                            Log.e("FirestoreError", "Error loading options", e);
                        });
            }


            private void handleRecyclerViewSelection(Option selectedOption) {
                if (dialog2 != null && dialog2.isShowing()) {
                    dialog2.dismiss();  // Đóng Dialog 2
                }

                // Cập nhật tên nhóm vào Dialog 1
                btnSelectedOption.setText(selectedOption.getName());
                btnSelectedOption.setTag(selectedOption.getName());

                // Cập nhật icon vào ImageView trong Dialog 1
                ImageView imgSelectedOption = dialog1.findViewById(R.id.imageViewGr);
                int iconResId = getResources().getIdentifier(
                        selectedOption.getIcon(), "drawable", MainActivity.this.getPackageName()
                );
                imgSelectedOption.setImageResource(iconResId);

                // Mở lại Dialog 1
                if (dialog1 != null) {
                    dialog1.show();
                }
            }

        });

    }


    private void checkBudgetLimit(Context context, String userId, String group,
                                  int newAmount, String calendarDate) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userString = user.getUid();

        // Set up calendar with current month and year from calendarDate
        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = dateFormat.parse(calendarDate);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);
        final int[] totalExpenseThisMonth = {0}; // Change to an array to be effectively final

        // Calculate total expenses for the current month for the specified group
        db.collection("users").document(userId)
                .collection("Expenses")
                .whereEqualTo("group", group)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String dateStr = document.getString("calendar");
                            try {
                                Date docDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateStr);
                                Calendar docCalendar = Calendar.getInstance();
                                docCalendar.setTime(docDate);
                                int docMonth = docCalendar.get(Calendar.MONTH) + 1;
                                int docYear = docCalendar.get(Calendar.YEAR);

                                if (docMonth == currentMonth && docYear == currentYear) {
                                    int amount = document.getLong("amount").intValue();
                                    totalExpenseThisMonth[0] += amount; // Update the array element
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        // Retrieve budget of the group from Firestore
                        db.collection("users").document(userId)
                                .collection("Budgets")
                                .whereEqualTo("group", group)
                                .get()
                                .addOnCompleteListener(budgetTask -> {
                                    if (budgetTask.isSuccessful() && !budgetTask.getResult().isEmpty()) {
                                        int groupBudget = budgetTask.getResult().getDocuments().get(0)
                                                .getLong("amount").intValue();

                                        // Check if total expense exceeds the budget
                                        if (totalExpenseThisMonth[0] > groupBudget) { // Use the array's element
                                            // Save notification to Firestore

                                            int difference = totalExpenseThisMonth[0] - groupBudget;
                                            NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
                                            String formattedDifference = numberFormat.format(difference);

                                            String title = "Over budget!";
                                            String content = "Group " + group + " has gone over budget " + formattedDifference + " VND.";
                                            String time = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

                                            Notification notification = new Notification(title, content, time);
                                            db.collection("users").document(userId)
                                                    .collection("Notifications")
                                                    .add(notification)
                                                    .addOnSuccessListener(documentReference -> {
                                                        Log.i("check", "Notification added with ID: " + documentReference.getId());

                                                        // Send local notification to the user
                                                        sendNotificationToUser(context, title, content);
                                                    });
                                        }
                                        if (totalExpenseThisMonth[0] == groupBudget) { // Use the array's element
                                            // Save notification to Firestore
                                            String title = "Spent all this month's budget!";
                                            String content = "Group " + group + "has spent all the budget this month " + " VND.";
                                            String time = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

                                            Notification notification = new Notification(title, content, time);
                                            db.collection("users").document(userId)
                                                    .collection("Notifications")
                                                    .add(notification)
                                                    .addOnSuccessListener(documentReference -> {
                                                        Log.i("check", "Notification added with ID: " + documentReference.getId());

                                                        // Send local notification to the user
                                                        sendNotificationToUser(context, title, content);
                                                    });
                                        }
                                    }
                                });
                    }
                });
    }


}

