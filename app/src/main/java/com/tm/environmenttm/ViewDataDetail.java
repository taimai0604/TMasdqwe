package com.tm.environmenttm;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.tm.environmenttm.adapter.CustomListViewDataAdapter;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.excel.Excel;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.Environment;
import com.tm.environmenttm.model.RealmTM;
import com.tm.environmenttm.model.ResponeBoolean;

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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

public class ViewDataDetail extends AppCompatActivity implements View.OnClickListener {
    private Device device;

    private Button btnFromDate;
    private Button btnToDate;
    private ListView listView;
    private CustomListViewDataAdapter adapter;
    private List<Environment> dataModels;
    private Spinner spinnerSize;
    private String[] items = new String[]{"50", "100", "500", "All"};

    private DatePickerDialog datePickerDialogFromDate = null;
    private DatePickerDialog datePickerDialogToDate = null;

    private final int DEFAULT_SIZE = 50;
    private int size_data = DEFAULT_SIZE;
    private int positioinSelected = 0;

    private String query = "";

    private int fYear, fMonth, fDay;
    private int tYear, tMonth, tDay;

    private Context context;

    private Date fromDate;
    private Date toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_view_data_detail);
        device = (Device) getIntent().getSerializableExtra("device");
        ConstantFunction.changeTitleBar(this, device.getLocation());

        listView = (ListView) findViewById(R.id.lv_view_data);
        context = getApplicationContext();

        Calendar c = Calendar.getInstance();
        fYear = c.get(Calendar.YEAR);
        fMonth = c.get(Calendar.MONTH);
        fDay = c.get(Calendar.DAY_OF_MONTH);
        fromDate = c.getTime();

        c.add(Calendar.DAY_OF_MONTH, 1);
        tYear = c.get(Calendar.YEAR);
        tMonth = c.get(Calendar.MONTH);
        tDay = c.get(Calendar.DAY_OF_MONTH);
        toDate = c.getTime();
        loadData(fromDate, toDate, DEFAULT_SIZE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            if (datePickerDialogFromDate == null) {
                datePickerDialogFromDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        btnFromDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        fYear = year;
                        fMonth = month;
                        fDay = dayOfMonth;
                    }
                }, fYear, fMonth, fDay);
            } else {
                datePickerDialogFromDate.updateDate(fYear, fMonth, fDay);
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            if (datePickerDialogToDate == null) {
                datePickerDialogToDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        btnToDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        tYear = year;
                        tMonth = month;
                        tDay = dayOfMonth;
                    }
                }, tYear, tMonth, tDay);
            } else {
                datePickerDialogToDate.updateDate(tYear, tMonth, tDay);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadData(Date fromDate, Date toDate, int size) {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        if (positioinSelected == (items.length - 1)) {
            query = "{\"order\":  \"datedCreated DESC\",\"skip\":0,\"where\":{\"datedCreated\": {\"between\":[\"" + DateFormat.format("yyyy-MM-dd", fromDate).toString() + "T00:00:00.000Z\",\"" + DateFormat.format("yyyy-MM-dd", toDate).toString() + "T00:00:00.000\"]}}}";
        } else {
            query = "{\"limit\":" + size + ", \"order\":  \"datedCreated DESC\",\"skip\":0,\"where\":{\"datedCreated\": {\"between\":[\"" + DateFormat.format("yyyy-MM-dd", fromDate).toString() + "T00:00:00.000Z\",\"" + DateFormat.format("yyyy-MM-dd", toDate).toString() + "T00:00:00.000\"]}}}";
        }
        Call<List<Environment>> call = iServices.getInfoEnvironmentByDevice(device.getId(), query);
        final MaterialDialog dialog = ConstantFunction.showProgressHorizontalIndeterminateDialog(this);
        call.enqueue(new Callback<List<Environment>>() {

            @Override
            public void onResponse(Call<List<Environment>> call, Response<List<Environment>> response) {
                dataModels = response.body();
                adapter = new CustomListViewDataAdapter(getApplicationContext(), dataModels);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
                dialog.dismiss();
                ConstantFunction.showToast(getApplicationContext(), dataModels.size() + "");
            }

            @Override
            public void onFailure(Call<List<Environment>> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_data_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.export_excel:
                final File fileExcel = Excel.exportFileExcel(getApplicationContext(), dataModels, listView, fromDate, toDate);
                if (fileExcel != null) {
                    new MaterialDialog.Builder(this)
                            .title(getResources().getString(R.string.info_export_excel))
                            .content(getResources().getString(R.string.info_export_excel_success) + " " + fileExcel.getPath() + " ?")
                            .positiveText(R.string.agree)
                            .negativeText(R.string.disagree)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    //doc du lieu file excel
                                    Excel.readExcelFile(getApplicationContext(), fileExcel);
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    // TODO
                                }
                            })
                            .show();
                } else {
                    new MaterialDialog.Builder(this)
                            .title(getResources().getString(R.string.info_export_excel))
                            .content(R.string.info_export_excel_fail)
                            .positiveText(R.string.agree)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                }
                            })
                            .show();
                }
                break;

            case R.id.action_filter:
                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title("Filter")
                        .customView(R.layout.dialog_filter_data, true)
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Calendar c = Calendar.getInstance();
                                c.set(fYear, fMonth, fDay);
                                fromDate = c.getTime();
                                c.set(tYear, tMonth, tDay);
                                toDate = c.getTime();

                                loadData(fromDate, toDate, size_data);

                                datePickerDialogFromDate.updateDate(fYear, fMonth, fDay);
                                datePickerDialogToDate.updateDate(tYear, tMonth, tDay);

                                btnFromDate.setText(fDay + "/" + (fMonth + 1) + "/" + fYear);
                                btnToDate.setText(tDay + "/" + (tMonth + 1) + "/" + tYear);
                            }
                        })
                        .show();
                btnFromDate = (Button) dialog.getCustomView().findViewById(R.id.btnFromDate);
                btnToDate = (Button) dialog.getCustomView().findViewById(R.id.btnToDate);
                spinnerSize = (Spinner) dialog.getCustomView().findViewById(R.id.spinnerSize);


                btnFromDate.setText(fDay + "/" + (fMonth + 1) + "/" + fYear);

                btnToDate.setText(tDay + "/" + (tMonth + 1) + "/" + tYear);

                //init spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
                spinnerSize.setAdapter(adapter);
                spinnerSize.setSelection(positioinSelected);
                spinnerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        String selected = items[position];
                        if (selected.equals("All")) {

                        } else {
                            size_data = Integer.valueOf(selected);
                        }
                        positioinSelected = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                btnFromDate.setOnClickListener(this);
                btnToDate.setOnClickListener(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // Get Current Date

        switch (v.getId()) {
            case R.id.btnFromDate:
                datePickerDialogFromDate.show();
                break;
            case R.id.btnToDate:
                datePickerDialogToDate.show();
                break;

        }
    }

}
