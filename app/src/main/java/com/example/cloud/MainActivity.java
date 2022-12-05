package com.example.cloud;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    int WeekPoint = 1000;
    int DayPoint = 1000;
    int week_check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        ViewPager2 vpPager = findViewById(R.id.view_pager_day);
        FragmentStateAdapter adapter = new DayPagerAdapter(this);

        vpPager.setAdapter(adapter);

        vpPager.setCurrentItem(DayPoint, false);

        vpPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                week_check += position - DayPoint;
                DayPoint = position;
                if(week_check/7 != 0) {
                    WeekPoint += week_check / 7;
                    week_check = 0;
                }
            }
        });
    }

    public void OnDay(View view) {
        setContentView(R.layout.activity_day);
        ViewPager2 vpPager = findViewById(R.id.view_pager_day);
        FragmentStateAdapter adapter = new DayPagerAdapter(this);

        vpPager.setAdapter(adapter);

        vpPager.setCurrentItem(DayPoint, false);

        vpPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                week_check += position - DayPoint;
                DayPoint = position;
                if(week_check%7 == 0) {
                    WeekPoint += week_check / 7;
                    week_check = 0;
                }
            }
        });
    }

    public void OnWeek(View view) {
        setContentView(R.layout.activity_week);
        ViewPager2 vpPager = findViewById(R.id.view_pager_week);
        FragmentStateAdapter adapter = new WeekPagerAdapter(this);

        vpPager.setAdapter(adapter);

        vpPager.setCurrentItem(WeekPoint, false);

        vpPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                DayPoint += (position - WeekPoint) * 7;
                WeekPoint = position;
            }
        });
    }

}