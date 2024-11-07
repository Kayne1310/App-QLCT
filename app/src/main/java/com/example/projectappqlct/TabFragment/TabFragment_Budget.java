package com.example.projectappqlct.TabFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ekn.gruzer.gaugelibrary.Range;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.example.projectappqlct.Adapter.BudgetAdapter;
import com.example.projectappqlct.Adapter.OptionAdapter;
import com.example.projectappqlct.Fragment.BudgetFragment;
import com.example.projectappqlct.Helper.CustomValueFormatter;
import com.example.projectappqlct.Model.Budget;
import com.example.projectappqlct.Model.Expense;
import com.example.projectappqlct.Model.Option;
import com.example.projectappqlct.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabFragment_Budget#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragment_Budget extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userString;
    private Dialog dialog1, dialog2, dialog3, dialog4;
    private Button btnSelectedOption, buttonSelect;
    private ImageView imgiconclick, imageviewGr;
    private Drawable selectedIconDrawable, icon;
    private FirebaseFirestore db;
    private BudgetAdapter budgetAdapter;
    private List<Budget> budgetList;
    private RecyclerView recyclerView;
    private String selectedIconTag;
    private ProgressBar progressBar;
    private static final String ARG_BUDGETS = "Budgets";
    private OptionAdapter optionAdapter;
    private List<Option> optionList = new ArrayList<>();
    private EditText editTextAmount, editTextDate;
    private ImageView imageViewGr;
    private int totalAmount = 0;
    private int totalDeficit = 0;
    private int budgetCount = 0;
    private int processedBudgets = 0;
    private ArcGauge arcGauge;
    private TextView textViewTotalBudget, textViewTotalExpense, textViewEndOfMonth;
    private String selectedMonthYear;
    private List<Budget> budgets;
    private ProgressBar loadingExpense;



    public TabFragment_Budget() {
        // Required empty public constructor
    }

    public static TabFragment_Budget newInstance(List<Budget> budgets, String monthYear) {
        TabFragment_Budget fragment = new TabFragment_Budget();
        Bundle args = new Bundle();
        args.putSerializable("Budgets", (Serializable) budgets); // Truyền danh sách ngân sách
        args.putString("monthYear", monthYear); // Truyền tháng/năm
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

        if (getArguments() != null) {
            budgetList = (List<Budget>) getArguments().getSerializable(ARG_BUDGETS);
            selectedMonthYear = getArguments().getString("monthYear");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab, container, false);

        // Ánh xạ ArcGauge từ layout
        arcGauge = view.findViewById(R.id.arcGauge);
        loadingExpense = view.findViewById(R.id.loadingExpense);
        // Ánh xạ các TextView từ layout
        textViewTotalBudget = view.findViewById(R.id.TextViewTotalBudget);
        textViewTotalExpense = view.findViewById(R.id.TextViewTotalExpense);
        textViewEndOfMonth = view.findViewById(R.id.TextViewEndOfMonth);

        // Thiết lập các Range cho ArcGauge
        setupGaugeRanges();

        // Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Lấy dữ liệu nếu người dùng đã đăng nhập
        if (user != null) {
            userString = user.getUid();
            loadBudgets();
        } else {
            Log.e("AuthError", "Người dùng chưa đăng nhập");
        }


        Button showDialogButton = view.findViewById(R.id.btnBG);
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog1();
            }
        });

        // recycleview budget
        recyclerView = view.findViewById(R.id.recyclerViewBudgets);
        recyclerView.setVisibility(View.INVISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new BudgetAdapter(getActivity(), budgetList));

        // Cập nhật TextView đếm ngược đến cuối tháng
        updateDaysRemainingToEndOfMonth();

        return view;
    }

    private void setupGaugeRanges() {
        Range range1 = new Range();
        range1.setColor(Color.parseColor("#ce0000"));
        range1.setFrom(0);
        range1.setTo(50);

        Range range2 = new Range();
        range2.setColor(Color.parseColor("#E3E500"));
        range2.setFrom(50);
        range2.setTo(100);

        Range range3 = new Range();
        range3.setColor(Color.parseColor("#00b20b"));
        range3.setFrom(100);
        range3.setTo(150);

        arcGauge.addRange(range1);
        arcGauge.addRange(range2);
        arcGauge.addRange(range3);
    }

    private void loadBudgets() {
        loadingExpense.setVisibility(View.VISIBLE);

        // Ẩn RecyclerView và ArcGauge

        arcGauge.setVisibility(View.INVISIBLE);

        db.collection("users").document(userString).collection("Budgets")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        budgetCount = 0;
                        totalAmount = 0;
                        totalDeficit = 0;

                        for (DocumentSnapshot document : task.getResult()) {
                            Budget budget = document.toObject(Budget.class);
                            if (budget != null) {
                                // Lấy tháng/năm từ calendar của Budget
                                String budgetMonthYear = budget.getCalendar().substring(3);
                                if (budgetMonthYear.equals(selectedMonthYear)) {
                                    // Nếu trùng tháng/năm, tính tổng ngân sách và xử lý chi tiêu
                                    budgetCount++;
                                    totalAmount += budget.getAmount();
                                    fetchTotalExpenseForBudget(budget, budget.getAmount(), selectedMonthYear);
                                }
                            }
                        }
                    } else {
                        Log.e("BudgetError", "Error fetching budgets", task.getException());
                    }
                    if (budgetCount > 0) {
                        arcGauge.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        loadingExpense.setVisibility(View.GONE);
                    }
                });
    }


    private void fetchTotalExpenseForBudget(Budget budget, int budgetAmount, String selectedMonthYear) {
        if (budget.getCalendar() == null) return;

        db.collection("users").document(userString).collection("Expenses")
                .whereEqualTo("group", budget.getGroup())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int totalExpense = 0;
                        for (DocumentSnapshot document : task.getResult()) {
                            Expense expense = document.toObject(Expense.class);

                            if (expense != null) {
                                // Lấy chuỗi MM/yyyy từ calendar của Expense
                                String expenseMonthYear = expense.getCalendar().substring(3);

                                // So sánh MM/yyyy của Budget và Expense với tab hiện tại
                                if (expenseMonthYear.equals(selectedMonthYear)) {
                                    totalExpense += expense.getAmount();
                                }
                            }
                        }

                        int deficit = budgetAmount - totalExpense;
                        totalDeficit += deficit;
                        processedBudgets++;

                        // Cập nhật gauge khi tất cả ngân sách được xử lý
                        if (processedBudgets == budgetCount) {
                            updateGauge();
                        }
                    } else {
                        Log.e("ExpenseError", "Error fetching expenses", task.getException());
                    }
                });
    }


    private void updateGauge() {
        Log.d("GaugeUpdate", "Total Amount: " + totalAmount + ", Total Deficit: " + totalDeficit);

        arcGauge.setMinValue(0);
        arcGauge.setMaxValue(totalAmount);
        arcGauge.setValue(totalDeficit);  // Giá trị đường tiến độ của gauge

        // Cập nhật TextView tổng ngân sách và tổng chi tiêu
        textViewTotalBudget.setText(formatCurrency(totalAmount));
        textViewTotalExpense.setText(formatCurrency(totalAmount - totalDeficit));


        arcGauge.setFormatter(new CustomValueFormatter());
    }

    private String formatGaugeValue(double value) {
        if (value < 1000) {
            return String.valueOf((int) value); // Trả về giá trị nguyên nếu dưới 1000
        } else if (value < 1_000_000) {
            return String.format("%.1fk", value / 1000); // Định dạng thành "k"
        } else {
            return String.format("%.1fM", value / 1_000_000); // Định dạng thành "M"
        }
    }

    private void updateDaysRemainingToEndOfMonth() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Log.e("AuthError", "Người dùng chưa đăng nhập");
            return;
        }

        // Lấy tháng/năm từ biến selectedMonthYear
        if (selectedMonthYear == null) {
            Log.e("InputError", "selectedMonthYear không được xác định");
            return;
        }

        // Phân tách tháng và năm từ selectedMonthYear
        String[] monthYearParts = selectedMonthYear.split("/");
        if (monthYearParts.length != 2) {
            Log.e("InputError", "Định dạng selectedMonthYear không đúng");
            return;
        }

        int month = Integer.parseInt(monthYearParts[0]) - 1; // Giảm 1 vì Calendar.MONTH bắt đầu từ 0
        int year = Integer.parseInt(monthYearParts[1]);

        // Thiết lập ngày hiện tại từ hệ thống
        Calendar currentDate = Calendar.getInstance();
        // Lấy ngày hôm nay
        int today = currentDate.get(Calendar.DAY_OF_MONTH);

        // Xác định ngày cuối tháng
        Calendar endOfMonth = Calendar.getInstance();
        endOfMonth.set(year, month, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));

        // Tính số ngày còn lại
        long daysRemaining = (endOfMonth.getTimeInMillis() - currentDate.getTimeInMillis()) / (24 * 60 * 60 * 1000);

        // Nếu ngày hiện tại lớn hơn ngày cuối tháng, đặt daysRemaining bằng 0
        if (today > endOfMonth.get(Calendar.DAY_OF_MONTH)) {
            daysRemaining = 0;
        } else {
            daysRemaining = Math.max(daysRemaining, 0); // Đảm bảo không có số ngày âm
        }

        // Cập nhật vào TextView
        textViewEndOfMonth.setText(daysRemaining + " Day");
    }


    private String formatCurrency(int amount) {
        return String.format("%,d", amount);
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

        buttonSelect = dialog1.findViewById(R.id.btnSelectedOption);
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
        editTextAmount = dialog1.findViewById(R.id.editTextAmount);
        editTextDate = dialog1.findViewById(R.id.editTextCalendar);
        imageviewGr = dialog1.findViewById(R.id.imageViewGr);

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


            // Budget budget = new Budget(AmountInt, Calendar, Group, Icon);

            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            userString = user.getUid();

            // Truy vấn Firestore để kiểm tra dữ liệu trùng lặp
            String finalIcon = Icon;
            if (userString != null) {
                // Truy vấn Firestore để kiểm tra dữ liệu trùng lặp từ server
                db.collection("users").document(userString)
                        .collection("Budgets")
                        .whereEqualTo("group", Group)
                        .whereEqualTo("calendar", Calendar)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                // Dữ liệu đã tồn tại với group và calendar trùng lặp
                                Toast.makeText(getActivity(), "This budget entry already exists", Toast.LENGTH_SHORT).show();
                            } else {
                                updateDataBudget(AmountInt, Calendar, Group, finalIcon);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirestoreError", "Error checking duplicate data", e);
                        });
            }

        });

        dialog1.show();
    }

    public void updateDataBudget(int AmountInt, String Calendar, String Group, String finalIcon) {
        // Nếu không trùng lặp, tiếp tục thêm dữ liệu
        Budget budget = new Budget(AmountInt, Calendar, Group, finalIcon);

        db.collection("users").document(userString)
                .collection("Budgets")
                .add(budget)
                .addOnSuccessListener(documentReference -> {
                    String budgetId = documentReference.getId(); // Lấy ID
                    budget.setId(budgetId); // Cập nhật ID vào đối tượng Budget

                    // Lưu budget với ID vào Firestore
                    documentReference.set(budget)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getActivity(), "Add budget successful", Toast.LENGTH_SHORT).show();
                                if (dialog1 != null && dialog1.isShowing()) {
                                    dialog1.dismiss();
                                }

                                BudgetFragment parentFragment = (BudgetFragment) getParentFragment();
                                if (parentFragment != null) {
                                    parentFragment.addBudgetData(budget);
                                    parentFragment.loadBudgetsAndSetupTabs(); // Thêm dữ liệu mới vào ViewPager và TabLayout
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FirestoreError", "Error updating budget ID", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error adding document", e);
                });
    }


    public void djtkonme(int AmountInt, String Calendar, String Group, String finalIcon) {
        // Nếu không trùng lặp, tiếp tục thêm dữ liệu
        Budget budget = new Budget(AmountInt, Calendar, Group, finalIcon);

        db.collection("users").document(userString)
                .collection("Budgets")
                .add(budget)
                .addOnSuccessListener(documentReference -> {
                    String budgetId = documentReference.getId(); // Lấy ID
                    budget.setId(budgetId); // Cập nhật ID vào đối tượng Budget

                    // Lưu budget với ID vào Firestore
                    documentReference.set(budget)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getActivity(), "Add budget successful", Toast.LENGTH_SHORT).show();
                                if (dialog1 != null && dialog1.isShowing()) {
                                    dialog1.dismiss();
                                }

                                BudgetFragment parentFragment = (BudgetFragment) getParentFragment();
                                if (parentFragment != null) {
                                    parentFragment.addBudgetData(budget);
                                    parentFragment.loadBudgetsAndSetupTabs(); // Thêm dữ liệu mới vào ViewPager và TabLayout
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FirestoreError", "Error updating budget ID", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error adding document", e);
                });
    }

    // Hiển thị hộp thoại 2
    public void showDialog2() {
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
            showDialog1();      // Quay lại hộp thoại 1
        });

        // Tìm RecyclerView
        RecyclerView recyclerView = dialog2.findViewById(R.id.recyclerViewOption);
        if (recyclerView == null) {
            Log.e("ShowDialog2", "RecyclerView is null! Check the layout file.");
            return; // Hoặc xử lý lỗi nếu cần
        }

        // Khởi tạo LayoutManager cho RecyclerView nếu chưa có
        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                int iconResId = getActivity().getResources().getIdentifier(selectedOption.getIcon(), "drawable", getActivity().getPackageName());
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

        // Thêm divider giữa các item
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);

        // Nếu muốn sử dụng divider tùy chỉnh, hãy đặt drawable của bạn
