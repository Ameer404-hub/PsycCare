package com.example.psyccare.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.psyccare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MoodTabFragment extends Fragment {

    private TextView M_DateView, M_MoodView, M_DescriptionView;
    String checkInDate, checkInTime, Type, Desc;
    DatabaseReference referenceToMoodCheckin;
    ProgressDialog messageBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mood_tab, container, false);

        M_DateView = rootView.findViewById(R.id.dateView);
        M_MoodView = rootView.findViewById(R.id.moodTitle);
        M_DescriptionView = rootView.findViewById(R.id.moodDescription);

        messageBox = new ProgressDialog(getActivity());
        messageBox.setTitle("");
        messageBox.setMessage("Loading...");

        referenceToMoodCheckin = FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getUid()).child("MoodCheckIns");

        if (isConnected(this)) {
            messageBox.show();
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            referenceToMoodCheckin.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        checkInDate = snapshot.child("checkInDate").getValue().toString().trim();
                        checkInTime = snapshot.child("checkInTime").getValue().toString().trim();
                        Type = snapshot.child("type").getValue().toString().trim();
                        Desc = snapshot.child("description").getValue().toString().trim();

                        M_DateView.setText(checkInDate + " " + checkInTime);
                        M_MoodView.setText(Type);
                        M_DescriptionView.setText(Desc);
                        messageBox.dismiss();
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    } else {
                        Toast.makeText(getActivity(), "Error while fetching Data!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "You're device is not connected to internet", Toast.LENGTH_LONG).show();
        }
        return rootView;
    }

    private boolean isConnected(MoodTabFragment CheckInternet) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiCon != null && wifiCon.isConnected()) || (mobileCon != null && mobileCon.isConnected()))
            return true;
        else
            return false;
    }
}