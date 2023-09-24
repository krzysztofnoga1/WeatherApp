package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter{
    City city;
    List<Fragment> fragmentList;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, City city) {
        super(fragmentActivity);
        fragmentList=new ArrayList<>();
        fragmentList.add(new BasicData(city));
        fragmentList.add(new DetailData(city));
        fragmentList.add(new UpcomingDaysData(city));
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 1:
                return fragmentList.get(1);
            case 2:
                return fragmentList.get(2);
            default:
                return fragmentList.get(0);
        }
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
