package com.example.weatherapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class BasicData extends Fragment {
    TextView cityName;
    TextView temperature;
    TextView weather;
    TextView pressure;
    TextView windSpeed;
    TextView feelsLike;
    TextView windDirection;
    ImageView weatherGraphic;
    City city;

    public BasicData(){

    }
    public BasicData(City city){
        this.city=city;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            city=(City)savedInstanceState.getSerializable("city");
        }
        View view=inflater.inflate(R.layout.fragment_basic_data, container, false);
        cityName=view.findViewById(R.id.city_name);
        weatherGraphic=view.findViewById(R.id.weather_img);
        temperature=view.findViewById(R.id.temperature);
        weather=view.findViewById(R.id.weather_type);
        pressure=view.findViewById(R.id.pressure);
        windSpeed=view.findViewById(R.id.wind_speed);
        feelsLike=view.findViewById(R.id.feels_like);
        windDirection=view.findViewById(R.id.wind_direction);
        setData(city);
        setWeatherImage(city.weatherType);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setData(city);
    }

    public void setWeatherImage(String weatherType){
        switch(weatherType){
            case "Clear":
                weatherGraphic.setImageResource(R.drawable.sun);
                break;
            case "Rain":
                weatherGraphic.setImageResource(R.drawable.rainy);
                break;
            case "Snow":
                weatherGraphic.setImageResource(R.drawable.snowy);
                break;
            case "Clouds":
                weatherGraphic.setImageResource(R.drawable.cloudy);
        }
    }

    public void setData(City city){
            cityName.setText(city.name);
            temperature.setText(city.temperature);
            String feelsLikeStr="Odczuw: "+city.temperatureFeelsLike;
            feelsLike.setText(feelsLikeStr);
            windDirection.setText(city.windDirection);
            windSpeed.setText(city.windSpeed);
            weather.setText(city.description);
            pressure.setText(city.pressure);
            setWeatherImage(city.weatherType);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("city", city);
        super.onSaveInstanceState(outState);
    }
}