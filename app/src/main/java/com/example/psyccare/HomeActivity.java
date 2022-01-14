package com.example.psyccare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.psyccare.Fragments.AssistanceFragment;
import com.example.psyccare.Fragments.HomeFragment;
import com.example.psyccare.Fragments.MoodTrackerFragment;
import com.example.psyccare.Fragments.MoreFragment;
import com.example.psyccare.Fragments.MyThoughtsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomeActivity extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
        chipNavigationBar.setItemSelected(R.id.bottom_nav_home,true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        bottomMenu();

    }

    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.bottom_nav_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.bottom_nav_myThoughts:
                        fragment = new MyThoughtsFragment();
                        break;
                    case R.id.bottom_nav_moodTracker:
                        fragment = new MoodTrackerFragment();
                        break;
                    case R.id.bottom_nav_assistance:
                        fragment = new AssistanceFragment();
                        break;
                    case R.id.bottom_nav_more:
                        fragment = new MoreFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });
    }
}