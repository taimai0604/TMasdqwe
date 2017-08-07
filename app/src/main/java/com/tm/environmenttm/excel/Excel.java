package com.tm.environmenttm.excel;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.tm.environmenttm.model.Device;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.AccessControlContext;
import java.util.Iterator;
import java.util.List;

/**
 * Created by MY on 8/6/2017.
 */

public class Excel {

//    private  Context context;
//    public Excel(Context context){
//        this.context = context;
//    }

    private static boolean saveFile(Context context, String fileName) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return false;
        }

        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName);
        PrintStream p = null; // declare a print stream object
        boolean success = false;

        try {
            OutputStream os = new FileOutputStream(file);
            // Connect print stream to the output stream
            p = new PrintStream(os);
            p.println("This is a TEST");
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != p)
                    p.close();
            } catch (Exception ex) {
            }
        }

        return success;
    }

    private static void readFile(Context context, String filename) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return;
        }

        FileInputStream fis = null;

        try {
            File file = new File(context.getExternalFilesDir(null), filename);
            fis = new FileInputStream(file);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                Log.w("FileUtils", "File data: " + strLine);
                Toast.makeText(context, "File Data: " + strLine, Toast.LENGTH_SHORT).show();
            }
            in.close();
        } catch (Exception ex) {
            Log.e("FileUtils", "failed to load file", ex);
        } finally {
            try {
                if (null != fis) fis.close();
            } catch (IOException ex) {
            }
        }

        return;
    }

    private static boolean saveExcelFile(Context context, File file, List<com.tm.environmenttm.model.Environment> dataModels, ListView listView) {

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

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("Statictis");

        // Generate column headings
        Row row = sheet1.createRow(0);

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

        int rowNum = 1;
        System.out.println("Creating excel");
        int len = dataModels.size();
        for (int i = 0; i < len; i++) {
            sheet1.setColumnWidth(rowNum, (5 * 500));
            Row rowValue = sheet1.createRow(rowNum++);

            int colNum = 0;
            sheet1.setColumnWidth(colNum, (15 * 500));
            Cell cellDate = rowValue.createCell(colNum++);

            c.setCellStyle(cs);
            cellDate.setCellValue(DateFormat.format("dd/MM/yyyy HH:mm:ss", dataModels.get(i).getDatedCreated()).toString());

            Cell cell = rowValue.createCell(colNum++);
            c.setCellStyle(cs);
            Object value = dataModels.get(i).getTempC();
            addCell(cell, value);

            cell = rowValue.createCell(colNum++);
            c.setCellStyle(cs);
            value = dataModels.get(i).getHumidity();
            addCell(cell, value);

            cell = rowValue.createCell(colNum++);
            c.setCellStyle(cs);
            value = dataModels.get(i).getPressure();
            addCell(cell, value);

            cell = rowValue.createCell(colNum++);
            c.setCellStyle(cs);
            value = dataModels.get(i).getLightLevel();
            addCell(cell, value);

            cell = rowValue.createCell(colNum++);
            c.setCellStyle(cs);
            value = dataModels.get(i).getHeatIndex();
            addCell(cell, value);

            cell = rowValue.createCell(colNum++);
            c.setCellStyle(cs);
            value = dataModels.get(i).getDewPoint();
            addCell(cell, value);
        }

        // Create a path where we will place our List of objects on external storage
        // File file = new File(context.getExternalFilesDir(null), fileName);
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

    private static void addCell(Cell cell, Object value) {
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        }
    }

    public static void openExcelFile(Context context, File file) {
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return;
        }
    }

    public static void readExcelFile(Context context, File file) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return;
        }

        try {
            // Creating Input Stream
//            File file = new File(context.getExternalFilesDir(null), filename);
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator<Row> rowIter = mySheet.rowIterator();

            while (rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator<Cell> cellIter = myRow.cellIterator();
                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Log.w("FileUtils", "Cell Value: " + myCell.toString());
                    Toast.makeText(context, "cell Value: " + myCell.toString(), Toast.LENGTH_SHORT).show();
                }
            }
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

    public static File exportFileExcel(Context context, List<com.tm.environmenttm.model.Environment> dataModels, ListView listView) {
        boolean check = false;
        File file = null;
        if (context != null) {
            if (dataModels != null) {
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Statictis" + dataModels.size() + ".xls");
                if (saveExcelFile(context, file, dataModels, listView)) {
                    return file;
                }
            }
        }
        return file;
    }
}
