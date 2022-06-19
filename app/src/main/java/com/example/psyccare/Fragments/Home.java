package com.example.psyccare.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
    SimpleDateFormat dateFormat, timeFormat;
    TextView dateTimeDisplay, checkInHeading, checkInStat;
    String currDate, currTime, checkInDate, checkInTime, dbHeading, monthNode, dateNode, periodcheckIn, periodCurTime;
    int isCheckInTime, timeOfDay, currDateSub, checkInDateSub, checkInExistCount = 0;
    boolean checkInExist;
    MaterialButton checkInBtn;
    ImageView homeView;
    LinearLayout Symptoms, Explore, Support, Recommended;
    LinearLayout Happy, Sad, Angry, Stressed, Excited;
    DatabaseReference referenceToMoodCheckin, referenceToMonthCheckin, referenceToDailyCheckin;
    ProgressDialog messageBox;

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

        messageBox = new ProgressDialog(getActivity());
        messageBox.setTitle("");
        messageBox.setMessage("Loading...");
        messageBox.setCanceledOnTouchOutside(false);

        Happy = rootView.findViewById(R.id.Happy);
        Sad = rootView.findViewById(R.id.Sad);
        Angry = rootView.findViewById(R.id.Angry);
        Stressed = rootView.findViewById(R.id.Stressed);
        Excited = rootView.findViewById(R.id.Excited);

        calendar = Calendar.getInstance();

        dateFormat = new SimpleDateFormat("MMM d, yyyy, EEE");
        timeFormat = new SimpleDateFormat("hh:mm aaa");
        currDate = dateFormat.format(calendar.getTime());
        currTime = timeFormat.format(calendar.getTime());
        dateNode = dateFormat.format(calendar.getTime());
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

        messageBox.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });

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
        messageBox.show();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        referenceToMoodCheckin.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        monthNode = ds.getKey();
                        referenceToMonthCheckin = referenceToMoodCheckin.child(monthNode);
                        referenceToMonthCheckin.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot dsInside : dataSnapshot.getChildren()) {
                                        dateNode = dsInside.getKey();
                                        referenceToDailyCheckin = referenceToMonthCheckin.child(dateNode);
                                        referenceToDailyCheckin.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                if (dataSnapshot1.exists()) {
                                                    for (DataSnapshot dsDaily : dataSnapshot1.getChildren()) {
                                                        if (dsDaily.child("description").exists()) {
                                                            checkInDate = dsDaily.child("checkInDate").getValue().toString().trim();
                                                            checkInTime = dsDaily.child("checkInTime").getValue().toString().trim();
                                                            isCheckInTime = Integer.parseInt(checkInTime.substring(0, 2));
                                                            timeOfDay = Integer.parseInt(currTime.substring(0, 2));
                                                            if (checkInDate.length() > 16)
                                                                checkInDateSub = Integer.parseInt(checkInDate.substring(4, 6));
                                                            else
                                                                checkInDateSub = Integer.parseInt(checkInDate.substring(4, 5));

                                                            if (currDate.length() > 16)
                                                                currDateSub = Integer.parseInt(currDate.substring(4, 6));
                                                            else
                                                                currDateSub = Integer.parseInt(currDate.substring(4, 5));

                                                            periodcheckIn = checkInTime.substring(6, 8);
                                                            periodCurTime = currTime.substring(6, 8);
                                                            if (periodcheckIn.equals("PM"))
                                                                isCheckInTime = isCheckInTime + 12;
                                                            if (periodCurTime.equals("PM"))
                                                                timeOfDay = timeOfDay + 12;

                                                            if (checkInDate.equals(currDate)) {
                                                                if (timeOfDay >= 0 && timeOfDay < 3 && isCheckInTime >= 0 && isCheckInTime < 3) {
                                                                    Toast.makeText(getActivity(), "Mid-Night check in already done\nYou can do next Check In at 3 O'clock", Toast.LENGTH_SHORT).show();
                                                                    checkInExist = true;
                                                                    checkInExistCount++;
                                                                    break;
                                                                } else if (timeOfDay >= 3 && timeOfDay < 6 && isCheckInTime >= 3 && isCheckInTime < 6) {
                                                                    Toast.makeText(getActivity(), "Early Morning check in already done\nYou can do next Check In at 6 O'clock", Toast.LENGTH_SHORT).show();
                                                                    checkInExist = true;
                                                                    checkInExistCount++;
                                                                    break;
                                                                } else if (timeOfDay >= 6 && timeOfDay < 12 && isCheckInTime >= 6 && isCheckInTime < 12) {
                                                                    Toast.makeText(getActivity(), "Morning check in already done\nYou can do next Check In at 12 O'clock", Toast.LENGTH_SHORT).show();
                                                                    checkInExist = true;
                                                                    checkInExistCount++;
                                                                    break;
                                                                } else if (timeOfDay >= 12 && timeOfDay < 16 && isCheckInTime >= 12 && isCheckInTime < 16) {
                                                                    Toast.makeText(getActivity(), "Afternoon check in already done\nYou can do next Check In at 4 O'clock", Toast.LENGTH_SHORT).show();
                                                                    checkInExist = true;
                                                                    checkInExistCount++;
                                                                    break;
                                                                } else if (timeOfDay >= 16 && timeOfDay < 21 && isCheckInTime >= 16 && isCheckInTime < 21) {
                                                                    Toast.makeText(getActivity(), "Evening check in already done\nYou can do next Check In at 9 O'clock", Toast.LENGTH_SHORT).show();
                                                                    checkInExist = true;
                                                                    checkInExistCount++;
                                                                    break;
                                                                } else if (timeOfDay >= 21 && timeOfDay < 24 && isCheckInTime >= 21 && isCheckInTime < 24) {
                                                                    Toast.makeText(getActivity(), "Night check in already done\nYou can do next Check In at 12 O'clock", Toast.LENGTH_SHORT).show();
                                                                    checkInExist = true;
                                                                    checkInExistCount++;
                                                                    break;
                                                                } else
                                                                    checkInExist = false;
                                                            } else {
                                                                if(checkInExistCount == 1){
                                                                    if (currDateSub > checkInDateSub)
                                                                        checkInExist = true;
                                                                    else
                                                                        checkInExist = false;
                                                                }
                                                                checkInExist = true;
                                                            }
                                                        }
                                                    }
                                                    if (checkInExist == true) {
                                                        messageBox.dismiss();
                                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                    } else if (checkInExist == false) {
                                                        dbHeading = checkInHeading.getText().toString();
                                                        Intent moveTo = new Intent(getActivity(), MoodCheckin.class);
                                                        moveTo.putExtra("checkInHeading", dbHeading);
                                                        startActivity(moveTo);
                                                        getActivity().finish();
                                                        messageBox.dismiss();
                                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                    }
                                                } else {
                                                    dbHeading = checkInHeading.getText().toString();
                                                    Intent moveTo = new Intent(getActivity(), MoodCheckin.class);
                                                    moveTo.putExtra("checkInHeading", dbHeading);
                                                    startActivity(moveTo);
                                                    getActivity().finish();
                                                    messageBox.dismiss();
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                messageBox.dismiss();
                                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                Toast.makeText(getActivity(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } else {
                                    dbHeading = checkInHeading.getText().toString();
                                    Intent moveTo = new Intent(getActivity(), MoodCheckin.class);
                                    moveTo.putExtra("checkInHeading", dbHeading);
                                    startActivity(moveTo);
                                    getActivity().finish();
                                    messageBox.dismiss();
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                messageBox.dismiss();
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(getActivity(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    dbHeading = checkInHeading.getText().toString();
                    Intent moveTo = new Intent(getActivity(), MoodCheckin.class);
                    moveTo.putExtra("checkInHeading", dbHeading);
                    startActivity(moveTo);
                    getActivity().finish();
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                messageBox.dismiss();
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}