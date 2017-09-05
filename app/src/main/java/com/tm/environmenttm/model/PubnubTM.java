package com.tm.environmenttm.model;

import android.content.Context;
import android.util.Log;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.tm.environmenttm.CustomModel.CustomCallbackPubnub;
import com.tm.environmenttm.constant.ConstantValue;

import java.util.Arrays;

import static android.content.ContentValues.TAG;

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
    private Context context;

    public void initPubnub(Context context, Device device) {
        if (pubnub == null) {
            pnConfiguration.setSubscribeKey(ConstantValue.SUB_PUBNUB);
            pnConfiguration.setPublishKey(ConstantValue.PUB_PUBNUB);
            pubnub = new PubNub(pnConfiguration);
            customCallbackPubnub = new CustomCallbackPubnub(context, device.getDeviceId(), device.getLocation());
            this.context = context;
        }
    }

    public void updateDevice(Device device) {
        customCallbackPubnub = new CustomCallbackPubnub(context, device.getDeviceId(), device.getLocation());
        Log.d(TAG, "updateDevice: " + device.getLocation());
    }

    public void subChannel(Context context, Device device, String... channels) {
        if (pubnub == null) {
            initPubnub(context, device);
        }
        pubnub.addListener(customCallbackPubnub);
        pubnub.subscribe().channels(Arrays.asList(channels)).execute();
        Log.d(TAG, "subChannel: " + customCallbackPubnub.getLocation());
    }

    public void unsubChannel(Context context, Device device, String... channels) {
        if (pubnub == null) {
            initPubnub(context, device);
        }
        pubnub.removeListener(customCallbackPubnub);
        pubnub.unsubscribe()
                .channels(Arrays.asList(channels))
                .execute();
        Log.d(TAG, "unsubChannel: " + customCallbackPubnub.getLocation());
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
