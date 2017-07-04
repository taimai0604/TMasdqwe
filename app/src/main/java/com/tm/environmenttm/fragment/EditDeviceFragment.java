package com.tm.environmenttm.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tm.environmenttm.R;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.RealmTM;
import com.tm.environmenttm.model.ResponeBoolean;
import com.tm.environmenttm.model.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class EditDeviceFragment extends Fragment {
    private EditText edNameDevice;
    private TextView tvType;
    private EditText edDeviceId;
    private EditText edLocation;
    private EditText edKeyThingspeak;
    Device device;

    public EditDeviceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_device, container, false);

        setHasOptionsMenu(true);

        edNameDevice = (EditText) view.findViewById(R.id.edNameDevice);
        tvType = (TextView) view.findViewById(R.id.tvType);
        edDeviceId = (EditText) view.findViewById(R.id.edDeviceId);
        edLocation = (EditText) view.findViewById(R.id.edLocation);
        edKeyThingspeak = (EditText) view.findViewById(R.id.edKeyThingspeak);

        device = (Device) getArguments().getSerializable("device");

        edNameDevice.setText(device.getNameDevice());
        edDeviceId.setText(device.getDeviceId());
        edLocation.setText(device.getLocation());
        edKeyThingspeak.setText(device.getKeyThingspeak());

       loadTypeForDevice(device.getTypeId());

        tvType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Type> list = RealmTM.INSTANT.findAll(Type.class);
                List<String> strings = new ArrayList<>();
                for (Type type : list) {
                    strings.add(type.getNameType());
                }
                new MaterialDialog.Builder(getContext())
                        .title(getContext().getString(R.string.type_device))
                        .items(strings.toArray(new String[0]))
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                tvType.setText(text.toString());
                            }
                        })
                        .show();
            }
        });
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
            device.setNameDevice(edNameDevice.getText().toString());
            device.setTypeId(getTypeId(tvType.getText().toString()));
            device.setDeviceId(edDeviceId.getText().toString());

            // dua vao ten de thay doi thong so vi tri
            device.setLocation(edLocation.getText().toString());
            device.setKeyThingspeak(edKeyThingspeak.getText().toString());

            //update len server
            saveChangeDevice(device);
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveChangeDevice(Device device) {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<ResponeBoolean> call = iServices.editDevice(device, device.getId());
        call.enqueue(new Callback<ResponeBoolean>() {
            @Override
            public void onResponse(Call<ResponeBoolean> call, Response<ResponeBoolean> response) {
                if (response.code() == 200) {
                    ConstantFunction.popBackStack(getFragmentManager());
                } else {
                    ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                }
            }

            @Override
            public void onFailure(Call<ResponeBoolean> call, Throwable t) {
                ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
            }

        });
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

    private String getTypeId(String nameType) {
        List<Type> list = RealmTM.INSTANT.findAll(Type.class);
        for (Type type : list) {
            if (nameType.equals(type.getNameType())) {
                return type.getId();
            }
        }
        return null;
    }
}