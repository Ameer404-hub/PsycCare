package com.example.psyccare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.psyccare.DataAdapters.SliderAdapter;
import com.google.android.material.button.MaterialButton;

public class OnBoarding extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout dotsLayout;
    TextView[] dots;
    MaterialButton letsGo;
    int currPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.indicator);
        letsGo = findViewById(R.id.goBtn);

        letsGo.setVisibility(View.INVISIBLE);

        SliderAdapter sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        addIndicator(0);
        viewPager.addOnPageChangeListener(changeListener);

        letsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }

    public void skipSlide(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    public void nextSlide(View view) {
        viewPager.setCurrentItem(currPos + 1);
    }

    private void addIndicator(int position) {
        dots = new TextView[4];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);

            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.ButtonColorPrimary));
        }
    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addIndicator(position);
            currPos = position;

            if (position == 0) {
                letsGo.setVisibility(View.INVISIBLE);
            } else if (position == 1) {
                letsGo.setVisibility(View.INVISIBLE);
            } else if (position == 2) {
                letsGo.setVisibility(View.INVISIBLE);
            } else if (position == 3) {
                Animation animation = AnimationUtils.loadAnimation(OnBoarding.this, R.anim.slide_anim);
                letsGo.setAnimation(animation);
                letsGo.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}