package com.example.psyccare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.psyccare.DataAdapters.StatsTabAdapter;
import com.example.psyccare.Fragments.MoodStats;
import com.example.psyccare.Fragments.ThoughtStats;
import com.google.android.material.tabs.TabLayout;

public class MyStats extends AppCompatActivity {

    TabLayout statsTabs;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stats);

        statsTabs = findViewById(R.id.statsTab);
        viewPager = findViewById(R.id.statsViewPager);

        StatsTabAdapter adapter = new StatsTabAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new MoodStats(), "Mood Check Ins");
        adapter.addFragment(new ThoughtStats(), "Thought Check Ins");
        viewPager.setAdapter(adapter);
        statsTabs.setupWithViewPager(viewPager);
    }
}