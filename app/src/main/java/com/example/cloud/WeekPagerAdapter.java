package com.example.cloud;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class WeekPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_ITEMS = 1001;

    public WeekPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        WeekFragment weekFragment = new WeekFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", NUM_ITEMS - position);
        weekFragment.setArguments(bundle);
        return weekFragment;
    }

    @Override
    public int getItemCount() {
        return NUM_ITEMS;
    }
}
