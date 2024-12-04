package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CustomCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Tô màu hàng thứ 2
        if (row == 1) {
            if (column >= 0 && column < 14) {
                cell.setBackground(Color.YELLOW);
            } else if ((column - 14) % 4 == 0) { // Cột 14, 18, 22, ...
                cell.setBackground(Color.decode("#bae5fd"));
            } else if ((column - 14) % 4 == 1) { // Cột 15, 19, 23, ...
                cell.setBackground(Color.decode("#7dd1fc"));
            } else if ((column - 14) % 4 == 2) { // Cột 16, 20, 24, ...
                cell.setBackground(Color.decode("#38baf8"));
            } else if ((column - 14) % 4 == 3) { // Cột 17, 21, 25, ...
                cell.setBackground(Color.decode("#0281c7"));
            }
        } else if (row == 0 && column >= 14) {
            // Màu nền hàng đầu tiên từ cột 14 trở đi
            cell.setBackground(Color.decode("#f2dc95"));
            // Căn giữa text
            setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            // Đặt màu mặc định cho các hàng khác
            cell.setBackground(Color.WHITE);
        }
        return cell;
    }
}
