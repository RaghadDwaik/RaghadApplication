package com.example.myapplication;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private HashMap<String,String> parseJsonObject(JSONObject object){
        //Initialize has map
        HashMap<String,String> datalist = new HashMap<>();
        //get name from object
        try {
            String name = object.getString("name");
        //get lat from Object
        String latitude = object.getJSONObject("geometry")
                .getJSONObject("location").getString("lat");
        //get long from Object
        String longitude = object.getJSONObject("geometry")
                .getJSONObject("location").getString("lng");

        //put all values in hashmap
            datalist.put("name",name);
            datalist.put("lat",latitude);
            datalist.put("lng",longitude);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return  datalist;

    }

    private List<HashMap<String,String>> parseJsonArray(JSONArray jsonArray){
        List<HashMap<String,String>> datalist = new ArrayList<>();
        for (int i=0;i<jsonArray.length();i++){
//            HashMap<String,String> data = null;
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                HashMap<String,String>  data = parseJsonObject(object);

                datalist.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

   }
        return datalist;
    }

    public  List<HashMap<String,String>> parseResult(JSONObject object){
        List<HashMap<String, String>> dataList = new ArrayList<>();
        try {
            JSONArray  jsonArray= object.getJSONArray("results");
            dataList = parseJsonArray(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return dataList;
    }
}
