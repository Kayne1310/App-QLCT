package com.example.projectappqlct.dialog;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projectappqlct.R;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectappqlct.R;

import java.util.Calendar;

public class DialogCreateExpensesActivity extends AppCompatActivity {
    private EditText editTextDate;
    private Calendar calendar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_expenses);  // Ensure you replace 'your_layout' with the actual layout file

        editTextDate = findViewById(R.id.editTextDate);
        calendar = Calendar.getInstance();

        // Disable direct input and show DatePickerDialog and TimePickerDialog on click
        editTextDate.setOnClickListener(v -> {
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Show DatePickerDialog first
            DatePickerDialog datePickerDialog = new DatePickerDialog(DialogCreateExpensesActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // After the date is selected, show the TimePickerDialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(DialogCreateExpensesActivity.this,
                                (timeView, selectedHour, selectedMinute) -> {
                                    // Format the selected date and time
                                    String dateTime = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear +
                                            " " + selectedHour + ":" + String.format("%02d", selectedMinute);
                                    editTextDate.setText(dateTime);
                                }, hour, minute, true);

                        timePickerDialog.show();
                    }, year, month, day);

            datePickerDialog.show();
        });
    }
}