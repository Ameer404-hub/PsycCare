package com.example.psyccare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.psyccare.Fragments.Home;
import com.example.psyccare.Fragments.Discover;
import com.example.psyccare.Fragments.More;
import com.example.psyccare.Fragments.Progress;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomeContainer extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
        chipNavigationBar.setItemSelected(R.id.bottom_nav_home, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
        bottomMenu();

    }

    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.bottom_nav_home:
                        fragment = new Home();
                        break;
                    case R.id.bottom_nav_discover:
                        fragment = new Discover();
                        break;
                    case R.id.bottom_nav_progress:
                        fragment = new Progress();
                        break;
                    case R.id.bottom_nav_more:
                        fragment = new More();
                        break;
                }
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
            }
        });
    }
}