package com.tm.environmenttm.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.tm.environmenttm.LoginActivity;
import com.tm.environmenttm.R;
import com.tm.environmenttm.config.ConfigApp;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantValue;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.PubnubTM;
import com.tm.environmenttm.model.RealmTM;

import java.util.Arrays;
import java.util.Calendar;

import io.realm.Realm;

public class SettingFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    Calendar c = Calendar.getInstance();

    private View rootView;

    SwitchButton sbNotificationTemp;

    private String TAG = this.getClass().getName();
    private ConfigApp configApp;
    private Realm realm;
    private Device device;

    private PubNub pubnub;

    private final Context context;

    public SettingFragment(Context context){
        this.context = context;
    }

    @Override
    @TargetApi(21)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ConstantFunction.changeTitleBar(getActivity(), ConstantValue.TITLE_SETTING);
        configApp = (ConfigApp) RealmTM.INSTANT.findFirst(ConfigApp.class);

        initPubnub();

        realm = Realm.getDefaultInstance();
        device = (Device) RealmTM.INSTANT.findFirst(Device.class);

        rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        sbNotificationTemp = (SwitchButton) rootView.findViewById(R.id.sbNotificationTemp);
        sbNotificationTemp.setChecked(configApp.isNotificationTemp());

        sbNotificationTemp.setOnCheckedChangeListener(this);

        return rootView;
    }

    private void initPubnub() {
        pubnub = PubnubTM.INSTANT.initPubnub();
        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {


                if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                } else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                    if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                        pubnub.publish().channel("hello").message("hello --- !!").async(new PNCallback<PNPublishResult>() {
                            @Override
                            public void onResponse(PNPublishResult result, PNStatus status) {
                                if (!status.isError()) {
                                } else {
                                }
                            }
                        });
                    }
                } else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {
                } else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                if (message.getChannel() != null) {
                    switch (message.getChannel()) {
                        case "hello":
                        case ConstantValue.CHANNEL_NOTIFICATION_TEMP:
                            if (device != null) {
                                Log.d(TAG, "message: " + message.getMessage());
                                Task task = new Task(context);
                                task.execute(message.getMessage().toString());

                            }
                            break;

                    }
                }
            }


            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        realm.beginTransaction();
        configApp.setNotificationTemp(isChecked);
        realm.commitTransaction();
        // sub pubnub
        if (isChecked) {

            PubnubTM.INSTANT.subChannel("hello");
        } else {
            PubnubTM.INSTANT.subChannel("hello");
        }
    }

    class Task extends AsyncTask<String, Void, String> {
        public Context context;

        public Task(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {
            String t = params[0];
            return t;
        }
    }
}
