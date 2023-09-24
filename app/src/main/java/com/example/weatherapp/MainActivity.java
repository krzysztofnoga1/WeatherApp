package com.example.weatherapp;

import static java.lang.Thread.sleep;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private static final String BASE_URL="http://api.openweathermap.org/data/2.5/weather?q=";
    private static final String API_KEY="&appid=da184db7c7926c7b3f1ab92481aca502&units=metric&lang=pl";
    private static final String API_KEY_F="&appid=da184db7c7926c7b3f1ab92481aca502&lang=pl";
    private static final String BASE_URL_FORECAST="http://api.openweathermap.org/data/2.5/forecast?q=";
    RequestQueue requestQueue;
    int currentCityIndex;
    private double refreshTimeout;
    private String units;
    private List<Fragment> fragmentList;
    private ArrayList<City> cities;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter viewPagerAdapter;
    private boolean isTablet;
    private boolean threadRunning;
    private Thread dataThread;
    private DetailData fragmentDetailData;
    private UpcomingDaysData fragmentUpcomingDaysData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentCityIndex = 0;
        super.onCreate(savedInstanceState);
        setFields();
        setContentView(R.layout.activity_main);

        if(!checkIfFileEmpty("cities"))
            writeCity();

        if(!checkIfFileEmpty("settings"))
            writeSettings();

        readSavedData();
        checkInternetConnection();
        viewPager2 = findViewById(R.id.fragment_frame);
        if(viewPager2!=null){
            viewPagerAdapter = new ViewPagerAdapter(this, cities.get(currentCityIndex));
            viewPager2.setAdapter(viewPagerAdapter);
        }
        else{
            configureForTablet();
        }
        updateCitiesData();
    }

    @Override
    protected void onStart() {
        threadRunning=true;
        super.onStart();
        dataThread=new Thread(()->{
            while(threadRunning){
                if(internetIsConnected(getApplicationContext())){
                    updateCitiesData();
                    if(!isTablet)
                        viewPagerAdapter.city=cities.get(currentCityIndex);
                    runOnUiThread(() -> {
                        updateCitiesData();
                        Toast.makeText(getApplicationContext(), "Data updated.", Toast.LENGTH_SHORT).show();
                    });
                }
                try {
                    sleep((long)refreshTimeout);
                }catch(InterruptedException e){
                    finish();
                }
            }
        });
        dataThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        threadRunning=false;
    }

    private void setFields(){
        threadRunning=true;
        isTablet=false;
        requestQueue=Volley.newRequestQueue(getApplicationContext());
        cities = new ArrayList<>();
        fragmentList = new ArrayList<>();
    }

    private void configureForTablet(){
        isTablet=true;
        fragmentDetailData=new DetailData(cities.get(currentCityIndex));
        fragmentUpcomingDaysData=new UpcomingDaysData(cities.get(currentCityIndex));
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_1, fragmentDetailData);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_2, fragmentUpcomingDaysData);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public boolean internetIsConnected(Context context) {
        boolean connected=false;

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Network activeNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);

        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                connected=true;
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                connected=true;
        }
        return connected;
    }
    public void checkInternetConnection(){
        if(!internetIsConnected(getApplicationContext())){
            int duration=Toast.LENGTH_SHORT;
            Context context=getApplicationContext();
            Toast toast=Toast.makeText(context, "No internet connection. Can't update data.", duration);
            toast.show();
        }
    }

    private void getWeatherDataForCity(int cityIndex){
        String tempUrl=BASE_URL+cities.get(cityIndex).name;
        if(this.units.equals("°C"))
            tempUrl+=API_KEY;
        else
            tempUrl+=API_KEY_F;
        StringRequest stringRequest=new StringRequest(Request.Method.POST, tempUrl, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                cities.get(cityIndex).updateData(jsonObject);
                cities.get(cityIndex).saveCityDataToFile(MainActivity.this);
                if(cityIndex==currentCityIndex){
                    updateCurrentDataFragments();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> System.out.println("Error"));
        requestQueue.add(stringRequest);
    }

    private void getWeatherForecastForCity(int cityIndex){
        String tempUrl=BASE_URL_FORECAST+cities.get(cityIndex).name+API_KEY;
        StringRequest stringRequest=new StringRequest(Request.Method.POST, tempUrl, response ->{
            try{
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("list");
                cities.get(cityIndex).getForecast(jsonArray);
                cities.get(cityIndex).saveForecastToFile(MainActivity.this);
                if(cityIndex==currentCityIndex){
                    updateForecastFragment();
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> System.out.println("Error"));
        requestQueue.add(stringRequest);
    }

    private void updateForecastFragment(){
        if(!isTablet){
            viewPagerAdapter.city.forecast=cities.get(currentCityIndex).forecast;
            if(viewPagerAdapter.fragmentList.get(2).getView()!=null){
                ((UpcomingDaysData)viewPagerAdapter.fragmentList.get(2)).forecastAdapter.forecastElementList=viewPagerAdapter.city.forecast;
                ((UpcomingDaysData)viewPagerAdapter.fragmentList.get(2)).forecastAdapter.notifyDataSetChanged();
            }
        }
        else{
            fragmentUpcomingDaysData.forecastAdapter.notifyDataSetChanged();
        }
    }

    private void updateCurrentDataFragments(){
        if(!isTablet){
            viewPagerAdapter.city=cities.get(currentCityIndex);
            if(viewPagerAdapter.fragmentList.get(0).getView()!=null)
                ((BasicData)viewPagerAdapter.fragmentList.get(0)).setData(viewPagerAdapter.city);
            if(viewPagerAdapter.fragmentList.get(1).getView()!=null)
                ((DetailData)viewPagerAdapter.fragmentList.get(1)).setData(viewPagerAdapter.city);
        }
        else{
            fragmentDetailData.city=cities.get(currentCityIndex);
            fragmentDetailData.setData(fragmentDetailData.city);
        }
    }

    private void updateCitiesData(){
        for(int i=0; i<cities.size(); i++){
            getWeatherDataForCity(i);
            getWeatherForecastForCity(i);
        }
    }

    public void updateDataOnRequest(View view){
        if(internetIsConnected(getApplicationContext())){
            getWeatherDataForCity(currentCityIndex);
            Toast.makeText(getApplicationContext(),"Data updated.", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"No internet connection. Can't update data.", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToCitiesList(View view){
        threadRunning=false;
        Intent intent=new Intent(this, CitiesActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void goToSettings(View view){
        threadRunning=false;
        Intent intent=new Intent(this, SettingsActivity.class);
        intent.putExtra("units", this.units);
        intent.putExtra("refresh", this.refreshTimeout);
        intent.putExtra("current", this.currentCityIndex);
        startActivity(intent);
        this.finish();
    }

    private void writeCity(){
        File file=new File(MainActivity.this.getFilesDir(), "cities");
        try{
            FileWriter writer=new FileWriter(file);
            writer.append("Warszawa");
            writer.append("\r");
            writer.flush();
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void writeSettings(){
        File file=new File(MainActivity.this.getFilesDir(), "settings");
        try{
            FileWriter writer=new FileWriter(file);
            writer.append("30");
            writer.append("\r");
            writer.append("°C");
            writer.flush();
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private boolean checkIfFileEmpty(String filename){
        File file=new File(MainActivity.this.getFilesDir(), filename);
        int lines=0;
        try{
            BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
            while((bufferedReader.readLine())!=null){
                lines++;
            }
            bufferedReader.close();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
        return lines > 1;
    }

    private void readDataForCities(){
        for(int i=0; i<cities.size(); i++){
            cities.get(i).readDataFromFile(MainActivity.this);
            cities.get(i).readForecastFromFile(MainActivity.this);
        }
    }

    private void readCitiesList(){
        File file=new File(MainActivity.this.getFilesDir(), "cities");
        try{
            BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
            String line;
            while((line=bufferedReader.readLine())!=null){
                cities.add(new City(line, this.units));
            }
            bufferedReader.close();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }



    private void readSettings(){
        File file=new File(MainActivity.this.getFilesDir(), "settings");
        try{
            BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
            String line=bufferedReader.readLine();
            this.refreshTimeout=Double.parseDouble(line);
            this.refreshTimeout*=60000;
            line=bufferedReader.readLine();
            this.units=line;
            bufferedReader.close();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    private void readCurrentCity(){
        File file=new File(MainActivity.this.getFilesDir(), "currentcity");
        try{
            BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
            String line=bufferedReader.readLine();
            bufferedReader.close();
            int current=Integer.parseInt(line);
            if(current>=cities.size())
                currentCityIndex=cities.size()-1;
            else
                currentCityIndex=current;
        }catch(IOException e){
            System.out.println(e.getMessage());
        }

    }

    private void readSavedData(){
        readSettings();
        readCitiesList();
        readDataForCities();
        readCurrentCity();
    }
}