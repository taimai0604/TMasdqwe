package com.tm.environmenttm.map;

/**
 * Created by HUYỀN MY NGUYỄN THỊ on 03/08/2016.
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParserLocation {
    public List<HashMap<String, String>> parse(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            Log.d("Places", "parse");
            jsonObject = new JSONObject((String) jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            Log.d("Places", "parse error");
            e.printStackTrace();
        }
        return getPlaceAddress(jsonArray);
    }

    private List<HashMap<String, String>> getPlaceAddress(JSONArray jsonArray) {
        int placesCount = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String, String> placeMap = null;
        Log.d("Places", "getPlaces");

        for (int i = 0; i < placesCount; i++) {
            try {
                placeMap = getInfoAddress((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
                Log.d("getAddress", "Adding Address");

            } catch (JSONException e) {
                Log.d("getAddress", "Error in Adding Address");
                e.printStackTrace();
            }
        }
        return placesList;
    }

    private HashMap<String, String> getInfoAddress(JSONObject googlePlaceJson) {
        HashMap<String, String> googlePlaceMap = new HashMap<String, String>();
        String latitude = "";
        String longitude = "";
        String formatted_address = "";

        Log.d("getInfoAddress", "Entered");

        try {
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            formatted_address = googlePlaceJson.getString("formatted_address");
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("formatted_address", formatted_address);
            Log.d("getAddressItem", "Putting Address");
        } catch (JSONException e) {
            Log.d("getAddressItem", "Error");
            e.printStackTrace();
        }
        return googlePlaceMap;
    }
}
