package com.example.wheatherwear.util;

import android.text.TextUtils;

import com.example.wheatherwear.db.City;
import com.example.wheatherwear.gson.ImageURL;
import com.example.wheatherwear.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author lxh (2019/4/4)
 */
public class Utility {

    /**
     * 将返回的JSON数据解析成Weather实体类
     */
    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将返回的JSON数据解析成必应每日一图的URL
     */
    public static ImageURL handleBingPicResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("images");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, ImageURL.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
