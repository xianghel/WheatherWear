package com.example.wheatherwear.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author lenovo
 */
public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond_txt")
    public String more;


}
