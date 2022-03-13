package com.example.psyccare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;

public class MoodCheckin extends AppCompatActivity {

    ImageView submitMood;
    LinearLayout Snackbar_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_checkin);

        submitMood = findViewById(R.id.sendMoodNote);
        Snackbar_layout = findViewById(R.id.moodIconsLayout);

        submitMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveTo = new Intent(getApplicationContext(), ThoughtCheckin.class);
                startActivity(moveTo);
                finish();
            }
        });
    }

    public void onBackPressed() {
        Snackbar snackbar = Snackbar.make(Snackbar_layout, "Are you sure to exit Mood Check-in?", Snackbar.LENGTH_LONG)
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