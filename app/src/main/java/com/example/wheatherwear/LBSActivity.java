package com.example.wheatherwear;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;
import java.util.List;

public class LBSActivity extends AppCompatActivity {

    private final String TAG="LBSActivity";
    public LocationClient mLocationClient;

    private TextView positionText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient=new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        setContentView(R.layout.activity_lbs);
        positionText=(TextView)findViewById(R.id.position_text_view);
        List<String> permissionList=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(LBSActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(LBSActivity.this,Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(LBSActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(LBSActivity.this,permissions,1);
        }else{
            requestLocation();
        }

    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }
    private void initLocation(){
        LocationClientOption option=new LocationClientOption();
        option.setScanSpan(5000);
        mLocationClient.setLocOption(option);
    }
    public class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG,"MyLocationListener is run()");
                    StringBuilder currentPosition=new StringBuilder();
                    currentPosition.append("纬度：").append(bdLocation.getLatitude()).append("\n");
                    currentPosition.append("经线: ").append(bdLocation.getLongitude()).append("\n");
                    currentPosition.append("定位方式: ");
                    if(bdLocation.getLocType()==BDLocation.TypeGpsLocation){
                        currentPosition.append("GPS");
                    }else if(bdLocation.getLocType()==BDLocation.TypeNetWorkLocation){
                        currentPosition.append("网络");
                    }
                    Log.d(TAG,"currentPosition:"+currentPosition);
                    positionText.setText(currentPosition);
                }
            });
        }
    }
    @Override
    protected  void onDestroy(){
        super.onDestroy();
        mLocationClient.stop();
    }
}
