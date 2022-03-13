package com.example.psyccare.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.psyccare.MoodCheckin;
import com.example.psyccare.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    Calendar calendar;
    SimpleDateFormat dateFormat, Time;
    TextView Greetings, dateTimeDisplay;
    String date;
    Button checkInBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        Greetings = rootView.findViewById(R.id.welcome);
        checkInBtn = rootView.findViewById(R.id.checkInBtn);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
        Time = new SimpleDateFormat("hh:mm aaa");
        date = dateFormat.format(calendar.getTime());
        dateTimeDisplay = (TextView)rootView.findViewById(R.id.dateTime);
        dateTimeDisplay.setText(date);
        /*Greet();*/
        CheckIn();
        checkInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveTo = new Intent(getActivity(), MoodCheckin.class);
                startActivity(moveTo);
                getActivity().finish();
            }
        });

        return rootView;
    }

   /* public void Greet(){
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if(timeOfDay >= 0 && timeOfDay < 12){
            Greetings.setText("Good Morning");
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            Greetings.setText("Good Afternoon");
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            Greetings.setText("Good Evening");
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            Greetings.setText("Good Night");
        }
    }*/

    public void CheckIn(){
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if(timeOfDay >= 0 && timeOfDay < 12){
            checkInBtn.setText("Let's do Morning Check-in");
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            checkInBtn.setText("Let's do Afternoon Check-in");
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            checkInBtn.setText("Let's do Evening Check-in");
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            checkInBtn.setText("Let's do Night Check-in");
        }
    }
}