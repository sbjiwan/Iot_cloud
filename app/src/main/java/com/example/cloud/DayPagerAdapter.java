package com.example.cloud;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DayPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_ITEMS = 1001;

    public DayPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    // 각 페이지를 나타내는 프래그먼트 반환
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        DayFragment dayFragment = new DayFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", NUM_ITEMS - position);
        dayFragment.setArguments(bundle);
        return dayFragment;
    }

    // 전체 페이지 개수 반환
    @Override
    public int getItemCount() {
        return NUM_ITEMS;
    }
}
