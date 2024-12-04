package org.example;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AddNewWeek {
    public static void addWeek(JTable table, DefaultTableModel model) {
        // Lưu trữ số cột hiện tại
        int currentColumnCount = model.getColumnCount();
        // Tạo mảng tên cột mới bao gồm cả cột cũ và cột mới
        String[] newColumnNames = new String[currentColumnCount + 4];
        Object[][] newData = new Object[table.getRowCount()][currentColumnCount + 4];

        // Sao chép toàn bộ dữ liệu hiện có
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int col = 0; col < currentColumnCount; col++) {
                newData[row][col] = table.getValueAt(row, col);
                if (row == 0) {
                    newColumnNames[col] = model.getColumnName(col);
                }
            }
        }

        // Thêm tên cho các cột mới
        for (int i = 0; i < 4; i++) {
            newColumnNames[currentColumnCount + i] = "Col " + (currentColumnCount + i + 1);
        }

        // Tạo model mới với dữ liệu đã được sao chép
        DefaultTableModel newModel = new DefaultTableModel(newData, newColumnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return row != 1;
            }
        };

        // Gán model mới cho bảng
        table.setModel(newModel);

        // Sao chép nội dung tiêu đề từ các cột 14-17 vào các cột mới
        for (int colOffset = 0; colOffset < 4; colOffset++) {
            Object headerValue = table.getValueAt(1, 14 + colOffset);
            if (headerValue != null) {
                table.setValueAt(headerValue, 1, currentColumnCount + colOffset);
            }
        }

        // Đặt tiêu đề cho tuần mới
        table.setValueAt("Tuần mới", 0, currentColumnCount);

        // Cập nhật giao diện
        table.revalidate();
        table.repaint();
    }
}