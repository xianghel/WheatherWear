package com.example.wheatherwear.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author lxh (2019/4/4)
 */
public class Weather {
    @SerializedName("status")
    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    //public Suggestion suggestion;
    public Update update;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
    @SerializedName("lifestyle")
    public List<Suggestion> suggestionList;
}
