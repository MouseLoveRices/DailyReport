package org.example;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TableUtilities {

    // Tìm cột bắt đầu của tuần dựa trên ngày được chọn
    public static int findWeekStartColumn(JTable table, java.util.Date selectedDate) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");

        for (int col = 14; col < table.getColumnCount(); col += 4) {
            String dateRange = (String) table.getValueAt(0, col);
            if (dateRange != null && dateRange.contains("tới")) {
                String[] dates = dateRange.split("tới");
                try {
                    java.util.Date fromDate = sdf.parse(dates[0].trim().replace("Từ ", ""));
                    java.util.Date toDate = sdf.parse(dates[1].trim());
                    if (!selectedDate.before(fromDate) && !selectedDate.after(toDate)) {
                        return col; // Trả về cột bắt đầu của tuần
                    }
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1; // Không tìm thấy tuần phù hợp
    }

    // Hiển thị dialog để lọc tuần
    public static void showFilterWeekDialog(JTable table) {
        JPanel datePanel = new JPanel(new FlowLayout());
        JLabel dateLabel = new JLabel("Chọn ngày:");
        JDateChooser dateChooser = new JDateChooser();

        datePanel.add(dateLabel);
        datePanel.add(dateChooser);

        int result = JOptionPane.showConfirmDialog(null, datePanel, "Filter Tuần", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            java.util.Date selectedDate = dateChooser.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn ngày!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int weekStartCol = findWeekStartColumn(table, selectedDate);
            if (weekStartCol == -1) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy tuần cho ngày đã chọn.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            showFilteredWeekTable(table, weekStartCol);
        }
    }

    // Hiển thị bảng tuần đã chọn
    public static void showFilteredWeekTable(JTable mainTable, int startColumn) {
        int totalRows = mainTable.getRowCount();
        if (totalRows == 0) {
            JOptionPane.showMessageDialog(null, "Bảng không có dữ liệu!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] columnNames = new String[14 + 4];
        Object[][] data = new Object[totalRows][14 + 4];

        try {
            for (int col = 0; col < 14; col++) {
                columnNames[col] = mainTable.getColumnName(col);
                for (int row = 0; row < totalRows; row++) {
                    data[row][col] = mainTable.getValueAt(row, col);
                }
            }
            for (int colOffset = 0; colOffset < 4; colOffset++) {
                int colIndex = startColumn + colOffset;
                columnNames[14 + colOffset] = mainTable.getColumnName(colIndex);
                for (int row = 0; row < totalRows; row++) {
                    data[row][14 + colOffset] = mainTable.getValueAt(row, colIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi sao chép dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable filteredTable = new JTable(model);

        // Add MouseListener to enable date picker
        filteredTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = filteredTable.rowAtPoint(evt.getPoint());
                int col = filteredTable.columnAtPoint(evt.getPoint());
                if (col >= 14) { // Restrict to week-specific columns
                    DatePickerHelper.showSingleDatePicker(filteredTable, row, col);
                }
            }
        });

        filteredTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        filteredTable.setDefaultRenderer(Object.class, new CustomCellRenderer());
        filteredTable.setRowHeight(40);

        JScrollPane scrollPane = new JScrollPane(filteredTable);
        JDialog dialog = new JDialog();
        dialog.setTitle("Dữ liệu tuần được chọn");
        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton saveButton = new JButton("Lưu thay đổi");
        saveButton.addActionListener(e -> {
            try {
                for (int col = 0; col < 14; col++) {
                    for (int row = 0; row < totalRows; row++) {
                        mainTable.setValueAt(filteredTable.getValueAt(row, col), row, col);
                    }
                }
                for (int colOffset = 0; colOffset < 4; colOffset++) {
                    int colIndex = startColumn + colOffset;
                    for (int row = 0; row < totalRows; row++) {
                        mainTable.setValueAt(filteredTable.getValueAt(row, 14 + colOffset), row, colIndex);
                    }
                }
                JOptionPane.showMessageDialog(dialog, "Thay đổi đã được lưu vào bảng chính!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Lỗi khi lưu thay đổi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton exportButton = new JButton("Xuất file Excel");
        exportButton.addActionListener(e -> ExportExcel.exportToExcel(filteredTable));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(exportButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

}
