package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileWriter;

public class SettingsActivity extends AppCompatActivity {
    EditText time;
    Spinner spinnerUnits;
    int current;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle extras=getIntent().getExtras();
        String units=extras.getString("units");
        Double timeout=extras.getDouble("refresh");
        current=extras.getInt("current");
        timeout/=60000;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        spinnerUnits=findViewById(R.id.settings_spinner);
        time=findViewById(R.id.settings_time);
        ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this, R.array.array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerUnits.setAdapter(adapter);
        spinnerUnits.setSelection(adapter.getPosition(units));
        time.setText(String.valueOf(timeout));
    }

    public void goBackToMainFromSettings(View view){
        Intent intent=new Intent(this, MainActivity.class);
        intent.putExtra("currentCityIndex", this.current);
        startActivity(intent);
        this.finish();
    }

    public void saveSettings(View view){
        saveSettingsToFile();
        Intent intent=new Intent(this, MainActivity.class);
        intent.putExtra("currentCityIndex", this.current);
        startActivity(intent);
        this.finish();
    }

    private void saveSettingsToFile(){
        File file=new File(SettingsActivity.this.getFilesDir(), "settings");
        String refresh_time=time.getText().toString();
        if(refresh_time.equals(""))
            refresh_time="15";
        String units=spinnerUnits.getSelectedItem().toString();
        try{
            FileWriter writer=new FileWriter(file);
            writer.append(refresh_time).append("\r");
            writer.append(units).append("\r");
            writer.flush();
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
