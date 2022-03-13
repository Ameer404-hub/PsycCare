package com.example.psyccare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.psyccare.Fragments.HomeFragment;
import com.google.android.material.snackbar.Snackbar;

public class ThoughtCheckin extends AppCompatActivity {

    ImageView submitThought;
    LinearLayout Snackbar_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thought_checkin);

        submitThought = findViewById(R.id.sendThoughtNote);
        Snackbar_layout = findViewById(R.id.snackBarAction);

        submitThought.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveTo = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(moveTo);
                finish();
            }
        });
    }

    public void onBackPressed() {
        Snackbar snackbar = Snackbar.make(Snackbar_layout, "Are you sure to exit Thought Check-in?", Snackbar.LENGTH_LONG)
                .setAction("Exit", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent goBack = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(goBack);
                        finish();
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }
}