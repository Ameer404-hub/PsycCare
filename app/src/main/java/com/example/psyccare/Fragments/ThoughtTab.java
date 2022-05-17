package com.example.psyccare.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;

public class ThoughtTab extends Fragment {

    private HorizontalBarChart mBarChart;
    private TextView T_DateView, T_ThoughtView, T_DescriptionView;
    String checkInDate, checkInTime, Type, Desc, classifiedAs;
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

        if (isConnected(this)) {
            messageBox.show();
            messageBox.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            referenceToThoughtCheckin.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            checkInDate = ds.child("checkInDate").getValue().toString().trim();
                            checkInTime = ds.child("checkInTime").getValue().toString().trim();
                            Type = ds.child("type").getValue().toString().trim();
                            Desc = ds.child("description").getValue().toString().trim();
                            classifiedAs = ds.child("classifiedAs").getValue().toString().trim();
                            T_DateView.setText(checkInDate + " " + checkInTime);
                            T_ThoughtView.setText(Type);
                            T_DescriptionView.setText(Desc);
                        }
                        drawChart(classifiedAs);
                        messageBox.dismiss();
                        messageBox.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    } else {
                        messageBox.dismiss();
                        messageBox.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(getActivity(), "Error while fetching Data!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    messageBox.dismiss();
                    messageBox.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "You're device is not connected to internet", Toast.LENGTH_LONG).show();
        }


        return rootView;
    }

    private void drawChart(String mood) {
        String HighValueLable, HighValue;
        HighValue = mood.replaceAll(" ", "");
        HighValue = HighValue.replaceAll("[^\\d.]", "");

        HighValueLable = mood.replaceAll(" ", "");
        HighValueLable = HighValueLable.replaceAll("%", "");
        HighValueLable = HighValueLable.replaceAll("\\.", "");
        HighValueLable = HighValueLable.replaceAll("\\d.","");

        ArrayList<String> BarLabel = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();

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
        barEntries.add(new BarEntry(0, Float.parseFloat(HighValue)));
        BarLabel.add(HighValue + "% " + HighValueLable);
        barchart(mBarChart, barEntries, BarLabel);
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

    private boolean isConnected(ThoughtTab CheckInternet) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiCon != null && wifiCon.isConnected()) || (mobileCon != null && mobileCon.isConnected()))
            return true;
        else
            return false;
    }
}