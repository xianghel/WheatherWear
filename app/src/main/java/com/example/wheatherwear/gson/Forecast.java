package com.example.wheatherwear.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author lxh (2019/4/4)
 */
public class Forecast {
    public String date;
    @SerializedName("cond_txt_d")
    public String more;
    @SerializedName("tmp_max")
    public String max;
    @SerializedName("tmp_min")
    public String min;


}
