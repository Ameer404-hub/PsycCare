package com.example.psyccare.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.psyccare.MoodCheckin;
import com.example.psyccare.MyStats;
import com.example.psyccare.OngoingPsycProb;
import com.example.psyccare.R;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Home extends Fragment {

    Calendar calendar;
    SimpleDateFormat dateFormat, Time;
    TextView dateTimeDisplay, checkInHeading, checkInStat;
    String date;
    MaterialButton checkInBtn;
    ImageView homeView;
    LinearLayout Symptoms, Explore, Support, Recommended;
    LinearLayout Happy, Sad, Angry, Stressed, Excited;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        checkInBtn = rootView.findViewById(R.id.checkInBtn);
        dateTimeDisplay = rootView.findViewById(R.id.dateTime);
        checkInHeading = rootView.findViewById(R.id.checkInHeading);
        checkInStat = rootView.findViewById(R.id.checkInStatement);
        homeView = rootView.findViewById(R.id.homePicture);
        Symptoms = rootView.findViewById(R.id.symptomsLayout);
        Explore = rootView.findViewById(R.id.exploreLayout);
        Recommended = rootView.findViewById(R.id.recommendLayout);
        Support = rootView.findViewById(R.id.supportLayout);

        Happy = rootView.findViewById(R.id.Happy);
        Sad = rootView.findViewById(R.id.Sad);
        Angry = rootView.findViewById(R.id.Angry);
        Stressed = rootView.findViewById(R.id.Stressed);
        Excited = rootView.findViewById(R.id.Excited);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
        Time = new SimpleDateFormat("hh:mm aaa");
        date = dateFormat.format(calendar.getTime());
        dateTimeDisplay.setText(date);
        Greet();
        setPicture();

        Symptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveTo = new Intent(getActivity(), OngoingPsycProb.class);
                startActivity(moveTo);
            }
        });
        Explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveTo = new Intent(getActivity(), MyStats.class);
                startActivity(moveTo);
            }
        });

        Happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveTo = new Intent(getActivity(), MoodCheckin.class);
                startActivity(moveTo);
                getActivity().finish();
            }
        });
        Sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveTo = new Intent(getActivity(), MoodCheckin.class);
                startActivity(moveTo);
                getActivity().finish();
            }
        });
        Angry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveTo = new Intent(getActivity(), MoodCheckin.class);
                startActivity(moveTo);
                getActivity().finish();
            }
        });
        Stressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveTo = new Intent(getActivity(), MoodCheckin.class);
                startActivity(moveTo);
                getActivity().finish();
            }
        });
        Excited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveTo = new Intent(getActivity(), MoodCheckin.class);
                startActivity(moveTo);
                getActivity().finish();
            }
        });
        checkInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveTo = new Intent(getActivity(), MoodCheckin.class);
                startActivity(moveTo);
                getActivity().finish();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                getActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        return rootView;
    }

    public void Greet() {
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 5) {
            checkInHeading.setText("Mid Night Check In");
            checkInStat.setText("Had a good rest?");
        } else if (timeOfDay >= 5 && timeOfDay < 12) {
            checkInHeading.setText("Morning Check In");
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            checkInHeading.setText("Afternoon Check In");
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            checkInHeading.setText("Evening Check In");
        } else if (timeOfDay >= 21 && timeOfDay <= 24) {
            checkInHeading.setText("Night Check In");
            checkInStat.setText("How was your day?");
        }
    }

    public void setPicture() {
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 24 && timeOfDay < 2) {
            homeView.setImageResource(R.drawable.night_checkin2);
        } else if (timeOfDay >= 2 && timeOfDay < 5) {
            homeView.setImageResource(R.drawable.night_checkin2);
        } else if (timeOfDay >= 5 && timeOfDay < 9) {
            homeView.setImageResource(R.drawable.morning_checkin1);
        } else if (timeOfDay >= 9 && timeOfDay < 12) {
            homeView.setImageResource(R.drawable.morning_checkin2);
        } else if (timeOfDay >= 12 && timeOfDay < 14) {
            homeView.setImageResource(R.drawable.afternoon_checkin1);
        } else if (timeOfDay >= 14 && timeOfDay < 16) {
            homeView.setImageResource(R.drawable.afternoon_checkin2);
        } else if (timeOfDay >= 16 && timeOfDay < 19) {
            homeView.setImageResource(R.drawable.evening_checkin2);
        } else if (timeOfDay >= 19 && timeOfDay < 21) {
            homeView.setImageResource(R.drawable.evening_checkin1);
        } else if (timeOfDay >= 21 && timeOfDay <= 24) {
            homeView.setImageResource(R.drawable.night_checkin1);
        }
    }
}