package com.example.myapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JasonParser {
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
        String longtitude = object.getJSONObject("geometry")
                .getJSONObject("location").getString("lng");

        //put all values in hashmap
            datalist.put("name",name);
            datalist.put("lat",latitude);
            datalist.put("lng",longtitude);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return  datalist;

    }

    private List<HashMap<String,String>> parseJasonArray(JSONArray jsonArray){
        List<HashMap<String,String>> datalist = new ArrayList<>();
        for (int i=0;i<jsonArray.length();i++){
            HashMap<String,String> data = null;
            try {
                data = parseJsonObject((JSONObject) jsonArray.get(i));

                datalist.add(data);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }



   }
        return datalist;
    }

    public  List<HashMap<String,String>> parseResult(JSONObject object){
        JSONArray jsonArray= null;
        try {
            jsonArray= object.getJSONArray("result");
        } catch (JSONException e) {
            throw new RuntimeException(e);

        }
        return parseJasonArray(jsonArray);
    }
}
