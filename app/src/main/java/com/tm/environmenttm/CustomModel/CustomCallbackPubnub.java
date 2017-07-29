package com.tm.environmenttm.CustomModel;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.tm.environmenttm.LoginActivity;
import com.tm.environmenttm.R;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantValue;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.RealmTM;

import java.util.Date;

/**
 * Created by taima on 07/09/2017.
 */

public class CustomCallbackPubnub extends SubscribeCallback {
    private final String TAG = this.getClass().getName();
    private String deviceId;
    private String location;
    private Context context;
    private int notify_no;
    private final int MAX_NOTIFY = 9;

    public CustomCallbackPubnub(Context context, String deviceId, String location) {
        this.context = context;
        this.deviceId = deviceId;
        this.location = location;
        this.notify_no = 0;
    }

    @Override
    public void status(PubNub pubnub, PNStatus status) {


        if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
        } else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
            if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
            }
        } else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {
        } else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {
        }
    }

    @Override
    public void message(PubNub pubnub, PNMessageResult message) {
        if (message.getChannel() != null) {
            if (deviceId != null) {
                String channel = message.getChannel();
                if (channel.equals(ConstantValue.CHANNEL_NOTIFICATION_TEMP + "-" + deviceId)) {
                    Task task = new Task(context);
                    task.execute(message.getMessage().toString());
                }
            }
        }
    }


    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {

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
            String result = s.replaceAll("\"","") + " - " + DateFormat.format("dd/MM/yyyy hh:mm:ss a", new Date()).toString();

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, LoginActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notification =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_logo)
                            .setContentTitle(location)
                            .setContentText(result)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setContentIntent(pendingIntent).setAutoCancel(true);

            NotificationManager notificationManager;
            notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            if (notify_no < MAX_NOTIFY) {
                notify_no = notify_no + 1;
            } else {
                notify_no = 0;
            }
            notificationManager.notify(notify_no, notification.build());
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
