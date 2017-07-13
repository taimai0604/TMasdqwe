package com.tm.environmenttm.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tm.environmenttm.Home;
import com.tm.environmenttm.R;
import com.tm.environmenttm.adapter.CustomListLocationAdapter;
import com.tm.environmenttm.adapter.CustomListThingspeakAdapter;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.constant.ConstantValue;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.ChartThingspeak;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.RealmTM;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThingspeakFragment extends Fragment {
    private Device device;
    private List<ChartThingspeak> dataModels;
    private ListView listView;
    private CustomListThingspeakAdapter adapter;

    public ThingspeakFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ConstantFunction.changeTitleBar(getActivity(), ConstantValue.TITLE_THINGSPEAK);
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_thingspeak, container, false);
        device = (Device) getArguments().getSerializable(ConstantValue.DEVICE);
        listView = (ListView) view.findViewById(R.id.lvThingspeak);

        loadThingspeak(device.getId());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new ThingspeakFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(ConstantValue.DEVICE, device);
                bundle.putBoolean("active",
                        (device.isActive()));
                fragment.setArguments(bundle);
                ConstantFunction.replaceFragmentHasBackStack(getFragmentManager(), R.id.frgContent, fragment, ConstantValue.FRG_THINGSPEAK);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_device, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_device) {
            Fragment fragment = new AddDeivceFragment();
            ConstantFunction.replaceFragmentHasBackStack(getFragmentManager(), R.id.frgContent, fragment, ConstantValue.FRG_ADD_DEVICE);
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadThingspeak(String deviceId) {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<List<ChartThingspeak>> call = iServices.getAllChartThingspeak(deviceId,"");
        final MaterialDialog dialog = ConstantFunction.showProgressHorizontalIndeterminateDialog(getContext());
        call.enqueue(new Callback<List<ChartThingspeak>>() {
            @Override
            public void onResponse(Call<List<ChartThingspeak>> call, Response<List<ChartThingspeak>> response) {
                if (response.code() == 200) {
                    dataModels = response.body();
                    adapter = new CustomListThingspeakAdapter(getContext(), dataModels);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                } else {
                    ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<ChartThingspeak>> call, Throwable t) {
                ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                dialog.dismiss();
            }
        });
    }
}
