package com.tm.environmenttm;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.tm.environmenttm.adapter.CustomListViewDataAdapter;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.Environment;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data_detail);
        device = (Device) getIntent().getSerializableExtra("device");
        ConstantFunction.changeTitleBar(this, device.getLocation());


        listView = (ListView) findViewById(R.id.lv_view_data);

        Calendar c = Calendar.getInstance();
        fYear = c.get(Calendar.YEAR);
        fMonth = c.get(Calendar.MONTH);
        fDay = c.get(Calendar.DAY_OF_MONTH);
        Date fromDate = c.getTime();

        c.add(Calendar.DAY_OF_MONTH, 1);
        tYear = c.get(Calendar.YEAR);
        tMonth = c.get(Calendar.MONTH);
        tDay = c.get(Calendar.DAY_OF_MONTH);
        Date toDate = c.getTime();
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
            if(datePickerDialogToDate == null) {
                datePickerDialogToDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        btnToDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        tYear = year;
                        tMonth = month;
                        tDay = dayOfMonth;
                    }
                }, tYear, tMonth, tDay);
            }else{
                datePickerDialogToDate.updateDate(tYear, tMonth, tDay);
            }
        }
    }

    private void loadData(Date fromDate, Date toDate, int size) {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        if(positioinSelected == (items.length - 1)){
            query = "{\"order\":  \"datedCreated DESC\",\"skip\":0,\"where\":{\"datedCreated\": {\"between\":[\"" + DateFormat.format("yyyy-MM-dd", fromDate).toString() + "T00:00:00.000Z\",\"" + DateFormat.format("yyyy-MM-dd", toDate).toString() + "T00:00:00.000\"]}}}";
        }else {
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
                ConstantFunction.showToast(getApplicationContext(),dataModels.size()+"");
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
                                Date fromDate = c.getTime();
                                c.set(tYear, tMonth, tDay);
                                Date toDate = c.getTime();

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
                        if(selected.equals("All")){

                        }else{
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
