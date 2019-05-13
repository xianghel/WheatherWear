package com.example.wheatherwear;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wheatherwear.gson.Weather;
import com.example.wheatherwear.util.HttpUtil;
import com.example.wheatherwear.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static org.litepal.LitePalApplication.getContext;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG ="SearchActivity";
    private TextView cancelText;
    private EditText cityEdit;
    private ListView cityListView;
    private List<String> cityList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        addTextChangedListener();
    }

    private void initView() {
        cancelText = findViewById(R.id.cancel_text);
        cityEdit = findViewById(R.id.city_edit);
        cityListView = findViewById(R.id.search_city_list);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, cityList);
        cityListView.setAdapter(adapter);
    }

    private void addTextChangedListener() {
        cityEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 输入的内容变化的监听
                Log.e("输入过程中执行该方法", "文字变化");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 输入前的监听
                Log.e("输入前确认执行该方法", "开始输入");

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 输入后的监听
                Log.e("输入结束执行该方法", "输入结束");
                String cityName = cityEdit.getText().toString();
                requestCity(cityName);
            }

        });
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据。
     */
    private void requestCity(final String cityName) {
        String cityUrl = "https://free-api.heweather.net/s6/weather?location=" + cityName + "&key=bf530e2a48ca4e66bc5315dae80f494d";
        HttpUtil.sendOkHttpRequest(cityUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null) {
                            if ("ok".equals(weather.status)) {

                            } else {
                                Log.d(TAG, "天气状态不ok");
                            }
                        } else {
                            Log.d(TAG, "天气为空");
                        }
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
                    }
                });
            }
        });
    }
}
