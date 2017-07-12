package com.tm.environmenttm.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tm.environmenttm.R;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoDeviceFragment extends Fragment {
    private TextView tvNameDevice;
    private TextView tvType;
    private TextView tvDeviceId;
    private TextView tvLocation;
    private TextView tvKeyThingspeak;

    private Device device;
    private boolean mAlreadyLoaded = false;

    public InfoDeviceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_device, container, false);
        setHasOptionsMenu(true);

        tvNameDevice = (TextView) view.findViewById(R.id.edNameDevice);
        tvType = (TextView) view.findViewById(R.id.tvType);
        tvDeviceId = (TextView) view.findViewById(R.id.edDeviceId);
        tvLocation = (TextView) view.findViewById(R.id.edLocation);
        tvKeyThingspeak = (TextView) view.findViewById(R.id.edKeyThingspeak);

        device = (Device) getArguments().getSerializable("device");

        tvNameDevice.setText(device.getNameDevice());

        tvDeviceId.setText(device.getDeviceId());
        tvLocation.setText(device.getLocation());
        tvKeyThingspeak.setText(device.getKeyThingspeak());

        loadTypeForDevice(device.getTypeId());
        return view;
    }

    private void loadTypeForDevice(String typeId) {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<Type> call = iServices.getTypeById(typeId);
        call.enqueue(new Callback<Type>() {
            @Override
            public void onResponse(Call<Type> call, Response<Type> response) {
                if (response.code() == 200) {
                    tvType.setText(response.body().getNameType());
                } else {
                    ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                }
            }

            @Override
            public void onFailure(Call<Type> call, Throwable t) {
                ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
            }

        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_info_device, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            Fragment fragment = new EditDeviceFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("device", device);
            bundle.putBoolean("active",
                    (device.isActive()));
            fragment.setArguments(bundle);
            ConstantFunction.replaceFragmentHasBackStack(getFragmentManager(), R.id.frgContent, fragment, "edit_device");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
