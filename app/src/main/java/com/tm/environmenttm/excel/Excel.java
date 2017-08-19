package com.tm.environmenttm.excel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by MY on 8/6/2017.
 */

public class Excel {

    //luu file excel -> du lieu thu thap duoc
    private static boolean saveExcelFile(Context context, File file, List<com.tm.environmenttm.model.Environment> dataModels, ListView listView, Date fromDate, Date toDate) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return false;
        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //Cell style for TITLE static
        CellStyle style = wb.createCellStyle();
//        style.setBorderTop(BorderStyle.DOUBLE);
//        style.setBorderBottom(BorderStyle.THIN);
        style.setFillForegroundColor(HSSFColor.LIME.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setAlignment(CellStyle.ALIGN_CENTER);

        // We also define the font that we are going to use for displaying the
        // data of the cell. We set the font to ARIAL with 20pt in size and
        // make it BOLD and give blue as the color.
        Font font = wb.createFont();
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setFontHeightInPoints((short) 20);
//        font.setBold(true);
        font.setColor(HSSFColor.WHITE.index);
        style.setFont(font);

        //Cell style for info static
        CellStyle csInfo = wb.createCellStyle();
//        csInfo.setFillForegroundColor(HSSFColor.BLUE.index);
//        csInfo.setFillPattern(HSSFCellStyle.ALIGN_LEFT);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("Statictis");
        int len = dataModels.size();
//        sheet1.addMergedRegion(new CellRangeAddress(1,1,4,1));

        /*set info statictis*/
        int index = 0;
        Row rowTitle = sheet1.createRow(index);
        c = rowTitle.createCell(0);
        c.setCellValue("STATICTIS");
        c.setCellStyle(style);

        index += 1;
        Row rowDateFrom = sheet1.createRow(index);
        c = rowDateFrom.createCell(0);
        c.setCellValue("Date From: ");
        c.setCellStyle(csInfo);

        c = rowDateFrom.createCell(1);
//        addCell(c, btnFromDate.getText().toString());
        addCell(c, DateFormat.format("dd/MM/yyyy", fromDate).toString());
        index += 1;
        Row rowDateTo = sheet1.createRow(index);
        c = rowDateTo.createCell(0);
        c.setCellValue("Date To:");
        c.setCellStyle(csInfo);

        c = rowDateTo.createCell(1);
//        addCell(c, btnToDate.getText().toString());
        addCell(c, DateFormat.format("dd/MM/yyyy", toDate).toString());

        index += 1;
        Row rowCount = sheet1.createRow(index);

        c = rowCount.createCell(0);
        c.setCellValue("Record");
        c.setCellStyle(csInfo);

        c = rowCount.createCell(1);
        addCell(c, len);


        // Generate column headings
        index += 1;
        Row row = sheet1.createRow(index);

        c = row.createCell(0);
        c.setCellValue("Datetimme");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Temperature");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Humidity");
        c.setCellStyle(cs);

        c = row.createCell(3);
        c.setCellValue("Pressure");
        c.setCellStyle(cs);

        c = row.createCell(4);
        c.setCellValue("Light");
        c.setCellStyle(cs);

        c = row.createCell(5);
        c.setCellValue("Heat index");
        c.setCellStyle(cs);

        c = row.createCell(6);
        c.setCellValue("Dew point");
        c.setCellStyle(cs);

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (5 * 500));
        sheet1.setColumnWidth(2, (5 * 500));
        sheet1.setColumnWidth(3, (5 * 500));
        sheet1.setColumnWidth(4, (5 * 500));
        sheet1.setColumnWidth(5, (5 * 500));
        sheet1.setColumnWidth(6, (5 * 500));

        /*set data*/
        index += 1;
        int rowNum = index;
        System.out.println("Creating excel");
        for (int i = 0; i < len; i++) {
            sheet1.setColumnWidth(rowNum, (5 * 500));
            Row rowValue = sheet1.createRow(rowNum++);

            int colNum = 0;
            sheet1.setColumnWidth(colNum, (15 * 500));
            Cell cellDate = rowValue.createCell(colNum++);

            cellDate.setCellValue(DateFormat.format("dd/MM/yyyy HH:mm:ss", dataModels.get(i).getDatedCreated()).toString());

            Cell cell = rowValue.createCell(colNum++);
            Object value = dataModels.get(i).getTempC();
            addCell(cell, value);

            cell = rowValue.createCell(colNum++);
            value = dataModels.get(i).getHumidity();
            addCell(cell, value);

            cell = rowValue.createCell(colNum++);
            value = dataModels.get(i).getPressure();
            addCell(cell, value);

            cell = rowValue.createCell(colNum++);
            value = dataModels.get(i).getLightLevel();
            addCell(cell, value);

            cell = rowValue.createCell(colNum++);
            value = dataModels.get(i).getHeatIndex();
            addCell(cell, value);

            cell = rowValue.createCell(colNum++);
            value = dataModels.get(i).getDewPoint();
            addCell(cell, value);
        }

        // Create a path where we will place our List of objects on external storage
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }

        return success;
    }

    //them du lieu vao O DU LIEU
    private static void addCell(Cell cell, Object value) {
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        }
    }

    //doc du lieu file excel
    public static void readExcelFile(Context context, File file) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return;
        }

        try {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
// New Approach
            Uri apkURI = FileProvider.getUriForFile(
                    context,
                    context.getApplicationContext()
                            .getPackageName() + ".provider", file);
            install.setDataAndType(apkURI, "application/vnd.ms-excel");
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
// End New Approach
            context.startActivity(install);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    //xuat file excel , lay thu muc chua' file
    public static File exportFileExcel(Context context, List<com.tm.environmenttm.model.Environment> dataModels
            , ListView listView, Date fromDate, Date toDate) {
        File file = null;
        if (context != null) {
            if (dataModels != null) {
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Statictis" + dataModels.size() + ".xls");
                if (saveExcelFile(context, file, dataModels, listView, fromDate, toDate)) {
                    return file;
                }
            }
        }
        return file;
    }
}
