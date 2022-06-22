package com.example.psyccare;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.psyccare.DataAdapters.DiscoverAdapter;
import com.example.psyccare.DataAdapters.RecommendationsAdapter;
import com.example.psyccare.DataModels.DiscoverDataModel;
import com.example.psyccare.Fragments.Discover;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class Recommendations extends AppCompatActivity {

    RecyclerView recommendationView1, recommendationView2, recommendationView3, recommendationView4, recommendationView5;
    DatabaseReference referenceToRecommendations, referenceToDiscover;
    RecommendationsAdapter adapterView1, adapterView2, adapterView3, adapterView4, adapterView5;
    ArrayList<DiscoverDataModel> recommendData1, recommendData2, recommendData3, recommendData4, recommendData5;
    ProgressDialog messageBox;
    RelativeLayout recommendLayout1, recommendLayout2, recommendLayout3, recommendLayout4, recommendLayout5;
    TextView headingSect1, headingSect2, headingSect3, headingSect4, headingSect5;
    String tempLabel = "", topMoodSymptoms1, topMoodSymptoms2, topMoodSymptoms3, topThoughtSymptoms1, topThoughtSymptoms2;
    String[] labelMoodsdata, labelThoughtsData;
    int valMoodsData[], valThoughtsData[], tempVal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        recommendLayout1 = findViewById(R.id.recommendSec1);
        recommendLayout2 = findViewById(R.id.recommendSec2);
        recommendLayout3 = findViewById(R.id.recommendSec3);
        recommendLayout4 = findViewById(R.id.recommendSec4);
        recommendLayout5 = findViewById(R.id.recommendSec5);

        recommendationView1 = findViewById(R.id.recommendSec1View);
        recommendationView2 = findViewById(R.id.recommendSec2View);
        recommendationView3 = findViewById(R.id.recommendSec3View);
        recommendationView4 = findViewById(R.id.recommendSec4View);
        recommendationView5 = findViewById(R.id.recommendSec5View);

        headingSect1 = findViewById(R.id.headingSec1);
        headingSect2 = findViewById(R.id.headingSec2);
        headingSect3 = findViewById(R.id.headingSec3);
        headingSect4 = findViewById(R.id.headingSec4);
        headingSect5 = findViewById(R.id.headingSec5);

        recommendLayout1.setVisibility(View.GONE);
        recommendLayout2.setVisibility(View.GONE);
        recommendLayout3.setVisibility(View.GONE);
        recommendLayout4.setVisibility(View.GONE);
        recommendLayout5.setVisibility(View.GONE);

        recommendData1 = new ArrayList<>();
        recommendData2 = new ArrayList<>();
        recommendData3 = new ArrayList<>();
        recommendData4 = new ArrayList<>();
        recommendData5 = new ArrayList<>();

        adapterView1 = new RecommendationsAdapter(this, recommendData1);
        adapterView2 = new RecommendationsAdapter(this, recommendData2);
        adapterView3 = new RecommendationsAdapter(this, recommendData3);
        adapterView4 = new RecommendationsAdapter(this, recommendData4);
        adapterView5 = new RecommendationsAdapter(this, recommendData5);

        recommendationView1.setHasFixedSize(true);
        recommendationView2.setHasFixedSize(true);
        recommendationView3.setHasFixedSize(true);
        recommendationView4.setHasFixedSize(true);
        recommendationView5.setHasFixedSize(true);
        recommendationView1.setLayoutManager(new LinearLayoutManager(this));
        recommendationView2.setLayoutManager(new LinearLayoutManager(this));
        recommendationView3.setLayoutManager(new LinearLayoutManager(this));
        recommendationView4.setLayoutManager(new LinearLayoutManager(this));
        recommendationView5.setLayoutManager(new LinearLayoutManager(this));

        recommendationView1.setAdapter(adapterView1);
        recommendationView2.setAdapter(adapterView2);
        recommendationView3.setAdapter(adapterView3);
        recommendationView4.setAdapter(adapterView4);
        recommendationView5.setAdapter(adapterView5);

        valMoodsData = new int[5];
        valThoughtsData = new int[4];
        labelMoodsdata = new String[5];
        labelThoughtsData = new String[4];

        referenceToRecommendations = FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getUid()).child("RecommendationsData");

        referenceToDiscover = FirebaseDatabase.getInstance().getReference("DiscoverData");

        messageBox = new ProgressDialog(this);
        messageBox.setTitle("");
        messageBox.setMessage("Loading...");
        messageBox.setCanceledOnTouchOutside(false);

        if (isConnected(this)) {
            messageBox.show();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            getRecommendationsData();
        } else
            Toast.makeText(this, "You're device is not connected to internet", Toast.LENGTH_LONG).show();

        messageBox.setOnCancelListener(dialog -> getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE));
    }

    private void getRecommendationsData() {
        referenceToRecommendations.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("MoodsData").exists()){
                        labelMoodsdata[0] = dataSnapshot.child("MoodsData").child("Cheerful").getKey();
                        labelMoodsdata[1] = dataSnapshot.child("MoodsData").child("Anxiety").getKey();
                        labelMoodsdata[2] = dataSnapshot.child("MoodsData").child("Depression").getKey();
                        labelMoodsdata[3] = dataSnapshot.child("MoodsData").child("Stress").getKey();
                        labelMoodsdata[4] = dataSnapshot.child("MoodsData").child("Peaceful").getKey();

                        valMoodsData[0] = Integer.parseInt(dataSnapshot.child("MoodsData").child("Cheerful").getValue().toString());
                        valMoodsData[1] = Integer.parseInt(dataSnapshot.child("MoodsData").child("Anxiety").getValue().toString());
                        valMoodsData[2] = Integer.parseInt(dataSnapshot.child("MoodsData").child("Depression").getValue().toString());
                        valMoodsData[3] = Integer.parseInt(dataSnapshot.child("MoodsData").child("Stress").getValue().toString());
                        valMoodsData[4] = Integer.parseInt(dataSnapshot.child("MoodsData").child("Peaceful").getValue().toString());

                    }
                    if(dataSnapshot.child("ThoughtsData").exists()) {
                        labelThoughtsData[0] = dataSnapshot.child("ThoughtsData").child("Positive").getKey();
                        labelThoughtsData[1] = dataSnapshot.child("ThoughtsData").child("Confident").getKey();
                        labelThoughtsData[2] = dataSnapshot.child("ThoughtsData").child("Distrust").getKey();
                        labelThoughtsData[3] = dataSnapshot.child("ThoughtsData").child("Negative").getKey();

                        valThoughtsData[0] = Integer.parseInt(dataSnapshot.child("ThoughtsData").child("Positive").getValue().toString());
                        valThoughtsData[1] = Integer.parseInt(dataSnapshot.child("ThoughtsData").child("Confident").getValue().toString());
                        valThoughtsData[2] = Integer.parseInt(dataSnapshot.child("ThoughtsData").child("Distrust").getValue().toString());
                        valThoughtsData[3] = Integer.parseInt(dataSnapshot.child("ThoughtsData").child("Negative").getValue().toString());
                    }
                    for (int i = 0; i < valMoodsData.length; i++) {
                        for (int j = i + 1; j < valMoodsData.length; j++) {
                            if (valMoodsData[i] >= valMoodsData[j]) {
                                tempVal = valMoodsData[i];
                                valMoodsData[i] = valMoodsData[j];
                                valMoodsData[j] = tempVal;

                                tempLabel = labelMoodsdata[i];
                                labelMoodsdata[i] = labelMoodsdata[j];
                                labelMoodsdata[j] = tempLabel;
                            }
                        }
                    }
                    for (int i = 0; i < valThoughtsData.length; i++) {
                        for (int j = i + 1; j < valThoughtsData.length; j++) {
                            if (valThoughtsData[i] >= valThoughtsData[j]) {
                                tempVal = valThoughtsData[i];
                                valThoughtsData[i] = valThoughtsData[j];
                                valThoughtsData[j] = tempVal;

                                tempLabel = labelThoughtsData[i];
                                labelThoughtsData[i] = labelThoughtsData[j];
                                labelThoughtsData[j] = tempLabel;
                            }
                        }
                    }
                    topMoodSymptoms1 = labelMoodsdata[4];
                    topMoodSymptoms2 = labelMoodsdata[3];
                    topMoodSymptoms3 = labelMoodsdata[2];
                    topThoughtSymptoms1 = labelThoughtsData[3];
                    topThoughtSymptoms2 = labelThoughtsData[2];
                    generateRecommendations(topMoodSymptoms1, topMoodSymptoms2, topMoodSymptoms3, topThoughtSymptoms1, topThoughtSymptoms2);
                } else {
                    messageBox.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getApplicationContext(), "getRecommendationsData() error: "+dataSnapshot.getKey()+" Node does not exist!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                messageBox.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateRecommendations(String moodSymp1, String moodSymp2, String moodSymp3, String thoughtSymp1, String thoughtSymp2){
        headingSect1.setText("Feeling "+moodSymp1+" lately! Let's explore more about it");
        referenceToDiscover.child(moodSymp1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        DiscoverDataModel newModel = ds.getValue(DiscoverDataModel.class);
                        recommendData1.add(newModel);
                        recommendLayout1.setVisibility(View.VISIBLE);
                    }
                    messageBox.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    messageBox.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getApplicationContext(), "generateRecommendations() error: Node " + dataSnapshot.getKey() + " does not exist!!!", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                messageBox.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        headingSect2.setText("Recommendations based on "+moodSymp2);
        referenceToDiscover.child(moodSymp2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                if(dataSnapshot1.exists()){
                    for (DataSnapshot ds : dataSnapshot1.getChildren()) {
                        DiscoverDataModel newModel2 = ds.getValue(DiscoverDataModel.class);
                        recommendData2.add(newModel2);
                        recommendLayout2.setVisibility(View.VISIBLE);
                    }
                    messageBox.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    messageBox.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getApplicationContext(), "generateRecommendations() error: Node " + dataSnapshot1.getKey() + " does not exist!!!", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError1) {
                messageBox.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getApplicationContext(), databaseError1.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        headingSect3.setText("Your stats also reflect "+moodSymp3+ " symptoms");
        referenceToDiscover.child(moodSymp3).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                if(dataSnapshot2.exists()){
                    for (DataSnapshot ds : dataSnapshot2.getChildren()) {
                        DiscoverDataModel newModel3 = ds.getValue(DiscoverDataModel.class);
                        recommendData3.add(newModel3);
                        recommendLayout3.setVisibility(View.VISIBLE);
                    }
                    messageBox.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    messageBox.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getApplicationContext(), "generateRecommendations() error: Node " + dataSnapshot2.getKey() + " does not exist!!!", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError2) {
                messageBox.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getApplicationContext(), databaseError2.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        headingSect4.setText("Recommendations based on "+thoughtSymp1);
        referenceToDiscover.child(thoughtSymp1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                if(dataSnapshot3.exists()){
                    for (DataSnapshot ds : dataSnapshot3.getChildren()) {
                        DiscoverDataModel newModel4 = ds.getValue(DiscoverDataModel.class);
                        recommendData4.add(newModel4);
                        recommendLayout4.setVisibility(View.VISIBLE);
                    }
                    messageBox.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    messageBox.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getApplicationContext(), "generateRecommendations() error: Node " + dataSnapshot3.getKey() + " does not exist!!!", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError3) {
                messageBox.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getApplicationContext(), databaseError3.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        headingSect5.setText("Let's learn a bit about "+thoughtSymp2+ " as well!");
        referenceToDiscover.child(thoughtSymp2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot4) {
                if(dataSnapshot4.exists()){
                    for (DataSnapshot ds : dataSnapshot4.getChildren()) {
                        DiscoverDataModel newModel5 = ds.getValue(DiscoverDataModel.class);
                        recommendData5.add(newModel5);
                        recommendLayout5.setVisibility(View.VISIBLE);
                    }
                    messageBox.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    messageBox.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getApplicationContext(), "generateRecommendations() error: Node " + dataSnapshot4.getKey() + " does not exist!!!", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError4) {
                messageBox.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getApplicationContext(), databaseError4.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isConnected(Recommendations CheckInternet) {
        ConnectivityManager connectivityManager = (ConnectivityManager) CheckInternet.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiCon != null && wifiCon.isConnected()) || (mobileCon != null && mobileCon.isConnected()))
            return true;
        else
            return false;
    }
}