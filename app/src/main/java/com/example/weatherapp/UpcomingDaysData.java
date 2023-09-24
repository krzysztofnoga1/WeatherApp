package com.example.weatherapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UpcomingDaysData extends Fragment {
    City city;
    ForecastAdapter forecastAdapter;
    public UpcomingDaysData(){

    }

    public UpcomingDaysData(City city){
        this.city=city;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(savedInstanceState!=null){
            city=(City)savedInstanceState.getSerializable("city");
        }
        View view=inflater.inflate(R.layout.fragment_upcoming_days_data, container, false);
        RecyclerView recyclerView=view.findViewById(R.id.forecast_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        forecastAdapter=new ForecastAdapter(city.forecast);
        recyclerView.setAdapter(forecastAdapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("city", city);
        super.onSaveInstanceState(outState);
    }
}