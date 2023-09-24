package com.example.weatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastItem> {
    List<ForecastElement> forecastElementList;

    ForecastAdapter(List<ForecastElement> forecastElementList){
        this.forecastElementList=forecastElementList;
    }

    @NonNull
    @Override
    public ForecastItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forecast_item,parent,false);
        return new ForecastItem(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastItem holder, int position) {
        holder.date.setText(forecastElementList.get(position).time);
        holder.setWeatherImage(forecastElementList.get(position).weatherType);
    }

    @Override
    public int getItemCount() {
        return forecastElementList.size();
    }
}

class ForecastItem extends RecyclerView.ViewHolder{
    TextView date;
    ImageView image;
    private ForecastAdapter forecastAdapter;

    public ForecastItem(@NonNull View itemView){
        super(itemView);
        date=itemView.findViewById(R.id.forecast_date);
        image=itemView.findViewById(R.id.forecast_image);
    }

    public void setWeatherImage(String weatherType){
        switch(weatherType){
            case "Clear":
                image.setImageResource(R.drawable.sun);
                break;
            case "Rain":
                image.setImageResource(R.drawable.rainy);
                break;
            case "Snow":
                image.setImageResource(R.drawable.snowy);
                break;
            case "Clouds":
                image.setImageResource(R.drawable.cloudy);
        }
    }

    public ForecastItem linkAdapter(ForecastAdapter forecastAdapter){
        this.forecastAdapter=forecastAdapter;
        return this;
    }


}
