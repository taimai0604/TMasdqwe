package com.tm.environmenttm.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tm.environmenttm.R;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.map.DataParserLocation;
import com.tm.environmenttm.map.TestMapFragment;
import com.tm.environmenttm.model.AddressGeo;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.RealmTM;
import com.tm.environmenttm.model.ResponeBoolean;
import com.tm.environmenttm.model.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class EditDeviceFragment extends Fragment {
    private EditText edNameDevice;
    private TextView tvType;
    private EditText edDeviceId;
    private EditText edLocation;
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

        device = (Device) getArguments().getSerializable("device");

        edNameDevice.setText(device.getNameDevice());
        edDeviceId.setText(device.getDeviceId());
        edLocation.setText(device.getLocation());

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
            String address = edLocation.getText().toString();
            if (!address.isEmpty()) {
                device.setNameDevice(edNameDevice.getText().toString());
                device.setTypeId(getTypeId(tvType.getText().toString()));
                device.setDeviceId(edDeviceId.getText().toString());
                String url = TestMapFragment.getUrl(address);
                FetchUrlGeo FetchUrl = new FetchUrlGeo();
                FetchUrl.execute(url);
            } else {
                edLocation.requestFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edLocation, InputMethodManager.SHOW_IMPLICIT);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // Fetches data from url passed
    private class FetchUrlGeo extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = TestMapFragment.downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            List<HashMap<String, String>> addressPlacesList = null;
            DataParserLocation dataParser = new DataParserLocation();
            addressPlacesList = dataParser.parse(result);
            AddressGeo addressGeo = TestMapFragment.ShowInfoPlacesAddress(addressPlacesList);
            if (addressGeo != null) {
                device.setLocation(addressGeo.getAddress());
                device.setLatitude(addressGeo.getLa());
                device.setLongitude(addressGeo.getLn());
            }
            Log.d(TAG, "onOptionsItemSelected: add " + addressGeo);
            //update len server
            saveChangeDevice(device);

            Log.d("GooglePlacesReadTask", "onPostExecute Exit");
        }
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
