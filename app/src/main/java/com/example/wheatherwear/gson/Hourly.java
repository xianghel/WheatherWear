package com.example.wheatherwear.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author lxh (2019/4/4)
 */
public class Hourly {
    @SerializedName("time")
   public String hTime;
    @SerializedName("cond_txt")
   public String txt;
    @SerializedName("tmp")
   public String tmp;
}
