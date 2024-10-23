package com.example.projectappqlct;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
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

import com.example.projectappqlct.Model.Budget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.graphics.Color;  // Để sử dụng Color.parseColor()
import android.widget.Toast;

import com.ekn.gruzer.gaugelibrary.ArcGauge; // Import ArcGauge
import com.ekn.gruzer.gaugelibrary.Range;    // Import Range từ thư viện

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BudgetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BudgetFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAuth auth;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Dialog dialog1, dialog2, dialog3, dialog4;
    private Button btnSelectedOption, btnSelectedOption2;
    private ImageView imgiconclick, imageviewGr;
    private Drawable selectedIconDrawable;
    private FirebaseFirestore db;
    private BudgetADT budgetADT;
    private List<Budget> budgetList;
    private RecyclerView recyclerView;

    public BudgetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificantFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BudgetFragment newInstance(String param1, String param2) {
        BudgetFragment fragment = new BudgetFragment();
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
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

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

//            db = FirebaseFirestore.getInstance();
//            budgetList = new ArrayList<>();
//            adapter = new BudgetAdapter(budgetList);
//
//            recyclerView = view.findViewById(R.id.recyclerViewBudgets);
//            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//            recyclerView.setAdapter(adapter);
//
//            loadBudgets();  // Load data từ Firestore

        recyclerView = view.findViewById(R.id.recyclerViewBudgets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        budgetADT = new BudgetADT(getListBudget());
        recyclerView.setAdapter(budgetADT);

        return view;
    }

    ////???
    private List<Budget> getListBudget() {
        List<Budget> list = new ArrayList<>();
        this.budgetList = list;
        this.db = FirebaseFirestore.getInstance();

        db.collection("budgets")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Budget> budgets = queryDocumentSnapshots.toObjects(Budget.class);
                        list.addAll(budgets);
                        budgetADT.notifyDataSetChanged();  // Cập nhật RecyclerView
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", e.getMessage()));

        return list;
    }

//    private void loadBudgets() {
//        db.collection("Budgets").get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                budgetList.clear();
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    Budget budget = document.toObject(Budget.class);
//                    budget.setId(document.getId());  // Lưu ID cho việc xóa/sửa
//                    budgetList.add(budget);
//                }
//                adapter.notifyDataSetChanged();
//            } else {
//                Log.w("TAG", "Error getting documents.", task.getException());
//            }
//        });
//    }

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
    private void showDialog1() {
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
        EditText editTextField1 = dialog1.findViewById(R.id.editTextAmount);
        EditText editTextField2 = dialog1.findViewById(R.id.editTextCalendar);
        ImageView imageviewGr = dialog1.findViewById(R.id.imageViewGr);

        // Lấy ngày hiện tại
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        // Đặt ngày hiện tại làm hint
        editTextField2.setHint(String.format("%02d/%02d/%04d", day, month + 1, year));
        editTextField2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Đặt ngày đã chọn vào EditText
                        editTextField2.setText(String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        Button buttonSubmit = dialog1.findViewById(R.id.btnAddBudget); // Nút submit
        buttonSubmit.setOnClickListener(v -> {
            String Amount = editTextField1.getText().toString().trim();
            int AmountInt = Integer.parseInt(Amount);
            String Calendar = editTextField2.getText().toString().trim();
            String Group = buttonSelect.getText().toString().trim();
            String Icon = imageviewGr.getResources().toString().trim();
            Budget budget = new Budget(AmountInt, Calendar, Group, Icon);

            auth=FirebaseAuth.getInstance();
            FirebaseUser user=auth.getCurrentUser();
            String userString=user.getUid();

            db.collection("Users").document(userString)
                    .collection("Budget")
                    .add(budget)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.i("check", "DocumentSnapshot added with ID: " + documentReference.getId());
                            dialog1.dismiss();
                            Toast.makeText(getActivity(), "Add budget successful", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("check", "Error adding document", e);
                        }
                    });

            db.collection("Budgets")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        });
        dialog1.show();
    }

    // Hiển thị hộp thoại 2
    private void showDialog2() {
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
            ImageView imageViewGr = dialog1.findViewById(R.id.imageViewGr); // Giả sử bạn đã định nghĩa imageViewGr trong dialog 1
            if (imageViewGr != null && selectedIconDrawable != null) {
                imageViewGr.setImageDrawable(selectedIconDrawable);  // Cập nhật icon
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
        dialog4.show();
    }

    private void handleOptionSelected(Button selectedButton) {
        if (dialog2 != null && dialog2.isShowing()) {  // Đóng dialog2 nếu đang mở
            dialog2.dismiss();
        }

        // Lấy text từ Button được nhấn
        String selectedText = selectedButton.getText().toString();
        if (btnSelectedOption != null) {
            btnSelectedOption.setText(selectedText);  // Cập nhật text trong Dialog 1
        }

        // Lấy icon từ Button được nhấn (compound drawable bên trái)
        Drawable icon = selectedButton.getCompoundDrawables()[0];
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
            imgiconclick.setImageDrawable(selectedIconDrawable);  // Cập nhật icon
        }

    }


}


