package org.example;
import static org.example.DatePickerHelper.showDateInputDialog;
import static org.example.DatePickerHelper.showSingleDatePicker;
import static org.example.TableUtilities.showFilterWeekDialog;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class CustomTable {

    private static JTable table;
    private  DefaultTableModel model;
    private TableDataManager dataManager;
    private AutoCompleteManager autoCompleteManager;

    private int currentSelectedRow = -1;
    private int currentSelectedCol = -1;

    public JPanel createTablePanel() {
        // Số cột và số hàng
        int columnCount = 18;
        int rowCount = 10;
        dataManager = new TableDataManager(table,model);

        // Tạo dữ liệu mặc định
        Object[][] data = new Object[rowCount][columnCount];
        String[] columnNames = new String[columnCount];

        // Đặt tên cột
        for (int i = 0; i < columnCount; i++) {
            columnNames[i] = "Col " + (i + 1);
        }
        // Đặt tên cột cho hàng thứ 2 và từ cột 1 đến cột 14
        for (int i = 0; i < columnCount; i++) {
            if (i == 0) data[1][i] = "STT- No";
            else if (i == 1) data[1][i] = "Họ và tên nhân viên - MR Name";
            else if (i == 2) data[1][i] = "Khu Vực - REGION";
            else if (i == 3) data[1][i] = "Tỉnh - Province";
            else if (i == 4) data[1][i] = "Bệnh Viện - Hospital";
            else if (i == 5) data[1][i] = "Địa chỉ BV - Address";
            else if (i == 6) data[1][i] = "Hạng BV - Class";
            else if (i == 7) data[1][i] = "Tính chất BV - Title (public (BV Công)/ private (BV Tư nhân)/ CLINIC/SPS (PK))";
            else if (i == 8) data[1][i] = "Khoa - Department";
            else if (i == 9) data[1][i] = "Số giường bệnh của BV - No of Bed";
            else if (i == 10) data[1][i] = "Họ và tên BS - Dr Name";
            else if (i == 11) data[1][i] = "Vị trí BS - Position";
            else if (i == 12) data[1][i] = "Số bệnh nhân BS điều trị trong ngày - No Patients /day";
            else if (i == 13) data[1][i] = "Số điện thoại BS - Phone No";
        }
        for (int i = 14; i < columnCount; i++) {
            if (i == 14) data[1][i] = "Ngày - Date";
            else if (i == 15) data[1][i] = "Kế hoạch tuần sau - Plan Content";
            else if (i == 16) data[1][i] = "Báo cáo ngày - Report Content";
            else if (i == 17) data[1][i] = "Ghi chú - Noted";
        }
        // Tạo model cho bảng
        model = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                // Vô hiệu hóa chỉnh sửa cho dòng thứ 1
                return row != 1;
            }
        };

        // Tạo bảng
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // Đặt chiều cao hàng
        table.setRowHeight(40);
        // Tạo cell renderer tùy chỉnh để tô màu
        table.setDefaultRenderer(Object.class, new CustomCellRenderer());
        dataManager = new TableDataManager(table, model);
        // Tạo JScrollPane chứa bảng
        JScrollPane scrollPane = new JScrollPane(table);

        // Tạo ô input
        JTextField inputField = new JTextField(100);
        inputField.setPreferredSize(new Dimension(100, 40));
        JLabel inputLabel = new JLabel("Chỉnh sửa ô:");
        JLabel remindLable = new JLabel("Nhớ Enter");
        inputField.setVisible(false);
        inputLabel.setVisible(false);
        remindLable.setVisible(false);

        // Bắt sự kiện click chuột để hiển thị nội dung ô vào inputField
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentSelectedRow = table.getSelectedRow();
                currentSelectedCol = table.getSelectedColumn();

                autoCompleteManager = new AutoCompleteManager(table);
                // Kiểm tra xem có phải là cột ngày không (cột 14, 18, 22,...)
                boolean isDateColumn = currentSelectedCol >= 14 && (currentSelectedCol - 14) % 4 == 0;
                if (isDateColumn) {
                    if (currentSelectedRow == 0) {
                        // Xử lý cho dòng đầu tiên như cũ
                        showDateInputDialog(table, currentSelectedCol);
                    } else if (currentSelectedRow >= 2) {  // Từ dòng 3 trở đi
                        // Hiển thị date chooser đơn giản cho các dòng khác
                        showSingleDatePicker(table, currentSelectedRow, currentSelectedCol);
                    }
                }
                // Kiểm tra nếu là cột từ 15 trở đi (index 14 trở đi)
                if (currentSelectedCol >= 14) {
                    // Kiểm tra các trường bắt buộc
                    if (!CheckField.checkMandatoryFieldsFilled(table, currentSelectedRow)) {
                        return;  // Không cho phép nhập nếu thiếu thông tin
                    }
                }
                if (currentSelectedRow >= 0 && currentSelectedCol >= 0) {
                    Object value = table.getValueAt(currentSelectedRow, currentSelectedCol);
                    // Hiển thị input field và label
                    inputField.setText(value == null ? "" : value.toString());
                    inputField.setVisible(true);
                    inputLabel.setVisible(true);
                    remindLable.setVisible(true);

                    // Thêm FocusListener để cập nhật currentSelectedRow và currentSelectedCol
                    inputField.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            // Cập nhật lại vị trí ô được chọn khi inputField được focus
                            currentSelectedRow = table.getSelectedRow();
                            currentSelectedCol = table.getSelectedColumn();
                        }
                    });

                    inputField.requestFocus();
                    int rowNumber = currentSelectedRow - 1;
                    int colNumber = currentSelectedCol + 1;
                    if (rowNumber <= 0) {
                        rowNumber = 0;
                    }
                    inputLabel.setText(String.format("Chỉnh sửa ô: [Hàng %d, Cột %d]", rowNumber, colNumber));
                }
                if (currentSelectedRow >= 2 && currentSelectedCol >= 1 && currentSelectedCol <= 13) {
                    autoCompleteManager.configureAutoComplete(inputField, currentSelectedCol);
                    autoCompleteManager.setupTableAutoComplete();
                }

            }
        });
        // Cập nhật nội dung ô khi nhấn Enter
        inputField.addActionListener(e -> {
            if (currentSelectedRow >= 0 && currentSelectedCol >= 0) {
                table.setValueAt(inputField.getText(), currentSelectedRow, currentSelectedCol);
                inputField.setText("");
            }
        });
        // Thêm chức năng xóa nội dung khi nhấn phím Delete
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    if (currentSelectedRow >= 0 && currentSelectedCol >= 0) {
                        table.setValueAt(null, currentSelectedRow, currentSelectedCol);
                        inputField.setText("");
                    }
                }
            }
        });

        // Tạo JPanel để chứa bảng
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        // Tạo JPanel chứa ô input và thêm vào giao diện
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(inputLabel);
        inputPanel.add(inputField);
        inputPanel.add(remindLable);
        panel.add(inputPanel, BorderLayout.NORTH);

        // Bên trong phương thức createTablePanel()
        JButton addRowButton = new JButton("Thêm Dòng");
        addRowButton.addActionListener(e -> {
            Object[] newRow = new Object[columnCount]; // Tạo một dòng mới với số cột giống bảng
            model.addRow(newRow); // Thêm dòng mới vào model
            updateRowNumbers(table,0);
        });

        // Tạo nút xuất ra Excel
        JButton exportButton = new JButton("Lưu thành file Excel");
        exportButton.addActionListener(e -> ExportExcel.exportToExcel(table));

        // Thêm nút "Thêm Tuần"
        JButton addWeekButton = new JButton("Thêm Tuần");
        addWeekButton.addActionListener(e -> {
            AddNewWeek.addWeek(table, model);

            // Cập nhật lại model và dataManager
            model = (DefaultTableModel) table.getModel();
            dataManager = new TableDataManager(table, model);

            // Cập nhật lại số thứ tự nếu cần
            updateRowNumbers(table, 0);
        });

        //Thêm nút filter tuần
        JButton filterWeekButton = new JButton("Filter Tuần");
        filterWeekButton.addActionListener(e -> showFilterWeekDialog(table));

        JButton saveButton = new JButton("Lưu (Ctrl+S)");
        saveButton.addActionListener(e->{dataManager.saveTableData();});

        // Gán tổ hợp phím Ctrl+S để gọi hàm saveTableData()
        InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = panel.getActionMap();

        // Đăng ký tổ hợp phím Ctrl+S
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
        inputMap.put(keyStroke, "saveTableData");
        actionMap.put("saveTableData", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dataManager.saveTableData();
            }
        });

        // Thêm nút vào giao diện
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addRowButton);
        buttonPanel.add(addWeekButton);
        buttonPanel.add(filterWeekButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(exportButton); // Thêm cả nút export để nhóm các nút lại
        panel.add(buttonPanel, BorderLayout.SOUTH);

        if (dataManager.hasSavedData()){
            dataManager.loadTableData();
            model = (DefaultTableModel) table.getModel();
            updateRowNumbers(table,0);
        }else {
            updateRowNumbers(table,0);
        }
        return panel;
    }

    private void updateRowNumbers(JTable table, int columnIndex) {
        for (int row = 2; row < table.getRowCount(); row++) {
            table.setValueAt(row - 1, row, columnIndex); // Đặt STT bắt đầu từ 1
        }
    }

}