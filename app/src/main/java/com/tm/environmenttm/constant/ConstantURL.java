package com.tm.environmenttm.constant;

/**
 * Created by taima on 06/29/2017.
 */

public class ConstantURL {
    //server
    public static final String SERVER ="https://cryptic-sea-66379.herokuapp.com/";

    //account
    public static final String CREATE_ACCOUNT = "api/Accounts";
    public static final String CHECK_LOGIN = "/api/Accounts/login?include=user";

    //device
    public static final String GET_ALL_DEVICE = "/api/Devices";
    public static final String EDIT_DEVICE = "/api/Devices/{id}/editDevice";
    public static final String LED_CONTROL = "/api/Devices/controllerLed";


    //type
    public static final String GET_ALL_TYPE = "/api/Types";
    public static final String GET_TYPE_BY_ID = "/api/Types/{id}";

    //environment
    public static final String GET_INFO_ENVIRONMENT_CURRENT = "/api/Devices/getInfoEnv";

    //chart thingspeak
    public static final String GET_ALL_CHART_BY_DEVICE_ID = "/api/Devices/{id}/chartThingspeaks";


}
