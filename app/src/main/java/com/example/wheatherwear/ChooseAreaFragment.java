package com.example.wheatherwear;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * @author lxh (2019/4/4)
 */
public class ChooseAreaFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ChooseAreaFragment";
    private LinearLayout home;
    private LinearLayout city;
    private LinearLayout clothes;
    private LinearLayout about;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        home = view.findViewById(R.id.home);
        city = view.findViewById(R.id.my_city);
        clothes = view.findViewById(R.id.my_clothes);
        about = view.findViewById(R.id.about);
        home.setOnClickListener(this);
        city.setOnClickListener(this);
        clothes.setOnClickListener(this);
        about.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home:
                Intent intent=new Intent(getContext(),WeatherActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.my_city:
                break;
            case R.id.my_clothes:
                Intent intent1=new Intent(getContext(),ClothesActivity.class);
                startActivity(intent1);
                break;
            case R.id.about:
                break;
            default:
        }
    }
}
