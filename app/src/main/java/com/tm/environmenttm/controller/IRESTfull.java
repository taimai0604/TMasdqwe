package com.tm.environmenttm.controller;

import android.view.View;

import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.model.Device;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by taima on 06/29/2017.
 */

public interface IRESTfull {

    //create account

    //check login
    //list device
    @GET(ConstantURL.GET_ALL_DEVICE)
    Call<List<Device>> getAllDevice();

    //detail device

    //edit device

    //delete device

    //environment current

    //get environment by device

    //get battery by device

    //get list chart by device

    //set time delay

    //set state blub

    // search device by location


}
