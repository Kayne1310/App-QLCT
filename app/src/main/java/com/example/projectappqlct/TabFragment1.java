package com.example.projectappqlct;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.example.projectappqlct.Model.Budget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabFragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragment1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth auth;

    private Dialog dialog1, dialog2, dialog3, dialog4;
    private Button btnSelectedOption, btnSelectedOption2;
    private ImageView imgiconclick, imageviewGr;
    private Drawable selectedIconDrawable, icon;
    private FirebaseFirestore db;
    private BudgetAdapter budgetAdapter;
    private List<Budget> budgetList;
    private RecyclerView recyclerView;
    private FirebaseUser user;
    private String userString;
    private String selectedIconTag;

    public TabFragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabFragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static TabFragment1 newInstance(String param1, String param2) {
        TabFragment1 fragment = new TabFragment1();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);

        ArcGauge arcGauge = view.findViewById(R.id.arcGauge);

        // Tạo các Range (khoảng màu)
        Range range = new Range();
        range.setColor(Color.parseColor("#ce0000"));
        range.setFrom(0);
        range.setTo(50);

        Range range2 = new Range();
        range2.setColor(Color.parseColor("#E3E500"));
        range2.setFrom(50);
        range2.setTo(100);

        Range range3 = new Range();
        range3.setColor(Color.parseColor("#00b20b"));
        range3.setFrom(100);
        range3.setTo(150);

        // Thêm Range vào ArcGauge
        arcGauge.addRange(range);
        arcGauge.addRange(range2);
        arcGauge.addRange(range3);

        // Thiết lập giá trị min, max, và giá trị hiện tại
        arcGauge.setMinValue(20);
        arcGauge.setMaxValue(150);
        arcGauge.setValue(100);  // Đặt giá trị hiện tại là 0


        Button showDialogButton = view.findViewById(R.id.btnBG);
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog1();
            }
        });

        // Cài đặt RecyclerView cho danh sách Budgets
        recyclerView = view.findViewById(R.id.recyclerViewBudgets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        budgetAdapter = new BudgetAdapter(getActivity(), getListBudget());

        return view;
    }

    private List<Budget> getListBudget() {
        this.budgetList = new ArrayList<>();
        this.db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            String userString = user.getUid();
            db.collection("users").document(userString)
                    .collection("Budgets")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<Budget> budgets = queryDocumentSnapshots.toObjects(Budget.class);
                            budgetList.addAll(budgets);

                            // Thiết lập adapter với danh sách đã lấy
                            budgetAdapter = new BudgetAdapter(getActivity(), budgetList);
                            recyclerView.setAdapter(budgetAdapter);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("FirestoreError", e.getMessage()));
        } else {
            Log.e("AuthError", "User is not authenticated.");
        }
        return java.util.Collections.emptyList();
    }

    // Phương thức tạo dialog với layout và kích thước tùy chỉnh
    private Dialog createDialog(int layoutResId) {
        Dialog dialog = new Dialog(getActivity());
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
    public void showDialog1() {
        db = FirebaseFirestore.getInstance();
        if (dialog1 == null) {  // Khởi tạo dialog1 nếu chưa khởi tạo
            dialog1 = createDialog(R.layout.dialog_createbudget);
        }

        Button buttonSelect = dialog1.findViewById(R.id.btnSelectedOption);
        buttonSelect.setOnClickListener(v -> {
            dialog1.dismiss();  // Đóng dialog 1
            showDialog2();      // Mở dialog 2
        });

        TextView backToFragment = dialog1.findViewById(R.id.textViewCancel);
        backToFragment.setOnClickListener(v -> {
            dialog1.dismiss();  // Đóng hộp thoại 1
        });

        // Tham chiếu đến Button trong Dialog 1
        btnSelectedOption = dialog1.findViewById(R.id.btnSelectedOption);

        // Tham chiếu đến các EditText
        EditText editTextAmount = dialog1.findViewById(R.id.editTextAmount);
        EditText editTextDate = dialog1.findViewById(R.id.editTextCalendar);
        ImageView imageviewGr = dialog1.findViewById(R.id.imageViewGr);

        // Thêm TextWatcher vào editTextAmount
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Đặt ngày đã chọn vào EditText
                        editTextDate.setText(String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        Button buttonSubmit = dialog1.findViewById(R.id.btnAddBudget); // Nút submit
        buttonSubmit.setOnClickListener(v -> {
            String Amount = editTextAmount.getText().toString().replaceAll("[,]", "").trim();
            if (Amount.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }
            int AmountInt = Integer.parseInt(Amount);

            // Lấy ngày từ EditText hoặc hint nếu chưa nhập
            String Calendar = editTextDate.getText().toString().trim().isEmpty() ? editTextDate.getHint().toString().trim() : editTextDate.getText().toString().trim();
            String Group = btnSelectedOption.getText().toString().trim();
            if (Group.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter a valid group", Toast.LENGTH_SHORT).show();
                return;
            }

            String Icon = null;
            if (btnSelectedOption.getTag() != null && !btnSelectedOption.getTag().toString().isEmpty()) {
                Icon = btnSelectedOption.getTag().toString();
            } else if (imageviewGr.getTag() != null) {
                Icon = imageviewGr.getTag().toString();
            } else {
                // Xử lý trường hợp cả hai tag đều null hoặc rỗng.
                Icon = "baseline_drive_file_rename_outline_24"; // Gán giá trị mặc định nếu cần.
            }
            // Lấy tên icon từ Tag;


            Budget budget = new Budget(AmountInt, Calendar, Group, Icon);


            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            userString = user.getUid();

            db.collection("users").document(userString)
                    .collection("Budgets")
                    .add(budget)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String budgetId = documentReference.getId(); // Lấy ID
                            budget.setId(budgetId); // Cập nhật ID vào đối tượng Budget

                            // Lưu budget với ID vào Firestore
                            documentReference.set(budget) // Cập nhật Firestore với budget đã có ID
                                    .addOnSuccessListener(aVoid -> {
                                        Log.i("check", "Budget successfully updated with ID.");
                                        // Đóng dialog1 và reload lại fragment budget
                                        dialog1.dismiss();
                                        Toast.makeText(getActivity(), "Add budget successful", Toast.LENGTH_SHORT).show();

                                        // Gọi phương thức reload từ MainActivity
                                        ((MainActivity) getActivity()).reloadBudgetFragment();

                                    })
                                    .addOnFailureListener(e -> {
                                        Log.i("check", "Error updating budget ID", e);
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("check", "Error adding document", e);
                        }
                    });


        });
        dialog1.show();
    }

    // Hiển thị hộp thoại 2
    public void showDialog2() {
        if (dialog2 == null) {  // Khởi tạo dialog2 nếu chưa khởi tạo
            dialog2 = createDialog(R.layout.dialog_selectgroup);
        }

        Button buttonGrn = dialog2.findViewById(R.id.btnGrn);
        buttonGrn.setOnClickListener(v -> {
            dialog2.dismiss();  // Đóng dialog 2
            showDialog3();      // Mở dialog 3
        });

        TextView backToDialog1 = dialog2.findViewById(R.id.textViewBack1);
        backToDialog1.setOnClickListener(v -> {
            dialog2.dismiss();  // Đóng hộp thoại 2
            showDialog1();      // Quay lại hộp thoại 1
        });

        // Listener chung cho tất cả các Button trong Dialog 2
        View.OnClickListener optionClickListener = v -> {
            handleOptionSelected((Button) v);   // Cập nhật text và mở lại Dialog 1
        };
        dialog2.findViewById(R.id.btnEat).setOnClickListener(optionClickListener);
        dialog2.findViewById(R.id.btnShopping).setOnClickListener(optionClickListener);
        dialog2.findViewById(R.id.btnFamily).setOnClickListener(optionClickListener);
        dialog2.findViewById(R.id.btnHealthy).setOnClickListener(optionClickListener);
        dialog2.findViewById(R.id.btnVehicle).setOnClickListener(optionClickListener);
        dialog2.findViewById(R.id.btnSport).setOnClickListener(optionClickListener);
        dialog2.findViewById(R.id.btnEdu).setOnClickListener(optionClickListener);
        dialog2.findViewById(R.id.btnEntertainment).setOnClickListener(optionClickListener);
        dialog2.findViewById(R.id.btnGifts).setOnClickListener(optionClickListener);
        dialog2.findViewById(R.id.btnInvest).setOnClickListener(optionClickListener);
        dialog2.findViewById(R.id.btnMakeup).setOnClickListener(optionClickListener);
        dialog2.findViewById(R.id.btnDonate).setOnClickListener(optionClickListener);
        dialog2.findViewById(R.id.btnOther).setOnClickListener(optionClickListener);
        dialog2.show();

    }


    // Hiển thị hộp thoại 3
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
        EditText editTextGroup = dialog3.findViewById(R.id.editTextNameGr);

        // Nút Lưu

        Button buttonSave = dialog3.findViewById(R.id.btnSave);
        buttonSave.setOnClickListener(v -> {
            String selectedText = editTextGroup.getText().toString().trim();

            // Cập nhật icon cho ImageView imageViewGr trong dialog 1
            ImageView imageViewGr = dialog1.findViewById(R.id.imageViewGr);
            if (imageViewGr != null && selectedIconDrawable != null) {
                imageViewGr.setImageDrawable(selectedIconDrawable); // Update icon
                // Set the tag to the imageViewGr using the selectedIconTag
                imageViewGr.setTag(selectedIconTag); // Assign the tag retrieved from dialog 4
            } else {
                // Nếu không có icon được chọn, đặt icon mặc định
                imageViewGr.setImageResource(R.drawable.baseline_drive_file_rename_outline_24);
                // Set tag mặc định nếu cần thiết
                imageViewGr.setTag("baseline_drive_file_rename_outline_24");
            }

            // Cập nhật text cho buttonSelectOption trong dialog 1
            btnSelectedOption.setText(selectedText); // Cập nhật text

            // Đóng dialog 3 và mở lại dialog 1
            dialog3.dismiss();
            showDialog1();
        });

        TextView backToDialog1 = dialog3.findViewById(R.id.textViewBack2);
        backToDialog1.setOnClickListener(v -> {
            dialog3.dismiss();  // Đóng hộp thoại 3
            showDialog2();      // Quay lại hộp thoại 2
        });

        dialog3.show();
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
        dialog4.findViewById(R.id.imageView23).setOnClickListener(optionClickListener);
        dialog4.findViewById(R.id.imageView24).setOnClickListener(optionClickListener);
        dialog4.show();
    }

    private void handleOptionSelected(Button selectedButton) {
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
        ImageView imgSelectedOption = dialog1.findViewById(R.id.imageViewGr);

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


    public static String removeSpacesAndToLower(String input) {
        if (input == null) {
            return null; // Xử lý trường hợp chuỗi đầu vào là null
        }
        return input.replaceAll("\\s+", "").toLowerCase(); // Thay thế dấu cách và chuyển thành chữ thường
    }

}