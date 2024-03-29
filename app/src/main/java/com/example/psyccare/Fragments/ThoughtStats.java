package com.example.psyccare.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.psyccare.DataAdapters.ThoughtStatsContentAdapter;
import com.example.psyccare.DataModels.checkInModel;
import com.example.psyccare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ThoughtStats extends Fragment {

    RecyclerView recyclerViewForThoughtstats;
    DatabaseReference referenceToThoughtCheckin, referenceToMonthCheckin, referenceToDailyCheckin;
    ThoughtStatsContentAdapter adapterThoughtStats;
    ArrayList<checkInModel> tCheckIn;
    ProgressDialog messageBox;
    LinearLayout tStatsLayout;
    String monthNode, dateNode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_thought_stats, container, false);

        recyclerViewForThoughtstats = rootView.findViewById(R.id.thoughtStatsView);
        tStatsLayout = rootView.findViewById(R.id.thoughtCheckinLayout);

        referenceToThoughtCheckin = FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getUid()).child("ThoughtCheckIns");

        messageBox = new ProgressDialog(getActivity());
        messageBox.setTitle("");
        messageBox.setMessage("Loading...");
        messageBox.setCanceledOnTouchOutside(false);

        tCheckIn = new ArrayList<>();
        adapterThoughtStats = new ThoughtStatsContentAdapter(getActivity(), tCheckIn);
        recyclerViewForThoughtstats.setHasFixedSize(true);
        recyclerViewForThoughtstats.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewForThoughtstats.setAdapter(adapterThoughtStats);

        if (isConnected(this)) {
            messageBox.show();
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            getThoughtCheckins();
        } else {
            Toast.makeText(getActivity(), "You're device is not connected to internet", Toast.LENGTH_LONG).show();
        }
        messageBox.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
        return rootView;
    }

    private void getThoughtCheckins() {
        referenceToThoughtCheckin.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        monthNode = ds.getKey();
                        referenceToMonthCheckin = referenceToThoughtCheckin.child(monthNode);
                        referenceToMonthCheckin.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot dsMonthNode : dataSnapshot.getChildren()) {
                                        dateNode = dsMonthNode.getKey();
                                        referenceToDailyCheckin = referenceToMonthCheckin.child(dateNode);
                                        referenceToDailyCheckin.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                if (dataSnapshot1.exists()) {
                                                    for (DataSnapshot dsDaily : dataSnapshot1.getChildren()) {
                                                        if (dsDaily.child("description").exists()) {
                                                            checkInModel thoughtModel = dsDaily.getValue(checkInModel.class);
                                                            tCheckIn.add(thoughtModel);
                                                            LayoutAnimationController layoutAnimationController =
                                                                    AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_upwards);
                                                            tStatsLayout.setLayoutAnimation(layoutAnimationController);
                                                        }
                                                    }
                                                    messageBox.dismiss();
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                } else {
                                                    messageBox.dismiss();
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                    Toast.makeText(getActivity(), "getThoughtCheckins() error: Date Node does not exist!!!\nYou don't have any Mood Check Ins yet!", Toast.LENGTH_SHORT).show();
                                                }
                                                adapterThoughtStats.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError1) {
                                                messageBox.dismiss();
                                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                Toast.makeText(getActivity(), "Error: " + databaseError1.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                } else {
                                    messageBox.dismiss();
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Toast.makeText(getActivity(), "getThoughtCheckins() error: Date Node does not exist!!!\nYou don't have any Mood Check Ins yet!", Toast.LENGTH_SHORT).show();
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
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), "getThoughtCheckins() error: DataSnapShot does not exist!!!\nYou don't have any Thought Check Ins yet", Toast.LENGTH_SHORT).show();
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

    private boolean isConnected(ThoughtStats CheckInternet) {
        ConnectivityManager connectivityManager = (ConnectivityManager) CheckInternet.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiCon != null && wifiCon.isConnected()) || (mobileCon != null && mobileCon.isConnected()))
            return true;
        else
            return false;
    }
}