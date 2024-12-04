package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;

public class TableDataManager {
    private static final String SAVE_FILE = "TableData.ser";
    private final JTable table;
    private DefaultTableModel model;

    public TableDataManager(JTable table, DefaultTableModel model) {
        this.table = table;
        this.model = model;
    }
    public void saveTableData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            // Lưu số hàng và số cột
            oos.writeInt(table.getRowCount());
            oos.writeInt(table.getColumnCount());

            // Lưu tên cột
            for (int col = 0; col < table.getColumnCount(); col++) {
                oos.writeObject(table.getColumnName(col));
            }

            // Lưu dữ liệu từng ô
            for (int row = 0; row < table.getRowCount(); row++) {
                for (int col = 0; col < table.getColumnCount(); col++) {
                    oos.writeObject(table.getValueAt(row, col));
                }
            }
            JOptionPane.showMessageDialog(null, "Lưu thành công!");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi lưu dữ liệu: " + e.getMessage());
        }
    }

    public void loadTableData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            // Đọc số hàng và số cột
            int rowCount = ois.readInt();
            int colCount = ois.readInt();

            // Đọc tên cột
            String[] columnNames = new String[colCount];
            for (int col = 0; col < colCount; col++) {
                columnNames[col] = (String) ois.readObject();
            }

            // Đọc dữ liệu
            Object[][] data = new Object[rowCount][colCount];
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < colCount; col++) {
                    data[row][col] = ois.readObject();
                }
            }

            // Tạo model mới với dữ liệu đã đọc
            DefaultTableModel newModel = new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return row != 1;
                }
            };

            // Cập nhật table với model mới
            table.setModel(newModel);
            this.model = newModel;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    public boolean hasSavedData() {
        File file = new File(SAVE_FILE);
        return file.exists() && file.length() > 0;
    }
}

