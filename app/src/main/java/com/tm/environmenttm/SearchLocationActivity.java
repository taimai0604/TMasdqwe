package com.tm.environmenttm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.tm.environmenttm.AsyncTask.DemoAsyncTask;
import com.tm.environmenttm.CustomModel.CustomCallbackPubnub;
import com.tm.environmenttm.Services.ServiceCallNotification;
import com.tm.environmenttm.adapter.CustomListLocationAdapter;
import com.tm.environmenttm.config.ConfigApp;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.constant.ConstantValue;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.PubnubTM;
import com.tm.environmenttm.model.RealmTM;
import com.tm.environmenttm.model.ResponeNumber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchLocationActivity extends AppCompatActivity {

    private MaterialSearchView searchView;
    private List<Device> dataModels;
    private ListView listView;

    private Context context = this;
    private ConfigApp configApp;

    private Device oldDevice;

    private CustomListLocationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        drawUI();

        listView = (ListView) findViewById(R.id.lv_location);
        loadDevices("");

        adapter = new CustomListLocationAdapter(getApplicationContext(), dataModels);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public static final String TAG = "test thread";

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                dang test --------------------------------------
                oldDevice = (Device) RealmTM.INSTANT.findFirst(Device.class);
                if(oldDevice != null) {
                }

                Device device = dataModels.get(position);
                //remove
                RealmTM.INSTANT.deleteAll(Device.class);
                //add new
                RealmTM.INSTANT.addRealm(device);

                configApp = (ConfigApp) RealmTM.INSTANT.findFirst(ConfigApp.class);

                DemoAsyncTask asyncTask = new DemoAsyncTask(getApplicationContext());
                asyncTask.execute(device);
                try {
                    ConfigApp configAppRespone = asyncTask.get();
                    RealmTM.INSTANT.realm.beginTransaction();
                    configApp.setLowerTemp(configAppRespone.getUpperTemp());
                    configApp.setUpperTemp(configAppRespone.getLowerTemp());
                    configApp.setNotificationTemp(false);
                    RealmTM.INSTANT.realm.commitTransaction();

                    //un sub pubnub
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                setResult(Activity.RESULT_OK);
                Home.refresh = true;

                finish();


            }
        });
    }




    public void loadDevices(String nameLocation) {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<List<Device>> call = iServices.getDeviceByLocation("{\"where\":{\"location\":{\"like\":\"" + nameLocation + "\"},\"active\":true}}");
        dataModels = new ArrayList<>();
        call.enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                dataModels = response.body();
                adapter = new CustomListLocationAdapter(getApplicationContext(), dataModels);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void drawUI() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(ConstantValue.TITLE_SEARCH_LOCATION);
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setHint(getResources().getString(R.string.search));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ConstantFunction.showToast(getApplicationContext(), query);
                toolbar.setTitle(query);
                loadDevices(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

    }


}
