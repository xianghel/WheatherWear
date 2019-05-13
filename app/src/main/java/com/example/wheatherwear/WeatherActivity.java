package com.example.wheatherwear;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wheatherwear.gson.Forecast;
import com.example.wheatherwear.gson.Hourly;
import com.example.wheatherwear.gson.ImageURL;
import com.example.wheatherwear.gson.Weather;
import com.example.wheatherwear.service.AutoUpdateService;
import com.example.wheatherwear.util.HttpUtil;
import com.example.wheatherwear.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author lxh (2019/4/4)
 */
public class WeatherActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private Button navButton;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView drsgText;

    private TextView fluText;

    private TextView travText;

    private TextView uvText;

    private TextView cwText;

    private TextView airText;

    private TextView sportText;

    private ImageView bingPicImg;

    private String mWeatherId;
    private String weatherId;

    private String latitude;

    private String longitude;

    private LinearLayout hourlyLinearLayout;

    private final String TAG = "WeatherActivity";

    private static final String Cloudy = "多云";
    private static final String Sunny = "晴";
    private static final String Shade = "阴";
    private static final String LightRain = "小雨";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        // 初始化各控件
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        drsgText = (TextView) findViewById(R.id.drsg_text);
        fluText = (TextView) findViewById(R.id.flu_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        travText = (TextView) findViewById(R.id.trav_text);
        uvText = (TextView) findViewById(R.id.uv_text);
        cwText = (TextView) findViewById(R.id.cw_text);
        airText = (TextView) findViewById(R.id.air_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        hourlyLinearLayout = (LinearLayout) findViewById(R.id.hourly_layout);
        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        weatherId=getIntent().getStringExtra("weatherId");
        Log.d(TAG, "经度：" + latitude + "纬度：" + longitude);
        if (latitude != null && longitude != null) {
            requestWeatherByJingwei(latitude, longitude);
        }else if(weatherId!=null){
            requestWeather(weatherId);
        }else{
            drawerLayout.closeDrawer(GravityCompat.START);
            SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
            String responseText =pref.getString("weather",null);
            Weather weather = Utility.handleWeatherResponse(responseText);
            showWeatherInfo(weather);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息。
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "https://free-api.heweather.net/s6/weather?location=" + weatherId + "&key=bf530e2a48ca4e66bc5315dae80f494d";
        Log.d(TAG, weatherUrl);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null) {
                            if ("ok".equals(weather.status)) {
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                                editor.putString("weather", responseText);
                                editor.apply();
                                mWeatherId = weather.basic.weatherId;
                                showWeatherInfo(weather);
                            } else {
                                Log.d(TAG, "天气状态不ok");
                                Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "天气为空");
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "网络有错");
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    /**
     * 根据根据经纬度请求城市天气信息。
     */
    public void requestWeatherByJingwei(final String latitude, final String longitude) {
        String weatherUrl = "https://free-api.heweather.net/s6/weather?location=" + latitude + "," + longitude + "&key=bf530e2a48ca4e66bc5315dae80f494d";
        Log.d(TAG, weatherUrl);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null) {
                            if ("ok".equals(weather.status)) {
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                                editor.putString("weather", responseText);
                                editor.apply();
                                mWeatherId = weather.basic.weatherId;
                                showWeatherInfo(weather);
                            } else {
                                Log.d(TAG, "天气状态不ok");
                                Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "天气为空");
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "网络有错");
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    /**
     * 加载必应每日一图//必应不行，初步解决是每种天气情况对应一张美图
     */
    private void loadBingPic(String weatherInfo) {
        Log.d(TAG, "loadBingPic()");
        switch (weatherInfo) {
            case Cloudy:
                Glide.with(WeatherActivity.this).load(R.drawable.cloudy).into(bingPicImg);
                break;
            case Sunny:
                Glide.with(WeatherActivity.this).load(R.drawable.sunny).into(bingPicImg);
                break;
            case Shade:
                Glide.with(WeatherActivity.this).load(R.drawable.shade).into(bingPicImg);
                break;
            case LightRain:
                Glide.with(WeatherActivity.this).load(R.drawable.lightrain).into(bingPicImg);
                break;
            default:
        }
    }

    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather weather) {
        Log.d(TAG, "showWeatherInfo()");
        String cityName = weather.basic.cityName;
        String updateTime = weather.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more;
        loadBingPic(weatherInfo);
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime + "发布");
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Hourly hourly : weather.hourlyList) {
            View view = LayoutInflater.from(this).inflate(R.layout.hourly_item, hourlyLinearLayout, false);
            TextView timeText = (TextView) view.findViewById(R.id.time_txt);
            TextView infoText = (TextView) view.findViewById(R.id.info_txt);
            TextView tmpText = (TextView) view.findViewById(R.id.tmp_txt);
            String[] strings = hourly.hTime.split(" ");
            timeText.setText(strings[1]);
            infoText.setText(hourly.txt);
            tmpText.setText(hourly.tmp + "℃");
            hourlyLinearLayout.addView(view);
        }
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more);
            maxText.setText(forecast.max + "℃");
            minText.setText(forecast.min + "℃");
            forecastLayout.addView(view);
        }
        String comfort = "舒适度指数：" + weather.suggestionList.get(0).brf + "  " + weather.suggestionList.get(0).info;
        String drsg = "穿衣指数：" + weather.suggestionList.get(1).brf + "  " + weather.suggestionList.get(1).info;
        String fu = "感冒指数: " + weather.suggestionList.get(2).brf + "  " + weather.suggestionList.get(2).info;
        String sport = "运动指数：" + weather.suggestionList.get(3).brf + "  " + weather.suggestionList.get(3).info;
        String trav = "旅游指数：" + weather.suggestionList.get(4).brf + "  " + weather.suggestionList.get(4).info;
        String uv = "紫外线指数：" + weather.suggestionList.get(3).brf + "  " + weather.suggestionList.get(5).info;
        String cw = "洗车指数：" + weather.suggestionList.get(4).brf + "  " + weather.suggestionList.get(6).info;
        String air = "空气污染扩散条件指数：" + weather.suggestionList.get(4).brf + "  " + weather.suggestionList.get(7).info;
        comfortText.setText(comfort);
        drsgText.setText(drsg);
        sportText.setText(sport);
        fluText.setText(fu);
        travText.setText(trav);
        uvText.setText(uv);
        cwText.setText(cw);
        airText.setText(air);
        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

}
