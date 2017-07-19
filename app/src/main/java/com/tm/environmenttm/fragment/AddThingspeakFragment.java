package com.tm.environmenttm.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tm.environmenttm.R;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.constant.ConstantValue;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.ChartThingspeak;
import com.tm.environmenttm.model.Device;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddThingspeakFragment extends Fragment {
    private EditText edName;
    private EditText edContent;
    private EditText edDecription;
    private Device device;

    public AddThingspeakFragment(Device device) {
        this.device = device;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_add_thingspeak, container, false);
        edName = (EditText) view.findViewById(R.id.edNameThingSpeak);
        edContent = (EditText) view.findViewById(R.id.edContentThingspeak);
        edDecription = (EditText) view.findViewById(R.id.edDecription);


        edName.setText("");
        edName.forceLayout();

        edContent.setText("");
        edDecription.setText("");
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_edit_device, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            ChartThingspeak chartThingspeak = new ChartThingspeak();

            chartThingspeak.setName(edName.getText().toString());
            chartThingspeak.setContent(edContent.getText().toString());
            chartThingspeak.setDescription(edDecription.getText().toString());

            chartThingspeak.setActive(true);
            chartThingspeak.setDeviceId(device.getId());

            saveChange(chartThingspeak);
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveChange(ChartThingspeak chartThingspeak) {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<ChartThingspeak> call = iServices.addChartThingspeak(chartThingspeak);
        // Set up progress before call
        final MaterialDialog dialog = ConstantFunction.showProgressHorizontalIndeterminateDialog(getContext());
        // show it
        call.enqueue(new Callback<ChartThingspeak>() {
            @Override
            public void onResponse(Call<ChartThingspeak> call, Response<ChartThingspeak> response) {
                if (getContext() != null) {
                    if (response.body() != null) {
                        ConstantFunction.popBackStack(getFragmentManager());
                    } else {
                        ConstantFunction.showToast(getContext(), "Fail!");
                    }
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ChartThingspeak> call, Throwable t) {
                ConstantFunction.showToast(getContext(), "Fail!");
                dialog.dismiss();
            }
        });
    }
}
