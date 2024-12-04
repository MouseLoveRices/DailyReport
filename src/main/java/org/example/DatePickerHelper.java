package org.example;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerHelper {

    public static void showDateInputDialog(JTable table, int col) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JLabel fromLabel = new JLabel("Từ ngày:");
        JLabel toLabel = new JLabel("Đến ngày:");
        JDateChooser fromDateChooser = new JDateChooser();
        JDateChooser toDateChooser = new JDateChooser();

        panel.add(fromLabel);
        panel.add(fromDateChooser);
        panel.add(toLabel);
        panel.add(toDateChooser);

        int result = JOptionPane.showConfirmDialog(null, panel, "Chọn khoảng thời gian", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Date fromDate = fromDateChooser.getDate();
            Date toDate = toDateChooser.getDate();
            if (fromDate == null || toDate == null) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn cả hai ngày!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fromDate);
            int fromDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            calendar.setTime(toDate);
            int toDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            if (fromDayOfWeek != Calendar.MONDAY || toDayOfWeek != Calendar.SATURDAY) {
                JOptionPane.showMessageDialog(null, "Ngày bắt đầu phải là Thứ Hai và ngày kết thúc phải là Thứ Bảy.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (toDate.before(fromDate)) {
                JOptionPane.showMessageDialog(null, "Ngày kết thúc phải sau ngày bắt đầu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (col > 14) {
                int previousWeekEndColumn = col - 4;
                String previousWeekEndDateString = (String) table.getValueAt(0, previousWeekEndColumn);
                if (previousWeekEndDateString != null) {
                    String[] dates = previousWeekEndDateString.split("tới");
                    if (dates.length == 2) {
                        try {
                            Date previousWeekEndDate = new SimpleDateFormat("dd/MM/yyyy").parse(dates[1].trim());
                            if (!fromDate.after(previousWeekEndDate)) {
                                JOptionPane.showMessageDialog(null, "Ngày bắt đầu của tuần mới phải sau ngày kết thúc của tuần trước đó.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            String formattedFromDate = new SimpleDateFormat("dd/MM/yyyy").format(fromDate);
            String formattedToDate = new SimpleDateFormat("dd/MM/yyyy").format(toDate);
            table.setValueAt("Từ " + formattedFromDate + " tới " + formattedToDate, 0, col);
        }
    }

    public static void showSingleDatePicker(JTable table, int row, int col) {
        String weekRange = (String) table.getValueAt(0, col);
        if (weekRange == null || !weekRange.contains("tới")) {
            JOptionPane.showMessageDialog(null, "Hãy chọn khoảng thời gian cho tuần này ở dòng đầu tiên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date weekStart = null;
        Date weekEnd = null;
        try {
            String[] dates = weekRange.split("tới");
            weekStart = sdf.parse(dates[0].trim().replace("Từ ", ""));
            weekEnd = sdf.parse(dates[1].trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi đọc khoảng thời gian từ dòng đầu tiên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDateChooser dateChooser = new JDateChooser();
        try {
            String currentValue = (String) table.getValueAt(row, col);
            if (currentValue != null && !currentValue.isEmpty()) {
                Date currentDate = sdf.parse(currentValue);
                dateChooser.setDate(currentDate);
            }
        } catch (Exception ex) {
            // Ignore parse errors
        }

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("Chọn ngày:"), gbc);
        gbc.gridx = 1;
        panel.add(dateChooser, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(new JLabel(String.format("(Trong khoảng từ %s đến %s)", sdf.format(weekStart), sdf.format(weekEnd))), gbc);

        int result = JOptionPane.showConfirmDialog(null, panel, "Chọn ngày", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Date selectedDate = dateChooser.getDate();
            if (selectedDate == null || selectedDate.before(weekStart) || selectedDate.after(weekEnd)) {
                JOptionPane.showMessageDialog(null, String.format("Vui lòng chọn ngày trong khoảng từ %s đến %s!", sdf.format(weekStart), sdf.format(weekEnd)), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            table.setValueAt(sdf.format(selectedDate), row, col);
        }
    }
}

