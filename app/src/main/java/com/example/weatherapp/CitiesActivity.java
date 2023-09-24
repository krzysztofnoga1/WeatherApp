package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class CitiesActivity extends AppCompatActivity{

    private List<String> cities;

    EditText editText;
    CityListAdapter cityListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        cities=new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);

        readCitiesFromFile();
        RecyclerView recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cityListAdapter=new CityListAdapter(cities, this);
        recyclerView.setAdapter(cityListAdapter);
        editText=findViewById(R.id.editText);

        findViewById(R.id.add_btn).setOnClickListener(view -> {
            String newCity=editText.getText().toString();
            if(!newCity.equals("")){
                cities.add(newCity);
                cityListAdapter.notifyItemInserted(cities.size()-1);
                editText.setText("");
            }
        });
    }

    private void readCitiesFromFile(){
        File file=new File(CitiesActivity.this.getFilesDir(), "cities");
        try{
            BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
            String line;
            while((line=bufferedReader.readLine())!=null){
                cities.add(line);
            }
            bufferedReader.close();
        }catch(IOException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void goBackToMain(View view){
        saveCitiesToFile();
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void saveCitiesToFile(){
        File file=new File(CitiesActivity.this.getFilesDir(), "cities");
        try{
            FileOutputStream fileOutputStream=new FileOutputStream(file, false);
            OutputStreamWriter writer=new OutputStreamWriter(fileOutputStream);
            for(int i=0; i<cityListAdapter.getItemCount(); i++){
                writer.append(cityListAdapter.cityList.get(i)).append("\r");
            }
            writer.flush();
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("text", editText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        editText.setText(savedInstanceState.getString("text"));
        super.onRestoreInstanceState(savedInstanceState);
    }
}
