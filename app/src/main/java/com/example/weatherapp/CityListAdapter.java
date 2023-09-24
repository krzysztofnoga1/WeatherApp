package com.example.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class CityListAdapter extends RecyclerView.Adapter<AdapterItem>{
    List<String> cityList;
    public Context context;

    public CityListAdapter(List<String> cities, Context context){
        this.cityList=cities;
        this.context=context;
    }
    @NonNull
    @Override
    public AdapterItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city,parent,false);
        return new AdapterItem(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterItem holder, int position) {
        holder.city.setText(cityList.get(position));
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }
}

class AdapterItem extends RecyclerView.ViewHolder{

    TextView city;
    private CityListAdapter cityListAdapter;

    public AdapterItem(@NonNull View itemView) {
        super(itemView);
        city=itemView.findViewById(R.id.list_city_name);
        itemView.findViewById(R.id.delete_btn).setOnClickListener(view -> {
            cityListAdapter.cityList.remove(getAdapterPosition());
            cityListAdapter.notifyItemRemoved(getAdapterPosition());
        });

        itemView.findViewById(R.id.choose_btn).setOnClickListener(view -> {
            saveCitiesToFile(cityListAdapter.context);
            saveCurrentCityIndex(cityListAdapter.context);
            Intent intent=new Intent(cityListAdapter.context, MainActivity.class);
            cityListAdapter.context.startActivity(intent);
            ((CitiesActivity)cityListAdapter.context).finish();
        });
    }

    public AdapterItem linkAdapter(CityListAdapter cityListAdapter){
        this.cityListAdapter=cityListAdapter;
        return this;
    }

    private void saveCitiesToFile(Context context){
        File file=new File(context.getFilesDir(), "cities");
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

    private void saveCurrentCityIndex(Context context){
        File file=new File(context.getFilesDir(), "currentcity");
        try{
            FileOutputStream fileOutputStream=new FileOutputStream(file, false);
            OutputStreamWriter writer=new OutputStreamWriter(fileOutputStream);
            writer.append(String.valueOf(getAdapterPosition())).append('\r');
            writer.flush();
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}


