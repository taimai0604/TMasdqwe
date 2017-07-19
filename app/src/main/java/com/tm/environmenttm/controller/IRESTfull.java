package com.tm.environmenttm.controller;

import android.support.annotation.Nullable;

import com.tm.environmenttm.CustomModel.ResponeUserLogin;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.constant.ConstantValue;
import com.tm.environmenttm.fragment.ThingspeakFragment;
import com.tm.environmenttm.model.Account;
import com.tm.environmenttm.model.ChartThingspeak;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.Environment;
import com.tm.environmenttm.model.ResponeBoolean;
import com.tm.environmenttm.model.ResponeDelete;
import com.tm.environmenttm.model.ResponeNumber;
import com.tm.environmenttm.model.Type;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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
    Call<ResponeUserLogin> checkLogin(@Body Account account);

    @POST(ConstantURL.ADD_DEVICE)
    Call<ResponeBoolean> addDevice(@Body Device device);
    //list device
    @GET(ConstantURL.GET_ALL_DEVICE)
    Call<List<Device>> getAllDevice();

    //device by id
    @GET(ConstantURL.GET_DEVICE_BY_ID)
    Call<Device> getDeviceById(@Path("id") String id);
    //get list device
    @GET(ConstantURL.GET_ALL_DEVICE)
    Call<List<Device>> getDeviceByLocation(@Query("filter") String location);

    //detail device

    //edit device
    @POST(ConstantURL.EDIT_DEVICE)
    Call<ResponeBoolean> editDevice(@Body Device device, @Path("id") String id);

    //delete device
    @POST(ConstantURL.DELETE_DEVICE)
    Call<ResponeBoolean> deleteDevice(@Query("deviceId") String deviceId);

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

    //get low temp
    @GET(ConstantURL.GET_LOW_TEMP)
    Call<ResponeNumber> getLowTemp(@Query("deviceId") String deviceId);

    //set low temp
    @GET(ConstantURL.SET_LOW_TEMP)
    Call<ResponeBoolean> setLowTemp(@Query("deviceId") String deviceId, @Query("lowTemp") int timeDelay);

    //get height temp
    @GET(ConstantURL.GET_HEIGHT_TEMP)
    Call<ResponeNumber> getHeightTemp(@Query("deviceId") String deviceId);

    //set height temp
    @GET(ConstantURL.SET_HEIGHT_TEMP)
    Call<ResponeBoolean> setHeightTemp(@Query("deviceId") String deviceId, @Query("heightTemp") int timeDelay);

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
    Call<List<ChartThingspeak>> getAllChartThingspeak(@Path("id") String id, @Query("filter") String filter);

    //add chart thingspeak
    @POST(ConstantURL.ADD_THINGSPEAK)
    Call<ChartThingspeak> addChartThingspeak(@Body ChartThingspeak chartThingspeak);

    //edit chart thingspeak
    @PUT(ConstantURL.EDIT_CHART)
    Call<ChartThingspeak> editChartThingspeak(@Path("id") String id, @Body ChartThingspeak chartThingspeak);

    //delete chart thingspeak
    @DELETE(ConstantURL.DELETE_CHART)
    Call<ResponeDelete> deleteChartThingspeak(@Path("id") String id);


}
