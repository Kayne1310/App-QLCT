package com.example.projectappqlct.Helper;

import com.ekn.gruzer.gaugelibrary.contract.ValueFormatter;

public class CustomValueFormatter implements ValueFormatter {
    @Override
    public String getFormattedValue(double value) {
        if (value < 0) {
            // Xử lý giá trị âm
            if (value > -1000) {
                return String.valueOf((int) value); // Trả về giá trị nguyên nếu trên -1000
            } else if (value > -1_000_000) {
                return String.format("%.1fk", value / 1000); // Định dạng thành "-k"
            } else {
                return String.format("%.1fM", value / 1_000_000); // Định dạng thành "-M"
            }
        } else {
            // Xử lý giá trị dương
            if (value < 1000) {
                return String.valueOf((int) value); // Trả về giá trị nguyên nếu dưới 1000
            } else if (value < 1_000_000) {
                return String.format("%.1fk", value / 1000); // Định dạng thành "k"
            } else {
                return String.format("%.1fM", value / 1_000_000); // Định dạng thành "M"
            }
        }
    }
}
