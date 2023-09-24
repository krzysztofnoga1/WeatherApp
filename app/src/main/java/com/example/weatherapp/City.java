package com.example.weatherapp;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;

public class City implements Serializable {
    String name;
    String weatherType;
    String description;
    String longitude;
    String latitude;
    String temperature;
    String temperatureFeelsLike;
    String pressure;
    String humidity;
    String visibility;
    String windSpeed;
    String windDirection;
    String sunrise;
    String sunset;
    String units;
    ArrayList<ForecastElement> forecast;

    City(String name, String units){
        this.name=name;
        weatherType="Clear";
        this.units=units;
        this.forecast=new ArrayList<>();
    }

    public void getWindInfo(JSONObject jsonObject) throws JSONException {
        JSONObject wind=jsonObject.getJSONObject("wind");
        this.windSpeed=wind.getString("speed");
        this.windSpeed+=" m/s";
        this.windDirection=wind.getString("deg");
        this.windDirection+="°";
    }

    public void getWeatherInfo(JSONObject jsonObject) throws JSONException {
        JSONArray weather=jsonObject.getJSONArray("weather");
        JSONObject weatherObj=weather.getJSONObject(0);
        this.weatherType=weatherObj.getString("main");
        String desc=weatherObj.getString("description");
        this.description=desc.substring(0,1).toUpperCase();
        this.description+=desc.substring(1);
    }

    public void getMainInfo(JSONObject jsonObject) throws JSONException{
        JSONObject main=jsonObject.getJSONObject("main");
        this.temperature=String.valueOf(main.getInt("temp"));
        this.temperature+=this.units;
        this.temperatureFeelsLike=String.valueOf(main.getInt("feels_like"));
        this.temperatureFeelsLike+=this.units;
        this.humidity=main.getString("humidity");
        humidity+="%";
        this.pressure=main.getString("pressure");
        this.pressure+=" mBar";
    }

    public void getCoordInfo(JSONObject jsonObject) throws JSONException{
        JSONObject coord=jsonObject.getJSONObject("coord");
        this.longitude=coord.getString("lon");
        this.latitude=coord.getString("lat");
    }

    public void getVisibilityInfo(JSONObject jsonObject) throws JSONException{
        this.visibility= jsonObject.getString("visibility");
        visibility+=" m.";
    }

    public void getSysInfo(JSONObject jsonObject) throws JSONException{
        JSONObject sys=jsonObject.getJSONObject("sys");
        int timezone=jsonObject.getInt("timezone");
        double res=sys.getDouble("sunrise")+timezone;
        int days=(int)res/86400;
        res-=(days*86400);
        int hour=calculateHours((int)res);
        int minute=calculateMinutes((int)res);
        this.sunrise=String.valueOf(hour);
        this.sunrise+=":";
        if(minute<10)
            this.sunrise+="0";
        this.sunrise+=String.valueOf(minute);
        res=sys.getDouble("sunset")+timezone;
        days=(int)res/86400;
        res-=(days*86400);
        hour=calculateHours((int)res);
        minute=calculateMinutes((int)res);
        this.sunset=String.valueOf(hour);
        this.sunset+=":";
        if(minute<10)
            this.sunset+="0";
        this.sunset+=String.valueOf(minute);
    }

    private int calculateHours(int milis){
        return (milis/3600);
    }

    private int calculateMinutes(int milis){
        int rest=milis%3600;
        return (rest/60);
    }

    void updateData(JSONObject jsonObject) throws JSONException{
        getWindInfo(jsonObject);
        getCoordInfo(jsonObject);
        getVisibilityInfo(jsonObject);
        getWeatherInfo(jsonObject);
        getMainInfo(jsonObject);
        getSysInfo(jsonObject);
    }

    public void saveForecastToFile(Context context){
        String filename=this.name+"Forecast";
        File file=new File(context.getFilesDir(), filename);
        try{
            FileOutputStream fileOutputStream=new FileOutputStream(file, false);
            OutputStreamWriter writer=new OutputStreamWriter(fileOutputStream);
            for(int i=0; i<forecast.size(); i++){
                writer.append(forecast.get(i).time).append("\r");
                writer.append(forecast.get(i).weatherType).append("\r");
            }
            writer.flush();
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void readForecastFromFile(Context context){
        String filename=this.name+"Forecast";
        File file=new File(context.getFilesDir(), filename);
        try{
            BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
            for(int i=0; i<40; i++){
                String date=bufferedReader.readLine();
                String weather=bufferedReader.readLine();
                forecast.add(new ForecastElement(date, weather));
            }
            bufferedReader.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void saveCityDataToFile(Context context){
        File file=new File(context.getFilesDir(), this.name);
        try{
            FileOutputStream fileOutputStream=new FileOutputStream(file, false);
            OutputStreamWriter writer=new OutputStreamWriter(fileOutputStream);
            writer.append(this.weatherType).append("\r");
            writer.append(this.description).append("\r");
            writer.append(this.longitude).append("\r");
            writer.append(this.latitude).append("\r");
            writer.append(this.temperature).append("\r");
            writer.append(this.temperatureFeelsLike).append("\r");
            writer.append(this.pressure).append("\r");
            writer.append(this.humidity).append("\r");
            writer.append(this.visibility).append("\r");
            writer.append(this.windSpeed).append("\r");
            writer.append(this.windDirection).append("\r");
            writer.append(this.sunrise).append("\r");
            writer.append(this.sunset).append("\r");
            writer.flush();
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void readDataFromFile(Context context){
        File file=new File(context.getFilesDir(), this.name);
        try{
            BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
            this.weatherType=bufferedReader.readLine();
            this.description=bufferedReader.readLine();
            this.longitude=bufferedReader.readLine();
            this.latitude=bufferedReader.readLine();
            this.temperature=bufferedReader.readLine();
            this.temperatureFeelsLike=bufferedReader.readLine();
            this.pressure=bufferedReader.readLine();
            this.humidity=bufferedReader.readLine();
            this.visibility=bufferedReader.readLine();
            this.windSpeed=bufferedReader.readLine();
            this.windDirection=bufferedReader.readLine();
            this.sunrise=bufferedReader.readLine();
            this.sunset=bufferedReader.readLine();
            bufferedReader.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void clearList(){
        if(forecast.size()>0){
            forecast.removeAll(forecast);
        }
    }

    public void getForecast(JSONArray jsonArray) throws JSONException{
        clearList();
        JSONObject record;
        JSONArray weather;
        JSONObject firstRecord;
        for(int i=0; i<40; i++){
            record=jsonArray.getJSONObject(i);
            weather=record.getJSONArray("weather");
            firstRecord=weather.getJSONObject(0);
            String weatherT=firstRecord.getString("main");
            String weatherD=record.getString("dt_txt");
            weatherD=weatherD.substring(0,weatherD.length()-3);
            forecast.add(new ForecastElement(weatherD, weatherT));
        }
    }

}
