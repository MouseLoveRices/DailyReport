package org.example;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AutoCompleteManager {
    private final JTable table;

    public AutoCompleteManager(JTable table) {
        this.table = table;
    }

    // Đọc danh sách gợi ý từ tệp
    public List<String> readSuggestionsFromFile(String columnName) {
        File suggestFile = new File(columnName + ".txt");
        List<String> suggestions = new ArrayList<>();

        if (suggestFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(suggestFile, StandardCharsets.UTF_8))) {
                suggestions = reader.lines()
                        .filter(line -> !line.trim().isEmpty())
                        .collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return suggestions;
    }

    // Thêm gợi ý mới vào tệp
    public void addToSuggestionsFile(String columnName, String newSuggestion) {
        File suggestFile = new File(columnName + ".txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(suggestFile, true))) {
            writer.write(newSuggestion);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Lấy tên cột dựa trên chỉ số
    public String getColumnNameByIndex(int column) {
        return switch (column) {
            case 0 -> "STT- No";
            case 1 -> "Họ và tên nhân viên - MR Name";
            case 2 -> "Khu Vực - REGION";
            case 3 -> "Tỉnh - Province";
            case 4 -> "Bệnh Viện - Hospital";
            case 5 -> "Địa chỉ BV - Address";
            case 6 -> "Hạng BV - Class";
            case 7 -> "Tính chất BV - Title (public (BV Công)/ private (BV Tư nhân)/ CLINIC/SPS (PK))";
            case 8 -> "Khoa - Department";
            case 9 -> "Số giường bệnh của BV - No of Bed";
            case 10 -> "Họ và tên BS - Dr Name";
            case 11 -> "Vị trí BS - Position";
            case 12 -> "Số bệnh nhân BS điều trị trong ngày - No Patients /day";
            case 13 -> "Số điện thoại BS - Phone No";
            default -> "Col " + (column + 1);
        };
    }

    // Thiết lập tự động hoàn thiện cho JTextField
    public void configureAutoComplete(JTextField textField, int column) {
        String columnName = getColumnNameByIndex(column);
        List<String> suggestions = readSuggestionsFromFile(columnName);

        if (!suggestions.isEmpty()) {
            AutoCompleteDocument autoCompleteDoc = new AutoCompleteDocument(textField, suggestions);
            textField.setDocument(autoCompleteDoc);

            // Xử lý thêm gợi ý mới
            textField.addActionListener(e -> {
                String currentText = textField.getText().trim();
                if (!currentText.isEmpty() && suggestions.stream().noneMatch(s -> s.equalsIgnoreCase(currentText))) {
                    suggestions.add(currentText);
                    addToSuggestionsFile(columnName, currentText);
                    autoCompleteDoc.updateSuggestionList(suggestions);
                }
            });
        }
    }

    // Thiết lập tự động hoàn thiện cho bảng
    public void setupTableAutoComplete() {
        for (int col = 1; col <= 13; col++) {
            String columnName = getColumnNameByIndex(col);
            List<String> suggestions = readSuggestionsFromFile(columnName);

            if (!suggestions.isEmpty()) {
                TableColumn column = table.getColumnModel().getColumn(col);
                column.setCellEditor(new DefaultCellEditor(createAutoCompleteTextField(suggestions)) {
                    private JTextField currentTextField;
                    private AutoCompleteDocument autoCompleteDoc;

                    @Override
                    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                        currentTextField = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
                        autoCompleteDoc = new AutoCompleteDocument(currentTextField, suggestions);
                        currentTextField.setDocument(autoCompleteDoc);

                        // Thêm listener để lưu gợi ý mới
                        currentTextField.addActionListener(e -> {
                            String currentText = currentTextField.getText().trim();
                            if (!currentText.isEmpty() && suggestions.stream().noneMatch(s -> s.equalsIgnoreCase(currentText))) {
                                suggestions.add(currentText);
                                addToSuggestionsFile(columnName, currentText);
                                autoCompleteDoc.updateSuggestionList(suggestions);
                            }
                        });

                        // Thêm KeyListener để xử lý các phím điều hướng
                        currentTextField.addKeyListener(new KeyAdapter() {
                            @Override
                            public void keyPressed(KeyEvent e) {
                                JPopupMenu suggestionsPopup = autoCompleteDoc.getSuggestionsPopup();
                                JList<String> suggestionList = autoCompleteDoc.getSuggestionList();

                                if (suggestionsPopup != null && suggestionsPopup.isVisible()) {
                                    switch (e.getKeyCode()) {
                                        case KeyEvent.VK_ENTER:
                                            String selected = suggestionList.getSelectedValue();
                                            if (selected != null) {
                                                currentTextField.setText(selected);
                                                suggestionsPopup.setVisible(false);
                                                stopCellEditing();
                                            }
                                            break;
                                        case KeyEvent.VK_ESCAPE:
                                            suggestionsPopup.setVisible(false);
                                            break;
                                    }
                                }
                            }
                        });
                        return currentTextField;
                    }
                });
            }
        }
    }

    // Tạo JTextField với chức năng tự động hoàn thiện
    private JTextField createAutoCompleteTextField(List<String> suggestions) {
        JTextField textField = new JTextField();
        AutoCompleteDocument autoCompleteDoc = new AutoCompleteDocument(textField, suggestions);
        textField.setDocument(autoCompleteDoc);
        return textField;
    }

    // Lớp nội tại để quản lý tài liệu tự động hoàn thiện
    public static class AutoCompleteDocument extends PlainDocument {
        private final JTextField textField;
        private final List<String> suggestions;
        private final JPopupMenu suggestionsPopup;
        private final JList<String> suggestionList;

        public AutoCompleteDocument(JTextField textField, List<String> suggestions) {
            this.textField = textField;
            this.suggestions = suggestions;

            // Tạo popup menu cho gợi ý
            suggestionsPopup = new JPopupMenu();
            suggestionList = new JList<>();
            suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JScrollPane scrollPane = new JScrollPane(suggestionList);
            suggestionsPopup.add(scrollPane);

            // Thêm listener để chọn gợi ý
            suggestionList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        String selected = suggestionList.getSelectedValue();
                        if (selected != null) {
                            try {
                                remove(0, getLength());
                                insertString(0, selected, null);
                                suggestionsPopup.setVisible(false);
                            } catch (BadLocationException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            });

            // Thêm key listener để xử lý di chuyển trong danh sách gợi ý
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (suggestionsPopup.isVisible()) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_DOWN:
                                navigateSuggestions(1);
                                e.consume();
                                break;
                            case KeyEvent.VK_UP:
                                navigateSuggestions(-1);
                                e.consume();
                                break;
                            case KeyEvent.VK_ENTER:
                                selectCurrentSuggestion();
                                break;
                            case KeyEvent.VK_ESCAPE:
                                suggestionsPopup.setVisible(false);
                                break;
                        }
                    }
                }
            });
        }

        public void updateSuggestionList(List<String> newSuggestions) {
            this.suggestions.clear();
            this.suggestions.addAll(newSuggestions);
        }

        public JPopupMenu getSuggestionsPopup() {
            return suggestionsPopup;
        }

        public JList<String> getSuggestionList() {
            return suggestionList;
        }

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            // Thực hiện việc chèn văn bản
            super.insertString(offs, str, a);

            // Tìm và hiển thị gợi ý
            SwingUtilities.invokeLater(() -> {
                String text = null;
                try {
                    text = getText(0, getLength());
                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }
                updateSuggestions(text);
            });
        }

        @Override
        public void remove(int offs, int len) throws BadLocationException {
            super.remove(offs, len);
            // Tìm và hiển thị gợi ý sau khi xóa
            SwingUtilities.invokeLater(() -> {
                try {
                    String text = getText(0, getLength());
                    updateSuggestions(text);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            });
        }

        private void updateSuggestions(String input) {

            List<String> filteredSuggestions = suggestions.stream()
                    .filter(s -> s.toLowerCase().contains(input.toLowerCase()))
                    .toList();

            suggestionList.setListData(filteredSuggestions.toArray(new String[0]));

            if (!filteredSuggestions.isEmpty() && !input.isEmpty()) {
                suggestionList.setSelectedIndex(0);
                if (textField.isShowing()) {
                    suggestionsPopup.show(textField, 0, textField.getHeight());
                    textField.requestFocusInWindow();
                }
            } else {
                suggestionsPopup.setVisible(false);
            }
        }
        private void navigateSuggestions(int direction) {
            int currentIndex = suggestionList.getSelectedIndex();
            int itemCount = suggestionList.getModel().getSize();

            if (itemCount == 0) return;

            // Di chuyển liên tục trong danh sách
            int newIndex = currentIndex + direction;

            // Điều chỉnh để di chuyển tròn vòng
            if (newIndex < 0) newIndex = itemCount - 1;
            if (newIndex >= itemCount) newIndex = 0;

            suggestionList.setSelectedIndex(newIndex);
            suggestionList.ensureIndexIsVisible(newIndex);
        }

        private void selectCurrentSuggestion() {
            String selected = suggestionList.getSelectedValue();

            if (selected != null) {
                try {
                    remove(0, getLength());
                    insertString(0, selected, null);
                    suggestionsPopup.setVisible(false);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}