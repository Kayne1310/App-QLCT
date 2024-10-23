//package com.example.projectappqlct;
//
//import android.app.DatePickerDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.projectappqlct.Model.Budget;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.Calendar;
//import java.util.List;
//
//public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
//    private List<Budget> budgetList;
//    private FirebaseFirestore db;
//    private  Dialog dialog;
//    public BudgetAdapter(List<Budget> budgetList) {
//        this.budgetList = budgetList;
//        this.db = FirebaseFirestore.getInstance();
//    }
//
//    @NonNull
//    @Override
//    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
//        return new BudgetViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
//        Budget budget = budgetList.get(position);
//        holder.bind(budget);
//
//        // Xử lý sự kiện xóa Budget
//        holder.buttonDelete.setOnClickListener(v -> {
//            db.collection("Budgets").document(budget.getId())
//                    .delete()
//                    .addOnSuccessListener(aVoid -> {
//                        budgetList.remove(position);
//                        notifyItemRemoved(position);
//                    })
//                    .addOnFailureListener(e -> Log.w("TAG", "Error deleting document", e));
//        });
//
//        // Xử lý sự kiện sửa Budget (mở lại dialog1 với dữ liệu hiện tại)
//        holder.buttonEdit.setOnClickListener(v -> {
//            // Implement logic for editing budget if needed
//            openEditDialog(holder.itemView.getContext(), budget, position);
//        });
//    }
//
//    private void openEditDialog(Context context, Budget budget, int position) {
//        Dialog dialog = new Dialog(context);
//        dialog.setContentView(R.layout.dialog_createbudget);
//
//        Window window = dialog.getWindow();
//        if (window != null) {
//            WindowManager.LayoutParams params = window.getAttributes();
//            window.setWindowAnimations(R.style.DialogAnimation);  // Animation cho dialog
//            params.width = WindowManager.LayoutParams.MATCH_PARENT;
//            params.height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.9);
//            params.gravity = Gravity.TOP;  // Vị trí dialog ở trên cùng
//            window.setAttributes(params);
//        }
//
//        // Tham chiếu đến các thành phần trong dialog
//        EditText editTextAmount = dialog.findViewById(R.id.editTextAmount);
//        EditText editTextCalendar = dialog.findViewById(R.id.editTextCalendar);
//        Button buttonSelect = dialog.findViewById(R.id.btnSelectedOption);
//        Button buttonSubmit = dialog.findViewById(R.id.btnAddBudget);
//
//        // Đổ dữ liệu hiện tại vào các trường
//        editTextAmount.setText(String.valueOf(budget.getAmount()));
//        editTextCalendar.setText(budget.getCalendar());
//        buttonSelect.setText(budget.getGroup());
//
//        // Xử lý chọn ngày mới bằng DatePicker
//        editTextCalendar.setOnClickListener(v -> {
//            final Calendar calendar = Calendar.getInstance();
//            int year = calendar.get(Calendar.YEAR);
//            int month = calendar.get(Calendar.MONTH);
//            int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
//                    (view, selectedYear, selectedMonth, selectedDay) ->
//                            editTextCalendar.setText(String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)),
//                    year, month, day);
//            datePickerDialog.show();
//        });
//
//        // Xử lý khi nhấn Submit để cập nhật budget
//        buttonSubmit.setOnClickListener(v -> {
//            String newAmount = editTextAmount.getText().toString().trim();
//            String newCalendar = editTextCalendar.getText().toString().trim();
//            String newGroup = buttonSelect.getText().toString().trim();
//
//            // Cập nhật budget với dữ liệu mới
//            budget.setAmount(Integer.parseInt(newAmount));
//            budget.setCalendar(newCalendar);
//            budget.setGroup(newGroup);
//
//            // Cập nhật budget trong Firestore
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//            db.collection("Budgets").document(budget.getId())
//                    .set(budget)
//                    .addOnSuccessListener(aVoid -> {
//                        notifyItemChanged(position);  // Cập nhật RecyclerView
//                        dialog.dismiss();  // Đóng dialog sau khi cập nhật thành công
//                    })
//                    .addOnFailureListener(e -> Log.e("TAG", "Error updating document", e));
//        });
//
//        dialog.show();  // Hiển thị dialog
//    }
//
//
//
//
//    @Override
//    public int getItemCount() {
//        return budgetList.size();
//    }
//
//    static class BudgetViewHolder extends RecyclerView.ViewHolder {
//        TextView textViewAmount, textViewCalendar, textViewGroup;
//        Button buttonEdit, buttonDelete;
//
//        public BudgetViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textViewAmount = itemView.findViewById(R.id.textViewAmount);
//            textViewCalendar = itemView.findViewById(R.id.textViewCalendar);
//            textViewGroup = itemView.findViewById(R.id.textViewGroup);
//            buttonEdit = itemView.findViewById(R.id.buttonEdit);
//            buttonDelete = itemView.findViewById(R.id.buttonDelete);
//        }
//
//        public void bind(Budget budget) {
//            textViewAmount.setText(String.valueOf(budget.getAmount()));
//            textViewCalendar.setText(budget.getCalendar());
//            textViewGroup.setText(budget.getGroup());
//        }
//    }
//}
