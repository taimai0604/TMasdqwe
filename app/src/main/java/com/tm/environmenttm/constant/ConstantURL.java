package com.tm.environmenttm.constant;

/**
 * Created by taima on 06/29/2017.
 */

public class ConstantURL {
    //server
    public static final String SERVER = "https://cryptic-sea-66379.herokuapp.com/";
//    public static final String SERVER = "http://103.9.158.232:3000";

    //account
    public static final String CREATE_ACCOUNT = "api/Accounts";
    public static final String EDIT_ACCOUNT = "api/Accounts/{id}";
    public static final String CHECK_LOGIN = "/api/Accounts/login?include=user";

    //device
    public static final String GET_ALL_DEVICE = "/api/Devices";
    public static final String ADD_DEVICE = "/api/Devices/addDevice";
    public static final String GET_DEVICE_BY_ID = "/api/Devices/{id}";
    public static final String EDIT_DEVICE = "/api/Devices/{id}/editDevice";
    public static final String DELETE_DEVICE = "/api/Devices/removeDevice";
    public static final String LED_CONTROL = "/api/Devices/controllerLed";
    public static final String IS_LED = "/api/Devices/isLed";
    public static final String GET_TIME_DELAY = "/api/Devices/getTimeDelay";
    public static final String SET_TIME_DELAY = "/api/Devices/setTimeDelay";

    public static final String GET_LOW_TEMP = "/api/Devices/getLowTemp";
    public static final String SET_LOW_TEMP = "/api/Devices/setLowTemp";

    public static final String GET_HEIGHT_TEMP = "/api/Devices/getHeightTemp";
    public static final String SET_HEIGHT_TEMP = "/api/Devices/setHeightTemp";


    //type
    public static final String GET_ALL_TYPE = "/api/Types";
    public static final String GET_TYPE_BY_ID = "/api/Types/{id}";

    //environment
    public static final String GET_INFO_ENVIRONMENT_CURRENT = "/api/Devices/getInfoEnv";
    public static final String GET_INFO_ENVIRONMENT_BY_DEVICE = "/api/Devices/{id}/environments";


    //chart thingspeak
    public static final String GET_ALL_CHART_BY_DEVICE_ID = "/api/Devices/{id}/chartThingspeaks";
    public static final String ADD_THINGSPEAK = "/api/chartThingspeaks";
    public static final String EDIT_CHART = "/api/chartThingspeaks/{id}";
    public static final String DELETE_CHART = "/api/chartThingspeaks/{id}";


}
