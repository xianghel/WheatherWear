package com.example.wheatherwear.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author lxh (2019/4/4)
 */
public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {

        @SerializedName("loc")
        public String updateTime;

    }

}
