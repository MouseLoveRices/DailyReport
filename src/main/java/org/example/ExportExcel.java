package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportExcel {
    public static void exportToExcel(JTable table) {
        // Mở hộp thoại chọn file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu file Excel");
        fileChooser.setSelectedFile(new File("TableData.xlsx")); // Đặt tên file mặc định

        // Chỉ cho phép lưu file
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Đảm bảo file có đuôi .xlsx
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".xlsx")) {
                fileToSave = new File(filePath + ".xlsx");
            }

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Table Data");

            // Ghi tiêu đề cột
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < table.getColumnCount(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(table.getColumnName(col));

                // Áp dụng style tiêu đề
                CellStyle headerStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                headerStyle.setFont(font);

                // Thêm viền cho tiêu đề
                headerStyle.setBorderTop(BorderStyle.THIN);
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);

                cell.setCellStyle(headerStyle);
            }

            // Ghi dữ liệu
            for (int row = 0; row < table.getRowCount(); row++) {
                Row dataRow = sheet.createRow(row + 1);
                for (int col = 0; col < table.getColumnCount(); col++) {
                    Cell cell = dataRow.createCell(col);
                    Object value = table.getValueAt(row, col);
                    cell.setCellValue(value == null ? "" : value.toString());

                    // Áp dụng màu sắc và viền
                    CellStyle cellStyle = workbook.createCellStyle();

                    // Thêm viền cho tất cả các ô
                    cellStyle.setBorderTop(BorderStyle.THIN);
                    cellStyle.setBorderBottom(BorderStyle.THIN);
                    cellStyle.setBorderLeft(BorderStyle.THIN);
                    cellStyle.setBorderRight(BorderStyle.THIN);

                    // Giữ nguyên logic màu sắc cũ
                    if (row == 1) { // Dòng thứ 2
                        if (col >= 0 && col < 14) {
                            cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                        } else if ((col - 14) % 4 == 0) { // Cột 14, 18, 22, ...
                            cellStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
                        } else if ((col - 14) % 4 == 1) { // Cột 15, 19, 23, ...
                            cellStyle.setFillForegroundColor(IndexedColors.TURQUOISE.getIndex());
                        } else if ((col - 14) % 4 == 2) { // Cột 16, 20, 24, ...
                            cellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
                        } else if ((col - 14) % 4 == 3) { // Cột 17, 21, 25, ...
                            cellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
                        }
                        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    } else if (row == 0 && col >= 14) { // Dòng đầu tiên, từ cột 14 trở đi
                        cellStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
                        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        cellStyle.setAlignment(HorizontalAlignment.CENTER);
                    }
                    cell.setCellStyle(cellStyle);
                }
            }

            // Merge ô dòng 1 cho các tuần và thêm viền
            for (int startCol = 14; startCol < table.getColumnCount(); startCol += 4) {
                int endCol = Math.min(startCol + 3, table.getColumnCount() - 1);
                if (startCol < endCol) {
                    // Merge các ô
                    sheet.addMergedRegion(new CellRangeAddress(1, 1, startCol, endCol));

                    // Thêm màu nền và viền cho tất cả các ô trong vùng merge
                    CellStyle mergedStyle = workbook.createCellStyle();
                    mergedStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
                    mergedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    mergedStyle.setAlignment(HorizontalAlignment.CENTER);

                    // Thêm viền cho ô merge
                    mergedStyle.setBorderTop(BorderStyle.THIN);
                    mergedStyle.setBorderBottom(BorderStyle.THIN);
                    mergedStyle.setBorderLeft(BorderStyle.THIN);
                    mergedStyle.setBorderRight(BorderStyle.THIN);

                    for (int col = startCol; col <= endCol; col++) {
                        Cell cell = sheet.getRow(1).getCell(col);
                        if (cell == null) {
                            cell = sheet.getRow(1).createCell(col);
                        }
                        cell.setCellStyle(mergedStyle);
                    }
                }
            }

            // Ghi dữ liệu ra file
            try (FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
                workbook.write(fileOut);
                JOptionPane.showMessageDialog(null, "Xuất file thành công: " + fileToSave.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Lỗi khi xuất file: " + ex.getMessage());
            } finally {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
