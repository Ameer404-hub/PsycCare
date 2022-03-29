package com.example.psyccare.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.psyccare.DataAdapters.TabsAdapter;
import com.example.psyccare.R;
import com.google.android.material.tabs.TabLayout;

public class ProgressFragment extends Fragment {

    TabLayout Tabs;
    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_progress, container, false);

        Tabs = rootView.findViewById(R.id.tabView);
        viewPager = rootView.findViewById(R.id.tabViewPager);

        TabsAdapter adapter = new TabsAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new MoodTabFragment(), "Mood Tracker");
        adapter.addFragment(new ThoughtTabFragment(), "My Thoughts");
        viewPager.setAdapter(adapter);
        Tabs.setupWithViewPager(viewPager);

        return rootView;
    }
}