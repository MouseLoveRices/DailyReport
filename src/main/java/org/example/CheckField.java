package org.example;

import javax.swing.*;

public class CheckField {
    public static boolean checkMandatoryFieldsFilled(JTable table, int row) {
        // Bỏ qua việc kiểm tra cho dòng 0 và 1
        if (row < 2) {
            return true;
        }

        // Kiểm tra giá trị ở cột Bệnh Viện (index 4)
        Object hospitalValue = table.getValueAt(row, 4);
        // Nếu là "Medisol" thì luôn cho phép nhập
        if (hospitalValue != null && hospitalValue.toString().trim().equalsIgnoreCase("Medisol")) {
            return true;
        }

        // Kiểm tra các cột bắt buộc
        Object[] mandatoryColumns = {
                table.getValueAt(row, 4),   // Bệnh Viện - Hospital
                table.getValueAt(row, 8),   // Khoa - Department
                table.getValueAt(row, 10)   // Họ và tên BS - Dr Name
        };

        // Kiểm tra xem các cột bắt buộc có giá trị không
        for (Object value : mandatoryColumns) {
            if (value == null || value.toString().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Vui lòng điền đầy đủ thông tin:\n" +
                                "- Bệnh Viện\n" +
                                "- Khoa\n" +
                                "- Họ và tên Bác sĩ\n" +
                                "Trước khi nhập thông tin tuần!",
                        "Thiếu thông tin",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        return true;
    }
}
