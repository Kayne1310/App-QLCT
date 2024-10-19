package com.example.projectappqlct;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificantFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificantFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Dialog dialog1, dialog2;
    private Button btnSelectedOption;

    public NotificantFragment() {
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
    public static NotificantFragment newInstance(String param1, String param2) {
        NotificantFragment fragment = new NotificantFragment();
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
        View view = inflater.inflate(R.layout.fragment_notificant, container, false);

        // Khởi tạo Pie chart
        Pie pie = AnyChart.pie();

        // Tạo danh sách dữ liệu
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("John", 10000));
        data.add(new ValueDataEntry("Jake", 12000));
        data.add(new ValueDataEntry("Peter", 18000));

        // Đặt dữ liệu cho Pie chart
        pie.data(data);

        // Tham chiếu đến AnyChartView sau khi view được khởi tạo
        AnyChartView anyChartView = view.findViewById(R.id.any_chart_view);
        anyChartView.setChart(pie);


        Button showDialogButton =view.findViewById(R.id.btnBG);
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog1();
            }
        });

        return view;
    }


    // Phương thức thiết lập Pie Chart
    private void setupPieChart(View view) {
        Pie pie = AnyChart.pie();
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("John", 10000));
        data.add(new ValueDataEntry("Jake", 12000));
        data.add(new ValueDataEntry("Peter", 18000));
        pie.data(data);

        AnyChartView anyChartView = view.findViewById(R.id.any_chart_view);
        anyChartView.setChart(pie);
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
    private void showDialog1() {
        if (dialog1 == null) {  // Khởi tạo dialog1 nếu chưa khởi tạo
            dialog1 = createDialog(R.layout.dialog_createbudget);
        }
        Button buttonSelect = dialog1.findViewById(R.id.btnSelectedOption);
        TextView backToFragment = dialog1.findViewById(R.id.textViewCancel);

        buttonSelect.setOnClickListener(v -> {
            dialog1.dismiss();  // Đóng dialog 1
            showDialog2();      // Mở dialog 2
        });
        dialog1.show();

        backToFragment.setOnClickListener(v -> {
            dialog1.dismiss();  // Đóng hộp thoại 1
        });


        // Tham chiếu đến Button trong Dialog 1
        btnSelectedOption = dialog1.findViewById(R.id.btnSelectedOption);

        // Nút mở Dialog 2
        Button buttonselect = dialog1.findViewById(R.id.btnShopping);
        buttonSelect.setOnClickListener(v -> {
            dialog1.dismiss();  // Đóng Dialog 1
            showDialog2();      // Mở Dialog 2
        });
        
    }


    // Hiển thị hộp thoại 2
    private void showDialog2() {
        if (dialog2 == null) {  // Khởi tạo dialog2 nếu chưa khởi tạo
            dialog2 = createDialog(R.layout.dialog_selectgroup);
        }
        Button buttonGrn = dialog2.findViewById(R.id.btnGrn);
        TextView backToDialog1 = dialog2.findViewById(R.id.textViewBack1);

        buttonGrn.setOnClickListener(v -> {
            dialog2.dismiss();  // Đóng dialog 2
            showDialog3();      // Mở dialog 3
        });

        backToDialog1.setOnClickListener(v -> {
            dialog2.dismiss();  // Đóng hộp thoại 2
            showDialog1();      // Quay lại hộp thoại 1
        });


        View.OnClickListener optionClickListener = v -> {
            String selectedText = "";
            int viewId = v.getId();  // Lấy ID của Button được nhấn

            // Dùng if-else để kiểm tra ID Button
            if (viewId == R.id.btnEat) {
                selectedText = "Eat";
            } else if (viewId == R.id.btnShopping) {
                selectedText = "Shopping";
            } else if (viewId == R.id.btnEdu) {
                selectedText = "Education";
            } else if (viewId == R.id.btnEntertainment) {
                selectedText = "Entertainment";
            }

            handleOptionSelected(selectedText);  // Cập nhật text và mở lại Dialog 1
        };

        dialog2.findViewById(R.id.btnEat).setOnClickListener(optionClickListener);
        dialog2.findViewById(R.id.btnShopping).setOnClickListener(optionClickListener);
        dialog2.findViewById(R.id.btnEdu).setOnClickListener(optionClickListener);
        dialog2.findViewById(R.id.btnEntertainment).setOnClickListener(optionClickListener);
        dialog2.show();
    }


    // Hiển thị hộp thoại 3
    private void showDialog3() {
        Dialog dialog3 = createDialog(R.layout.dialog_newgroup);
        Button buttonGrn = dialog3.findViewById(R.id.btnSelectGrParent);
        TextView backToDialog1 = dialog3.findViewById(R.id.textViewBack2);

        buttonGrn.setOnClickListener(v -> {
            dialog3.dismiss();  // Đóng dialog 3
            showDialog4();      // Mở dialog 4
        });
        dialog3.show();

        backToDialog1.setOnClickListener(v -> {
            dialog3.dismiss();  // Đóng hộp thoại 3
            showDialog2();      // Quay lại hộp thoại 2
        });
    }

    // Hiển thị hộp thoại 4
    private void showDialog4() {
        Dialog dialog4 = createDialog(R.layout.dialog_selectgroup2);
        TextView backToDialog1 = dialog4.findViewById(R.id.textViewBack3);
//        Button buttonGrn = dialog4.findViewById(R.id.btnGrn);
//        buttonGrn.setOnClickListener(v -> {
//            dialog4.dismiss();  // Đóng dialog 4
////            showDialog5();      // Mở dialog 4
//        });
        dialog4.show();

        backToDialog1.setOnClickListener(v -> {
            dialog4.dismiss();  // Đóng hộp thoại 4
            showDialog3();      // Quay lại hộp thoại 3
        });
    }

    private void handleOptionSelected(String selectedText) {
        if (dialog2 != null && dialog2.isShowing()) {  // Kiểm tra dialog2 trước khi đóng
            dialog2.dismiss();
        }

        if (btnSelectedOption != null) {
            btnSelectedOption.setText(selectedText);  // Cập nhật text trong Dialog 1
        }

        if (dialog1 != null) {  // Kiểm tra dialog1 trước khi mở
            dialog1.show();
        }
    }



}