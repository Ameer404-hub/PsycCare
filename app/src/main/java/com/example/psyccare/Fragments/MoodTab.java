package com.example.psyccare.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.psyccare.Login;
import com.example.psyccare.R;
import com.example.psyccare.Signup;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MoodTab extends Fragment {

    String classifiedAs, perCent;
    DatabaseReference referenceToMoodCheckin, referenceToMonthCheckin, referenceToDailyCheckin;
    ProgressDialog messageBox;
    LineChart lineChart;
    PieChart pieChart;
    String monthNode, dateNode, HighValueLable, HighValue, val, finalVal;
    int count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mood_tab, container, false);

        pieChart = rootView.findViewById(R.id.mPieChart);
        lineChart = rootView.findViewById(R.id.mLineChart);

        messageBox = new ProgressDialog(getActivity());
        messageBox.setTitle("");
        messageBox.setMessage("Loading...");
        messageBox.setCanceledOnTouchOutside(false);

        referenceToMoodCheckin = FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getUid()).child("MoodCheckIns");

        if (isConnected(this)) {
            messageBox.show();
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            setupLineChart();
            /*setupPieChart();*/
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

    private boolean isConnected(MoodTab CheckInternet) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiCon != null && wifiCon.isConnected()) || (mobileCon != null && mobileCon.isConnected()))
            return true;
        else
            return false;
    }

    private void setupLineChart() {
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setTouchEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setPinchZoom(true);

        Description description = new Description();
        description.setText("Daily Check ins");
        description.setTextSize(10);
        lineChart.setDescription(description);

        Legend l = lineChart.getLegend();
        l.setEnabled(false);

        ArrayList<Entry> entries = new ArrayList<>();
        String[] Days = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setValueFormatter(new MyAxixValueFormatter(Days));
        xAxis.setGranularity(1);

        YAxis yAxisLeft = lineChart.getAxisLeft();
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);
        /*yAxisLeft.setDrawGridLines(false);
        yAxisRight.setDrawGridLines(false);*/

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
                                if(dataSnapshot.exists()) {
                                    for(DataSnapshot dsMonthNode : dataSnapshot.getChildren()) {
                                        dateNode = dsMonthNode.getKey();
                                        referenceToDailyCheckin = referenceToMonthCheckin.child(dateNode);
                                        referenceToDailyCheckin.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                if (dataSnapshot1.exists()) {
                                                    for (DataSnapshot dsDaily : dataSnapshot1.getChildren()) {
                                                        if (dsDaily.child("description").exists()) {
                                                            classifiedAs = dsDaily.child("classifiedAs").getValue().toString().trim();
                                                            perCent = dsDaily.child("perCent").getValue().toString().trim();
                                                            if (!classifiedAs.equals("") && !perCent.equals("")) {
                                                                HighValue = perCent.replaceAll("[^\\d.]", "");
                                                                HighValueLable = classifiedAs.replaceAll(" ", "");
                                                                entries.add(new Entry(count++, Float.parseFloat(HighValue), HighValueLable));
                                                            }
                                                        }
                                                    }
                                                    messageBox.dismiss();
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                    ArrayList<Integer> colors = new ArrayList<>();
                                                    for (int color : ColorTemplate.MATERIAL_COLORS) {
                                                        colors.add(color);
                                                    }
                                                    for (int color : ColorTemplate.VORDIPLOM_COLORS) {
                                                        colors.add(color);
                                                    }

                                                    LineDataSet lineSet = new LineDataSet(entries, "");
                                                    lineSet.setColors(ColorTemplate.getHoloBlue());
                                                    lineSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                                                    lineSet.setLineWidth(2f);
                                                    lineSet.setDrawCircles(true);
                                                    lineSet.setDrawCircleHole(true);
                                                    lineSet.setCubicIntensity(0.2f);
                                                    lineSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                                                    lineSet.setCircleColor(ColorTemplate.getHoloBlue());
                                                    lineSet.setCircleRadius(4f);
                                                    lineSet.setFillAlpha(65);
                                                    lineSet.setDrawValues(true);
                                                    lineSet.setFillColor(ColorTemplate.getHoloBlue());
                                                    lineSet.setCircleRadius(6);
                                                    lineSet.setCircleHoleRadius(3);

                                                    ArrayList<ILineDataSet> dataSet = new ArrayList<>();
                                                    dataSet.add(lineSet);
                                                    LineData data = new LineData(dataSet);
                                                    lineChart.setData(data);
                                                    lineChart.animateX(1400, Easing.EasingOption.EaseInOutQuad);
                                                    lineChart.invalidate();

                                                    lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                                                        @Override
                                                        public void onValueSelected(Entry e, Highlight h) {
                                                            messageBox.show();
                                                            val = e.toString();
                                                            val = val.substring(17, 21);
                                                            finalVal = val;
                                                            referenceToDailyCheckin.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                                                    if(dataSnapshot2.exists()) {
                                                                        for(DataSnapshot dsTap : dataSnapshot2.getChildren()) {
                                                                            if(dsTap.child("description").exists()){
                                                                                classifiedAs = dsTap.child("classifiedAs").getValue().toString().trim();
                                                                                perCent = dsTap.child("perCent").getValue().toString().trim();
                                                                                if (!classifiedAs.equals("") && !perCent.equals("")) {
                                                                                    HighValue = perCent.replaceAll("[^\\d.]", "");
                                                                                    HighValueLable = classifiedAs;
                                                                                    if (finalVal.equals(HighValue)) {
                                                                                        HighValueLable = HighValueLable.toUpperCase();
                                                                                        AlertDialog.Builder showMessage = new AlertDialog.Builder(getActivity());
                                                                                        showMessage.setMessage("").setTitle("Check In Details");
                                                                                        showMessage.setMessage("Mood: " + HighValueLable).setCancelable(false).
                                                                                                setPositiveButton("Hide", new DialogInterface.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                                                    }
                                                                                                });
                                                                                        AlertDialog alert = showMessage.create();
                                                                                        messageBox.dismiss();
                                                                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                                        alert.show();
                                                                                        break;
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    } else {
                                                                        messageBox.dismiss();
                                                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                        Toast.makeText(getActivity(), "setupLineChart() error: Date Node does not exist!!!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError1) {
                                                                    messageBox.dismiss();
                                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                    Toast.makeText(getActivity(), "Error: " + databaseError1.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onNothingSelected() {

                                                        }
                                                    });

                                                } else {
                                                    messageBox.dismiss();
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                    Toast.makeText(getActivity(), "setupLineChart() error: Date Node does not exist!!!", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getActivity(), "setupLineChart() error: Date Node does not exist!!!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "setupLineChart() error: MoodCheckin Node  does not exist!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(10);
        pieChart.setRotationEnabled(true);
        pieChart.setRotationAngle(0);
        pieChart.setEntryLabelTextSize(10);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("");
        pieChart.setCenterTextSize(12);

        Description description = new Description();
        description.setText("Weekly Progress");
        description.setTextSize(10);
        pieChart.setDescription(description);

        Legend l = pieChart.getLegend();
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setEnabled(false);
        l.setTextSize(12);

        ArrayList<PieEntry> entries = new ArrayList<>();

        referenceToMoodCheckin.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                     /*float[] ranker = new float[10];
                int count = 0;*/
                    String HighValueLable, HighValue;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        classifiedAs = ds.child("classifiedAs").getValue().toString().trim();
                        perCent = ds.child("perCent").getValue().toString().trim();
                        if (!classifiedAs.equals("") && !perCent.equals("")) {
                            HighValue = perCent.replaceAll("[^\\d.]", "");
                            HighValueLable = classifiedAs.replaceAll(" ", "");
                            entries.add(new PieEntry(Float.parseFloat(HighValue), HighValueLable));
                        /*ranker[count] = Float.parseFloat(HighValue);
                        count++;*/
                        }
                    }

               /* float first, second, third;
                third = first = second = Integer.MIN_VALUE;
                for (int i = 0; i < ranker.length; i++) {

                    if (ranker[i] > first) {
                        third = second;
                        second = first;
                        first = ranker[i];
                    }
                    else if (ranker[i] > second) {
                        third = second;
                        second = ranker[i];
                    } else if (ranker[i] > third)
                        third = ranker[i];
                }*/

                    ArrayList<Integer> colors = new ArrayList<>();
                    for (int color : ColorTemplate.MATERIAL_COLORS) {
                        colors.add(color);
                    }
                    for (int color : ColorTemplate.VORDIPLOM_COLORS) {
                        colors.add(color);
                    }

                    PieDataSet dataSet = new PieDataSet(entries, "");
                    dataSet.setColors(colors);
                    dataSet.setSliceSpace(3);
                    dataSet.setSelectionShift(5);

                    PieData data = new PieData(dataSet);
                    data.setValueFormatter(new PercentFormatter());
                    data.setDrawValues(true);
                    data.setValueTextSize(12f);
                    data.setValueTextColor(Color.BLACK);
                    pieChart.setData(data);
                    pieChart.invalidate();
                    pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
                } else
                    Toast.makeText(getActivity(), "setupPieChart() error: DataSnapShot does not exist!!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class MyAxixValueFormatter implements IAxisValueFormatter {
        private String[] mValues;

        public MyAxixValueFormatter(String[] Values) {
            this.mValues = Values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int) value];
        }
    }
}