//        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));

        // Thêm divider vào RecyclerView
//        recyclerView.addItemDecoration(dividerItemDecoration);


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



    private void showDialog3() {
        if (dialog3 == null) {  // Khởi tạo dialog3 nếu chưa khởi tạo
            dialog3 = createDialog(R.layout.dialog_newgroup);
        }

        // Chọn icon
        ImageView imgIconClick = dialog3.findViewById(R.id.imgicon);
        imgIconClick.setOnClickListener(v -> {
            showDialog4();  // Mở dialog 4 để chọn icon
        });

        // Chọn text
        EditText editTextOption = dialog3.findViewById(R.id.editTextNameGr);

        // Nút Lưu
        Button buttonSave = dialog3.findViewById(R.id.btnSave);
        buttonSave.setOnClickListener(v -> {
            String selectedText = editTextOption.getText().toString().trim();

            // Kiểm tra tên option không trống
            if (selectedText.isEmpty()) {
                Toast.makeText(dialog3.getContext(), "The name option cannot be left blank!", Toast.LENGTH_SHORT).show();
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

        TextView backToDialog2 = dialog3.findViewById(R.id.textViewBack2);
        backToDialog2.setOnClickListener(v -> {
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
                        Toast.makeText(getActivity(), "Save successful!", Toast.LENGTH_SHORT).show();

                        // Sau khi lưu, tải lại danh sách options để cập nhật RecyclerView trong dialog2
                        loadOptionsFromFirestore();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getActivity(), "Dont save   option.", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(getActivity(), "Failed to load options.", Toast.LENGTH_SHORT).show();
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
                selectedOption.getIcon(), "drawable", getActivity().getPackageName()
        );
        imgSelectedOption.setImageResource(iconResId);

        // Mở lại Dialog 1
        if (dialog1 != null) {
            dialog1.show();
        }
    }



}