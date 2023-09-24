package com.example.weatherapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailData extends Fragment {
    City city;
    TextView cityNameDetail;
    TextView descriptionDetail;
    TextView temperatureDetail;
    TextView temperatureFeelsLikeDetail;
    TextView pressureDetail;
    TextView windSpeedDetail;
    TextView windDirectionDetail;
    TextView longitude;
    TextView latitude;
    TextView humidityDetail;
    TextView visibilityDetail;
    TextView sunriseDetail;
    TextView sunsetDetail;

    public DetailData(){

    }

    public DetailData(City city){
        this.city=city;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            city=(City)savedInstanceState.getSerializable("city");
        }
        View view=inflater.inflate(R.layout.fragment_detail_data, container, false);
        descriptionDetail=view.findViewById(R.id.description);
        temperatureDetail=view.findViewById(R.id.temp);
        temperatureFeelsLikeDetail =view.findViewById(R.id.temp_feels_like);
        cityNameDetail=view.findViewById(R.id.city_name_detail);
        pressureDetail=view.findViewById(R.id.pressure_detail);
        windSpeedDetail=view.findViewById(R.id.wind_speed_detail);
        windDirectionDetail=view.findViewById(R.id.wind_direction_detail);
        longitude=view.findViewById(R.id.longitude_detail);
        latitude=view.findViewById(R.id.latitude_detail);
        humidityDetail=view.findViewById(R.id.humidity_detail);
        visibilityDetail=view.findViewById(R.id.visibility_detail);
        sunriseDetail=view.findViewById(R.id.sunrise);
        sunsetDetail=view.findViewById(R.id.sunset);
        setData(city);
        return view;
    }

    public void setData(City city){
        cityNameDetail.setText(city.name);
        temperatureDetail.setText(city.temperature);
        temperatureFeelsLikeDetail.setText(city.temperatureFeelsLike);
        descriptionDetail.setText(city.description);
        windDirectionDetail.setText(city.windDirection);
        windSpeedDetail.setText(city.windSpeed);
        pressureDetail.setText(city.pressure);
        longitude.setText(city.longitude);
        latitude.setText(city.latitude);
        humidityDetail.setText(city.humidity);
        visibilityDetail.setText(city.visibility);
        sunsetDetail.setText(city.sunset);
        sunriseDetail.setText(city.sunrise);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("city", city);
        super.onSaveInstanceState(outState);
    }
}