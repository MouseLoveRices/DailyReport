package org.example;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Tạo frame
            JFrame frame = new JFrame("Custom Table Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);

            // Sử dụng lớp CustomTable để tạo bảng
            CustomTable customTable = new CustomTable();
            JPanel tablePanel = customTable.createTablePanel();

            // Thêm bảng vào frame
            frame.add(tablePanel);

            // Hiển thị frame
            frame.setVisible(true);
        });
    }
}
