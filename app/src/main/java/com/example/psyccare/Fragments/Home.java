package com.example.psyccare.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.psyccare.MoodCheckin;
import com.example.psyccare.MyStats;
import com.example.psyccare.OngoingPsycProb;
import com.example.psyccare.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class Home extends Fragment {

    Calendar calendar;
    Date dbDate, currDate;
    SimpleDateFormat dateFormat, timeFormat;
    TextView dateTimeDisplay, checkInHeading, checkInStat;
    String todayDate, todayTime, checkInDate, checkInTime;
    int Hour;
    boolean checkInExist;
    MaterialButton checkInBtn;
    ImageView homeView;
    LinearLayout Symptoms, Explore, Support, Recommended;
    LinearLayout Happy, Sad, Angry, Stressed, Excited;
    DatabaseReference referenceToMoodCheckin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        referenceToMoodCheckin = FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getUid()).child("MoodCheckIns");

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
        timeFormat = new SimpleDateFormat("hh:mm aaa");
        todayDate = dateFormat.format(calendar.getTime());
        todayTime = timeFormat.format(calendar.getTime());
        dateTimeDisplay.setText(todayDate);
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
                checkInLimit();
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

    public void checkInLimit() {
        referenceToMoodCheckin.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        checkInDate = ds.child("checkInDate").getValue().toString().trim();
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
                        try {
                            dbDate = sdf.parse(checkInDate);
                            currDate = sdf.parse(todayDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        checkInTime = ds.child("checkInTime").getValue().toString().trim();
                        Hour = Integer.parseInt(checkInTime.substring(0, 2));
                        String period = checkInTime.substring(6, 8);
                        if (period.equals("PM"))
                            Hour = Hour + 12;

                        if (dbDate.equals(currDate)) {
                            if (Hour >= 0 && Hour < 5) {
                                Toast.makeText(getActivity(), "Night check in already done", Toast.LENGTH_SHORT).show();
                                checkInExist = true;
                                break;
                            } else if (Hour >= 5 && Hour < 12) {
                                Toast.makeText(getActivity(), "Morning check in already done", Toast.LENGTH_SHORT).show();
                                checkInExist = true;
                                break;
                            } else if (Hour >= 12 && Hour < 16) {
                                Toast.makeText(getActivity(), "Afternoon check in already done.", Toast.LENGTH_SHORT).show();
                                checkInExist = true;
                                break;
                            } else if (Hour >= 16 && Hour < 21) {
                                Toast.makeText(getActivity(), "Evening check in already done", Toast.LENGTH_SHORT).show();
                                checkInExist = true;
                                break;
                            } else if (Hour >= 21 && Hour <= 24) {
                                Toast.makeText(getActivity(), "Night check in already done", Toast.LENGTH_SHORT).show();
                                checkInExist = true;
                                break;
                            } else {
                                checkInExist = true;
                                break;
                            }
                        } else {
                            checkInExist = false;
                        }
                    }
                    if (checkInExist == false) {
                        Intent moveTo = new Intent(getActivity(), MoodCheckin.class);
                        startActivity(moveTo);
                        getActivity().finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}