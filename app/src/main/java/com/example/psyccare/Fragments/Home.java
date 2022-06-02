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
    ;
    SimpleDateFormat dateFormat, timeFormat;
    TextView dateTimeDisplay, checkInHeading, checkInStat;
    String currDate, currTime, checkInDate, checkInTime;
    int isCheckInTime, timeOfDay;
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
        currDate = dateFormat.format(calendar.getTime());
        currTime = timeFormat.format(calendar.getTime());
        dateTimeDisplay.setText(currDate);
        Greet();

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
                checkInLimit();
            }
        });
        Sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInLimit();
            }
        });
        Angry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInLimit();
            }
        });
        Stressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInLimit();
            }
        });
        Excited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInLimit();
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
        timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 3) {
            homeView.setImageResource(R.drawable.night_checkin2);
            checkInHeading.setText("Mid-night Check In");
            checkInStat.setText("Trying to sleep?");
        } else if (timeOfDay >= 3 && timeOfDay < 6) {
            homeView.setImageResource(R.drawable.night_checkin2);
            checkInHeading.setText("Early Morning Check In");
            checkInStat.setText("Had a good rest?");
        } else if (timeOfDay >= 6 && timeOfDay < 9) {
            homeView.setImageResource(R.drawable.morning_checkin1);
            checkInHeading.setText("Morning Check In");
            checkInStat.setText("Had a good rest?");
        } else if (timeOfDay >= 9 && timeOfDay < 12) {
            homeView.setImageResource(R.drawable.morning_checkin2);
            checkInHeading.setText("Morning Check In");
        } else if (timeOfDay >= 12 && timeOfDay < 14) {
            homeView.setImageResource(R.drawable.afternoon_checkin1);
            checkInHeading.setText("Afternoon Check In");
        } else if (timeOfDay >= 14 && timeOfDay < 16) {
            homeView.setImageResource(R.drawable.afternoon_checkin2);
            checkInHeading.setText("Afternoon Check In");
        } else if (timeOfDay >= 16 && timeOfDay < 19) {
            homeView.setImageResource(R.drawable.evening_checkin2);
            checkInHeading.setText("Evening Check In");
        } else if (timeOfDay >= 19 && timeOfDay < 21) {
            homeView.setImageResource(R.drawable.evening_checkin1);
            checkInHeading.setText("Evening Check In");
        } else if (timeOfDay >= 21 && timeOfDay <= 24) {
            homeView.setImageResource(R.drawable.night_checkin1);
            checkInHeading.setText("Night Check In");
            checkInStat.setText("How was your day?");
        }
    }

    public void checkInLimit() {
        referenceToMoodCheckin.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        checkInDate = ds.child("checkInDate").getValue().toString().trim();
                        checkInTime = ds.child("checkInTime").getValue().toString().trim();
                        isCheckInTime = Integer.parseInt(checkInTime.substring(0, 2));
                        timeOfDay = Integer.parseInt(currTime.substring(0, 2));
                        String periodHour = checkInTime.substring(6, 8);
                        String periodCurTime = checkInTime.substring(6, 8);
                        if (periodHour.equals("PM"))
                            isCheckInTime = isCheckInTime + 12;
                        if (periodCurTime.equals("PM"))
                            timeOfDay = timeOfDay + 12;

                        if (checkInDate.equals(currDate)) {
                            if (timeOfDay >= 0 && timeOfDay < 3 && isCheckInTime >= 0 && isCheckInTime < 3) {
                                Toast.makeText(getActivity(), "Mid-Night check in already done", Toast.LENGTH_SHORT).show();
                                checkInExist = true;
                                break;
                            } else if (timeOfDay >= 3 && timeOfDay < 6 && isCheckInTime >= 3 && isCheckInTime < 6) {
                                Toast.makeText(getActivity(), "Early Morning check in already done", Toast.LENGTH_SHORT).show();
                                checkInExist = true;
                                break;
                            } else if (timeOfDay >= 6 && timeOfDay < 12 && isCheckInTime >= 6 && isCheckInTime < 12) {
                                Toast.makeText(getActivity(), "Morning check in already done", Toast.LENGTH_SHORT).show();
                                checkInExist = true;
                                break;
                            } else if (timeOfDay >= 12 && timeOfDay < 16 && isCheckInTime >= 12 && isCheckInTime < 16) {
                                Toast.makeText(getActivity(), "Afternoon check in already done.", Toast.LENGTH_SHORT).show();
                                checkInExist = true;
                                break;
                            } else if (timeOfDay >= 16 && timeOfDay < 21 && isCheckInTime >= 16 && isCheckInTime < 21) {
                                Toast.makeText(getActivity(), "Evening check in already done", Toast.LENGTH_SHORT).show();
                                checkInExist = true;
                                break;
                            } else if (timeOfDay >= 21 && timeOfDay < 24 && isCheckInTime >= 21 && isCheckInTime < 24) {
                                Toast.makeText(getActivity(), "Night check in already done", Toast.LENGTH_SHORT).show();
                                checkInExist = true;
                                break;
                            } else
                                checkInExist = false;
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