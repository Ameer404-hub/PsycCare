package com.example.psyccare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class RecommendContent extends AppCompatActivity {

    String titleForActivity, contentForActivity, imageLinkForActivity;
    TextView toolbarTitle, contentView;
    ImageView imageForActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_content);

        titleForActivity = getIntent().getStringExtra("activityTitle");
        contentForActivity = getIntent().getStringExtra("activityContent");
        imageLinkForActivity = getIntent().getStringExtra("activityImage");

        toolbarTitle = findViewById(R.id.toolbar_title);
        contentView = findViewById(R.id.contentView);
        imageForActivity = findViewById(R.id.contentImage);
        toolbarTitle.setText(titleForActivity);
        contentView.setText(contentForActivity);

        if (imageLinkForActivity != "")
            Glide.with(this).load(imageLinkForActivity).into(imageForActivity);
        else {
        }
    }
}