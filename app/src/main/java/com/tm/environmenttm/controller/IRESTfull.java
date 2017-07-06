package com.tm.environmenttm.controller;

import com.tm.environmenttm.CustomModel.ResponeUserLogin;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.model.Account;
import com.tm.environmenttm.model.ChartThingspeak;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.Environment;
import com.tm.environmenttm.model.ResponeBoolean;
import com.tm.environmenttm.model.ResponeNumber;
import com.tm.environmenttm.model.Type;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by taima on 06/29/2017.
 */

public interface IRESTfull {

    //create account
    @POST(ConstantURL.CREATE_ACCOUNT)
    Call<Account> createAccount(@Body Account account);
    //check login
    @POST(ConstantURL.CHECK_LOGIN)
    Call<ResponeUserLogin>  checkLogin(@Body Account account);
    //list device
    @GET(ConstantURL.GET_ALL_DEVICE)
    Call<List<Device>> getAllDevice();

    //get list device
    @GET(ConstantURL.GET_ALL_DEVICE)
    Call<List<Device>> getDeviceByLocation(@Query("filter") String location);

    //detail device

    //edit device
    @POST(ConstantURL.EDIT_DEVICE)
    Call<ResponeBoolean> editDevice(@Body Device device, @Path("id") String id);

    //delete device

    //environment current
    @GET(ConstantURL.GET_INFO_ENVIRONMENT_CURRENT)
    Call<String> getEnvironmentCurrent(@Query("deviceId") String deviceId);

    //led control
    @GET(ConstantURL.LED_CONTROL)
    Call<ResponeBoolean> ledControl(@Query("deviceId") String deviceId, @Query("command") String command);

    //isLed
    @GET(ConstantURL.IS_LED)
    Call<ResponeBoolean> isLed(@Query("deviceId") String deviceId);

    //get time delay
    @GET(ConstantURL.GET_TIME_DELAY)
    Call<ResponeNumber> getTimeDelay(@Query("deviceId") String deviceId);

    //set time delay
    @GET(ConstantURL.SET_TIME_DELAY)
    Call<ResponeBoolean> setTimeDelay(@Query("deviceId") String deviceId, @Query("timeDelay") int timeDelay);

    //get environment by device
    @GET(ConstantURL.GET_INFO_ENVIRONMENT_BY_DEVICE)
    Call<List<Environment>> getInfoEnvironmentByDevice(@Path("id") String id, @Query("filter") String filter);

    //get battery by device

    //get list chart by device


    //set state blub

    //get Type by id
    @GET(ConstantURL.GET_TYPE_BY_ID)
    Call<Type> getTypeById(@Path("id") String id);

    //get all type
    @GET(ConstantURL.GET_ALL_TYPE)
    Call<List<Type>> getAllTypeDevice();

    //get list chart thingspeak
    @GET(ConstantURL.GET_ALL_CHART_BY_DEVICE_ID)
    Call<List<ChartThingspeak>> getAllChartThingspeak(@Path("id") String id);


}
