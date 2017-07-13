package com.tm.environmenttm.model;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.endpoints.presence.HereNow;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.tm.environmenttm.CustomModel.CustomCallbackPubnub;
import com.tm.environmenttm.LoginActivity;
import com.tm.environmenttm.R;
import com.tm.environmenttm.config.ConfigApp;
import com.tm.environmenttm.constant.ConstantValue;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by taima on 07/08/2017.
 */

public class PubnubTM {
    public static PubnubTM INSTANT = new PubnubTM();
    private CustomCallbackPubnub customCallbackPubnub;

    private PubnubTM() {
        customCallbackPubnub = null;
    }

    private PNConfiguration pnConfiguration = new PNConfiguration();
    private PubNub pubnub;

    public void initPubnub(Context context, Device device) {
        if (pubnub == null) {
            pnConfiguration.setSubscribeKey(ConstantValue.SUB_PUBNUB);
            pnConfiguration.setPublishKey(ConstantValue.PUB_PUBNUB);
            pubnub = new PubNub(pnConfiguration);
            customCallbackPubnub = new CustomCallbackPubnub(context, device.getDeviceId(), device.getLocation());
        }
    }

    public void subChannel(Context context, Device device, String... channels) {
        if(pubnub == null){
            initPubnub(context, device);
        }
        pubnub.addListener(customCallbackPubnub);
        pubnub.subscribe().channels(Arrays.asList(channels)).execute();
    }

    public void unsubChannel(Context context, Device device, String... channels) {
        pubnub.removeListener(customCallbackPubnub);
        pubnub.unsubscribe()
                .channels(Arrays.asList(channels))
                .execute();
    }

    public void pubChannel(String channel, String message) {
        pubnub.publish().channel(channel).message(message).async(new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) {
                // Check whether request successfully completed or not.
                if (!status.isError()) {

                    // Message successfully published to specified channel.
                }
                // Request processing failed.
                else {

                    // Handle message publish error. Check 'category' property to find out possible issue
                    // because of which request did fail.
                    //
                    // Request can be resent using: [status retry];
                }
            }
        });
    }

}
