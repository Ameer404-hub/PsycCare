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
import android.widget.TextView;
import android.widget.Toast;

import com.example.psyccare.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tensorflow.lite.examples.textclassification.client.Result;
import org.tensorflow.lite.examples.textclassification.client.TextClassificationClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThoughtTabFragment extends Fragment {

    private static final String TAG = "TextClassificationDemo";
    private TextClassificationClient client;
    private Handler handler;
    private HorizontalBarChart mBarChart;
    private TextView T_DateView, T_ThoughtView, T_DescriptionView;
    String checkInDate, checkInTime, Type, Desc;
    DatabaseReference referenceToThoughtCheckin;
    ProgressDialog messageBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_thought_tab, container, false);

        T_DateView = rootView.findViewById(R.id.dateView);
        T_ThoughtView = rootView.findViewById(R.id.thoughtTitle);
        T_DescriptionView = rootView.findViewById(R.id.thoughtDescription);

        mBarChart = rootView.findViewById(R.id.chart_thoughts);

        messageBox = new ProgressDialog(getActivity());
        messageBox.setTitle("");
        messageBox.setMessage("Loading...");

        referenceToThoughtCheckin = FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getUid()).child("ThoughtCheckIns");

        client = new TextClassificationClient(getActivity());
        handler = new Handler();

        if (isConnected(this)) {
            messageBox.show();
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            referenceToThoughtCheckin.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        checkInDate = snapshot.child("checkInDate").getValue().toString().trim();
                        checkInTime = snapshot.child("checkInTime").getValue().toString().trim();
                        Type = snapshot.child("type").getValue().toString().trim();
                        Desc = snapshot.child("description").getValue().toString().trim();

                        classify(Desc);

                        T_DateView.setText(checkInDate + " " + checkInTime);
                        T_ThoughtView.setText(Type);
                        T_DescriptionView.setText(Desc);
                        messageBox.dismiss();
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    } else {
                        messageBox.dismiss();
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(getActivity(), "Error while fetching Data!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "You're device is not connected to internet", Toast.LENGTH_LONG).show();
        }


        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        handler.post(
                () -> {
                    client.load();
                }
        );
    }

    @Override
    public void onStop(){
        super.onStop();
        handler.post(
                () -> {
                    client.load();
                }
        );
    }

    private void classify(final String text) {
        handler.post(
                () -> {
                    // Run text classification with TF Lite.
                    List<Result> results = client.classify(text);
                    // Show classification result on screen
                    showResult(text, results);
                });
    }

    private void showResult(final String inputText, final List<Result> results) {
        getActivity().runOnUiThread(
                () -> {
                    ArrayList<String> labels = new ArrayList<>();
                    ArrayList<String> BarLabel = new ArrayList<>();
                    ArrayList<Float> probability = new ArrayList<>();
                    ArrayList<BarEntry> barEntries = new ArrayList<>();

                    for (int i = 0; i < results.size(); i++) {
                        Result result = results.get(i);
                        labels.add(result.getTitle());   // Extract labels
                        probability.add(result.getConfidence());  // Extract confidence
                    }

                    float tempProb;
                    String tempLable;
                    for (int i = 0; i < probability.size(); i++) {
                        for (int j = i + 1; j < probability.size(); j++) {
                            if (probability.get(i) > probability.get(j)) {
                                tempProb = probability.get(i);
                                probability.set(i, probability.get(j));
                                probability.set(j, tempProb);

                                tempLable = labels.get(i);
                                labels.set(i, labels.get(j));
                                labels.set(j, tempLable);
                            }
                        }
                    }

                    String HighValueLable = "";
                    Float HighValue = 0.0f;

                    mBarChart.setDrawBarShadow(false);
                    mBarChart.setDrawValueAboveBar(true);
                    mBarChart.getDescription().setEnabled(false);
                    mBarChart.setPinchZoom(false);
                    mBarChart.setDrawGridBackground(false);


                    XAxis xl = mBarChart.getXAxis();
                    xl.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xl.setDrawAxisLine(true);
                    xl.setDrawGridLines(false);
                    xl.setGranularity(1);

                    YAxis yl = mBarChart.getAxisLeft();
                    yl.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                    yl.setDrawGridLines(false);
                    yl.setEnabled(false);
                    yl.setAxisMinimum(0f);

                    YAxis yr = mBarChart.getAxisRight();
                    yr.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                    yr.setDrawGridLines(false);
                    yr.setAxisMinimum(0f);

                    // PREPARING THE ARRAY LIST OF BAR ENTRIES

                    HighValueLable = labels.get(probability.size() - 1);
                    HighValue = probability.get(probability.size() - 1);

                    barEntries.add(new BarEntry(0, HighValue));
                    BarLabel.add(Math.round(HighValue * 1000) / 10.0 + "% " + HighValueLable);
                    barchart(mBarChart, barEntries, BarLabel);

                    HashMap<String, Object> Update = new HashMap<>();
                    Update.put("classifiedAs", HighValueLable + " " + HighValue * 1000 / 10.0 + "%");
                    referenceToThoughtCheckin.updateChildren(Update);
                });
    }

    public static void barchart(BarChart barChart, ArrayList<BarEntry> arrayList, final ArrayList<String> xAxisValues) {
        barChart.setDrawBarShadow(false);
        barChart.setFitBars(true);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(25);
        barChart.setPinchZoom(true);

        barChart.setDrawGridBackground(true);
        BarDataSet barDataSet = new BarDataSet(arrayList, "Class");
        barDataSet.setColors(new int[]{Color.parseColor("#03A9F4"), Color.parseColor("#FF9800"),
                Color.parseColor("#76FF03"), Color.parseColor("#000000"), Color.parseColor("#E91E63"), Color.parseColor("#2962FF")});
        //barDataSet.setColors(new int[]{Color.parseColor("#03A9F4"), Color.parseColor("#FF9800"), Color.parseColor("#76FF03"), Color.parseColor("#E91E63")});
        //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(1f);
        barData.setValueTextSize(0.5f);

        barChart.setBackgroundColor(Color.TRANSPARENT); //set whatever color you prefer
        barChart.setDrawGridBackground(false);
        barChart.animateY(2000);

        //Legend l = barChart.getLegend(); // Customize the ledgends
        //l.setTextSize(10f);
        //l.setFormSize(10f);
//To set components of x axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(16f);
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setEnabled(true);

        /*YAxis yLeft = barChart.getAxisLeft();
        yLeft.setAxisMaximum(100f);
        yLeft.setAxisMinimum(0f);
        yLeft.setEnabled(false);*/

        barChart.setData(barData);

    }

    public void onBackPressed() {
        client.unload();
    }

    private boolean isConnected(ThoughtTabFragment CheckInternet) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiCon != null && wifiCon.isConnected()) || (mobileCon != null && mobileCon.isConnected()))
            return true;
        else
            return false;
    }
}