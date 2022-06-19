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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.psyccare.DataModels.DoubleXLabelAxisRenderer;
import com.example.psyccare.DataModels.MyAxixValueFormatter;
import com.example.psyccare.MyStats;
import com.example.psyccare.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
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

import org.tensorflow.lite.examples.textclassification.client.Result;
import org.tensorflow.lite.examples.textclassification.client.TextClassificationClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoodTab extends Fragment {

    private TextClassificationClient client;
    Handler handler;
    DatabaseReference referenceToMoodCheckin, referenceToMonthCheckin, referenceToDailyCheckin;
    ProgressDialog messageBox;
    LineChart lineChart;
    PieChart pieChart;
    TextView openMFullStatsLC, openMFullStatsPC;
    String monthNode, dateNode, HighValueLable, HighValue, tapVal, tapDBVal, tapDBDate, lineChartDay, lineChartDate, allCheckInStat, classifiedAs, perCent;
    String[] lineChartDays, lineChartDates, lineChartvals, pieChartLables;
    int entryCount = 0, lineChartCount = 0, pieChartCount = 0, alertBoxCount1 = 0, alertBoxCount2 = 0, dailyCheckCount = 0, valExistCount = 0, loopCount = 1;
    Float Happy = 0f, Sad = 0f, Angry = 0f, Anxious = 0f, Excited = 0f, Stressed = 0f, Awesome = 0f, Terrible = 0f, Tired = 0f,
            Hopeful = 0f, Okay = 0f, Calm = 0f, Satisfied = 0f, Frustrated = 0f, NotSure = 0f;

    @Override
    public void onStart() {
        super.onStart();
        handler.post(
                () -> {
                    client.load();
                }
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.post(
                () -> {
                    client.load();
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mood_tab, container, false);

        client = new TextClassificationClient(getActivity());
        handler = new Handler();

        pieChart = rootView.findViewById(R.id.mPieChart);
        lineChart = rootView.findViewById(R.id.mLineChart);
        openMFullStatsLC = rootView.findViewById(R.id.openMStatsLC);
        openMFullStatsPC = rootView.findViewById(R.id.openMStatsPC);

        openMFullStatsLC.setOnClickListener(v -> {
            Intent moveTo = new Intent(getActivity(), MyStats.class);
            startActivity(moveTo);
        });
        openMFullStatsPC.setOnClickListener(v -> {
            Intent moveTo = new Intent(getActivity(), MyStats.class);
            startActivity(moveTo);
        });

        messageBox = new ProgressDialog(getActivity());
        messageBox.setTitle("");
        messageBox.setMessage("Loading...");
        messageBox.setCanceledOnTouchOutside(false);

        lineChartDays = new String[31];
        lineChartDates = new String[31];
        lineChartvals = new String[31];
        pieChartLables = new String[190];

        referenceToMoodCheckin = FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getUid()).child("MoodCheckIns");

        if (isConnected()) {
            messageBox.show();
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            totalDailyCheckIns();
            allPercentCount();
            setupLineChart();
            setupPieChart();
        } else
            Toast.makeText(getActivity(), "You're device is not connected to internet", Toast.LENGTH_LONG).show();

        messageBox.setOnCancelListener(dialog -> getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE));
        return rootView;
    }

    private boolean isConnected() {
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
        lineChart.setDrawGridBackground(false);
        lineChart.setPinchZoom(true);
        lineChart.setTouchEnabled(true);

        Description description = new Description();
        description.setText("");
        description.setTextSize(10);
        lineChart.setDescription(description);
        lineChart.setNoDataText("Not enough check ins completed to generate chart!");

        Legend l = lineChart.getLegend();
        l.setEnabled(false);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setTextSize(12);

        ArrayList<Entry> entries = new ArrayList<>();
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setGranularity(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setValueFormatter(new MyAxixValueFormatter(lineChartDates));
        xAxis.setAxisMinimum(1);

        YAxis yAxisLeft = lineChart.getAxisLeft();
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);
        yAxisLeft.setEnabled(true);
        yAxisLeft.setDrawGridLines(true);
        /*yAxisLeft.setValueFormatter((value, axis) -> "");*/

        lineChart.setXAxisRenderer(new DoubleXLabelAxisRenderer(lineChartDays, lineChart.getViewPortHandler(), lineChart.getXAxis(),
                lineChart.getTransformer(YAxis.AxisDependency.LEFT), (value, axis) -> {
            if (((int) value) > -1) {
                if(lineChartDays[((int) value)] != null)
                    return lineChartDays[((int) value)];
            }
            return "";
        }));

        referenceToMoodCheckin.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        monthNode = ds.getKey();
                        referenceToMonthCheckin = referenceToMoodCheckin.child(monthNode);
                        classifyAllCheckIns(monthNode, referenceToMonthCheckin);
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
                                                        if (dsDaily.child("classifiedAllAs").exists()) {
                                                            classifiedAs = dsDaily.child("classifiedAllAs").getValue().toString().trim();
                                                            perCent = dsDaily.child("perCent").getValue().toString().trim();
                                                            lineChartDate = dsDaily.child("checkInDate").getValue().toString().trim();

                                                            if (!classifiedAs.equals("") && !perCent.equals("")) {
                                                                HighValue = perCent.replaceAll("[^\\d.]", "");
                                                                HighValueLable = classifiedAs.replaceAll(" ", "");
                                                                entries.add(new Entry(entryCount, Float.parseFloat(HighValue)));
                                                            }
                                                            if (lineChartDate.length() < 17) {
                                                                lineChartDay = lineChartDate.substring(13, 16);
                                                                lineChartDate = lineChartDate.substring(0, 5);
                                                            } else {
                                                                lineChartDay = lineChartDate.substring(14, 17);
                                                                lineChartDate = lineChartDate.substring(0, 6);
                                                            }
                                                            lineChartDays[lineChartCount] = lineChartDay;
                                                            lineChartDates[lineChartCount] = lineChartDate;
                                                            lineChartCount++;
                                                            entryCount++;
                                                        }
                                                    }
                                                    messageBox.dismiss();
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                    LineDataSet lineSet = new LineDataSet(entries, "");
                                                    lineSet.setColors(ColorTemplate.getHoloBlue());
                                                    lineSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                                                    lineSet.setLineWidth(2f);
                                                    lineSet.setDrawCircles(true);
                                                    lineSet.setDrawCircleHole(true);
                                                    lineSet.setCubicIntensity(0.2f);
                                                    lineSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                                                    lineSet.setCircleColors(ColorTemplate.getHoloBlue());
                                                    lineSet.setCircleRadius(5f);
                                                    lineSet.setFillAlpha(65);
                                                    lineSet.setFillColor(ColorTemplate.getHoloBlue());
                                                    lineSet.setCircleRadius(7);
                                                    lineSet.setDrawValues(false);
                                                    lineSet.setDrawFilled(true);
                                                    lineSet.setFillDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.linechart_gradient));

                                                    ArrayList<ILineDataSet> dataSet = new ArrayList<>();
                                                    dataSet.add(lineSet);
                                                    LineData data = new LineData(dataSet);

                                                    lineChart.setData(data);
                                                    if (lineChartCount < 6)
                                                        lineChart.setVisibleXRange(0, lineChartCount);
                                                    else
                                                        lineChart.setVisibleXRange(0, 6);
                                                    lineChart.animateX(1400, Easing.EasingOption.EaseInOutQuad);
                                                    lineChart.invalidate();

                                                    lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                                                        @Override
                                                        public void onValueSelected(Entry e, Highlight h) {
                                                            messageBox.show();
                                                            tapVal = e.toString();
                                                            Log.v("tapVal; ", tapVal);

                                                            int valYIndex  = tapVal.indexOf("y:");
                                                            Log.v("valYIndex; ", String.valueOf(valYIndex));

                                                            String valY  = tapVal.substring(valYIndex);
                                                            Log.v("valY; ", valY);

                                                            int percentIndex = valY.indexOf(" ");
                                                            Log.v("percentIndex; ", String.valueOf(percentIndex));

                                                            String percent  = valY.substring(percentIndex);
                                                            Log.v("percent; ", percent);

                                                            percent = percent.replace(" ", "");
                                                            Log.v("percentReplace; ", percent);
                                                            findMyTap(percent);
                                                        }

                                                        @Override
                                                        public void onNothingSelected() {

                                                        }
                                                    });
                                                } else {
                                                    messageBox.dismiss();
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                    Toast.makeText(getActivity(), "setupLineChart() error: Date Node " + dateNode + " does not exist!!!", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getActivity(), "setupLineChart() error: Month Node " + monthNode + " does not exist!!!", Toast.LENGTH_SHORT).show();
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
                messageBox.dismiss();
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
        pieChart.setDrawEntryLabels(false);
        pieChart.setEntryLabelTextSize(13);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("");
        pieChart.setNoDataText("Not enough check ins completed to generate chart!");

        Description description = new Description();
        description.setText("");
        description.setTextSize(12);
        pieChart.setDescription(description);

        Legend l = pieChart.getLegend();
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setEnabled(true);
        l.setTextSize(12);

        ArrayList<PieEntry> entries = new ArrayList<>();

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
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot dsDaily : dataSnapshot.getChildren()) {
                                                        if (dsDaily.child("description").exists()) {
                                                            classifiedAs = dsDaily.child("type").getValue().toString().trim();
                                                            if (!classifiedAs.equals("") && !perCent.equals("")) {
                                                                HighValue = perCent.replaceAll("[^\\d.]", "");
                                                                HighValueLable = classifiedAs.replaceAll(" ", "");
                                                                pieChartLables[pieChartCount] = HighValueLable;
                                                                if (pieChartLables[pieChartCount].equals("Happy"))
                                                                    Happy++;
                                                                else if (pieChartLables[pieChartCount].equals("Sad"))
                                                                    Sad++;
                                                                else if (pieChartLables[pieChartCount].equals("Anggry"))
                                                                    Angry++;
                                                                else if (pieChartLables[pieChartCount].equals("Anxious"))
                                                                    Anxious++;
                                                                else if (pieChartLables[pieChartCount].equals("Excited"))
                                                                    Excited++;
                                                                else if (pieChartLables[pieChartCount].equals("Stressed"))
                                                                    Stressed++;
                                                                else if (pieChartLables[pieChartCount].equals("Awesome"))
                                                                    Awesome++;
                                                                else if (pieChartLables[pieChartCount].equals("Terrible"))
                                                                    Terrible++;
                                                                else if (pieChartLables[pieChartCount].equals("Tired"))
                                                                    Tired++;
                                                                else if (pieChartLables[pieChartCount].equals("Hopeful"))
                                                                    Hopeful++;
                                                                else if (pieChartLables[pieChartCount].equals("Okay"))
                                                                    Okay++;
                                                                else if (pieChartLables[pieChartCount].equals("Calm"))
                                                                    Calm++;
                                                                else if (pieChartLables[pieChartCount].equals("Satisfied"))
                                                                    Satisfied++;
                                                                else if (pieChartLables[pieChartCount].equals("Frustrated"))
                                                                    Frustrated++;
                                                                else if (pieChartLables[pieChartCount].equals("Not Sure"))
                                                                    NotSure++;
                                                                pieChartCount++;
                                                            }
                                                        }
                                                    }
                                                    if (pieChartCount == dailyCheckCount) {
                                                        if (valExistCount >= 7) {
                                                            if (Happy > 0 || Awesome > 0 || Hopeful > 0 || Excited > 0)
                                                                entries.add(new PieEntry(Math.round(((Happy + Awesome + Hopeful + Excited) / dailyCheckCount) * 100), "Cheerful"));
                                                            if (Sad > 0 || Tired > 0 || NotSure > 0)
                                                                entries.add(new PieEntry(Math.round(((Sad + Tired) / dailyCheckCount) * 100), "Depression"));
                                                            if (Angry > 0 || Anxious > 0)
                                                                entries.add(new PieEntry(Math.round(((Angry + Anxious) / dailyCheckCount) * 100), "Anxiety"));
                                                            if (Stressed > 0 || Terrible > 0 || Frustrated > 0)
                                                                entries.add(new PieEntry(Math.round(((Stressed + Terrible + Tired + Frustrated) / dailyCheckCount) * 100), "Stress"));
                                                            if (Okay > 0 || Calm > 0 || Satisfied > 0)
                                                                entries.add(new PieEntry(Math.round(((Okay + Calm + Satisfied) / dailyCheckCount) * 100), "Peaceful"));

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
                                                            data.setValueTextSize(13f);
                                                            data.setValueTextColor(Color.BLACK);
                                                            pieChart.setData(data);
                                                            pieChart.invalidate();
                                                            pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
                                                        }
                                                    }
                                                } else {
                                                    messageBox.dismiss();
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                    Toast.makeText(getActivity(), "setupPieChart() error: Date Node " + dateNode + " does not exist!!!", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getActivity(), "setupPieChart() error: Month Node " + monthNode + " does not exist!!!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "setupPieChart() error: MoodCheckin Node  does not exist!!!", Toast.LENGTH_SHORT).show();
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

    private void classifyAllCheckIns(String monthPos, DatabaseReference monthReference) {
        monthReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dsMonthNode : dataSnapshot.getChildren()) {
                        dateNode = dsMonthNode.getKey();
                        referenceToDailyCheckin = referenceToMonthCheckin.child(dateNode);
                        updateAllMerged(dateNode, monthPos, referenceToDailyCheckin);
                    }
                } else {
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), "Error: Month Node " + monthPos + " does not exist!!!", Toast.LENGTH_SHORT).show();
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

    private void updateAllMerged(String datePos, String monthPos, DatabaseReference dailyReference) {
        dailyReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1All) {
                if (dataSnapshot1All.exists()) {
                    for (DataSnapshot dsDailyAll : dataSnapshot1All.getChildren()) {
                        if (dsDailyAll.child("allMerged").exists()) {
                            allCheckInStat = dsDailyAll.child("allMerged").getValue().toString().trim();
                            List<Result> results = client.classify(allCheckInStat);
                            ArrayList<String> labels = new ArrayList<>();
                            ArrayList<Float> probability = new ArrayList<>();
                            for (int i = 0; i < results.size(); i++) {
                                Result result = results.get(i);
                                labels.add(result.getTitle());   // Extract labels
                                probability.add(result.getConfidence());  // Extract confidence
                                Log.v("probability", probability.toString());
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
                            String HighValueLable;
                            Float HighValue;
                            HighValueLable = labels.get(probability.size() - 1);
                            HighValue = probability.get(probability.size() - 1);
                            perCent = Math.round(HighValue * 1000) / 10.0 + "%";
                            classifiedAs = HighValueLable;

                            for(int a = loopCount ;a < lineChartvals.length; a++){
                                if(perCent.equals(lineChartvals[a])){
                                    perCent = (Math.round(HighValue * 1000) / 10.0) - 1.0 + "%";
                                    break;
                                }
                                else
                                    perCent = Math.round(HighValue * 1000) / 10.0 + "%";
                            }

                            if(loopCount > 3){
                                for(int b = loopCount - 2; b >= 0; b--){
                                    if(perCent.equals(lineChartvals[b])){
                                        perCent = (Math.round(HighValue * 1000) / 10.0) - 1.0 + "%";
                                        break;
                                    }
                                    else
                                        perCent = Math.round(HighValue * 1000) / 10.0 + "%";
                                }
                            }

                            loopCount++;
                            referenceToDailyCheckin = referenceToMoodCheckin.child(monthPos).child(datePos);
                            referenceToDailyCheckin.child("allCheckIns").child("classifiedAllAs").setValue(classifiedAs);
                            referenceToDailyCheckin.child("allCheckIns").child("perCent").setValue(perCent);
                        }
                    }

                } else {
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), "Date Node " + datePos + " does not exist!!!", Toast.LENGTH_SHORT).show();
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

    private void findMyTap(String tapValArgs) {
        DatabaseReference referenceTraceChart = FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getUid()).child("MoodCheckIns");
        referenceTraceChart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotTrace) {
                if (dataSnapshotTrace.exists()) {
                    for (DataSnapshot dsTrace : dataSnapshotTrace.getChildren()) {
                        monthNode = dsTrace.getKey();
                        DatabaseReference referenceTraceChart2 = referenceTraceChart.child(monthNode);
                        referenceTraceChart2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshotTrace2) {
                                if (dataSnapshotTrace2.exists()) {
                                    for (DataSnapshot dsTrace2 : dataSnapshotTrace2.getChildren()) {
                                        dateNode = dsTrace2.getKey();
                                        DatabaseReference referenceTraceChart3 = referenceTraceChart2.child(dateNode);
                                        referenceTraceChart3.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshotTrace3) {
                                                if (dataSnapshotTrace3.exists()) {
                                                    for (DataSnapshot dsTrace3 : dataSnapshotTrace3.getChildren()) {
                                                        if (dsTrace3.child("allMerged").exists()) {
                                                            dateNode = dsTrace3.child("checkInDate").getValue().toString().trim();
                                                            classifiedAs = dsTrace3.child("classifiedAllAs").getValue().toString().trim();
                                                            perCent = dsTrace3.child("perCent").getValue().toString().trim();
                                                            HighValue = perCent.replaceAll("[^\\d.]", "");
                                                            HighValueLable = classifiedAs;
                                                            Log.v("HighValue: ", HighValue);
                                                            if (tapValArgs.equals(HighValue)) {
                                                                generateAlertBox(HighValue, HighValueLable, dateNode);
                                                                alertBoxCount1 = 0;
                                                                alertBoxCount2 = 0;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    messageBox.dismiss();
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                    Toast.makeText(getActivity(), "findMyTap() error: Date Node " + dateNode + " does not exist!!!", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseErrorTrace3) {
                                                messageBox.dismiss();
                                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                Toast.makeText(getActivity(), "Error: " + databaseErrorTrace3.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                } else {
                                    messageBox.dismiss();
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Toast.makeText(getActivity(), "findMyTap() error: Month Node " + monthNode + " does not exist!!!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseErrorTrace2) {
                                messageBox.dismiss();
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(getActivity(), "Error: " + databaseErrorTrace2.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                } else {
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), "findMyTap() error: MoodCheckIns Node does not exist!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseErrorTrace) {
                messageBox.dismiss();
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getActivity(), "Error: " + databaseErrorTrace.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void generateAlertBox(String myValue, String myValueLable, String myDate) {
        DatabaseReference referenceTraceChart = FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getUid()).child("MoodCheckIns");
        referenceTraceChart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotTrace) {
                if (dataSnapshotTrace.exists()) {
                    for (DataSnapshot dsTrace : dataSnapshotTrace.getChildren()) {
                        monthNode = dsTrace.getKey();
                        DatabaseReference referenceTraceChart2 = referenceTraceChart.child(monthNode);
                        referenceTraceChart2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshotTrace2) {
                                if (dataSnapshotTrace2.exists()) {
                                    for (DataSnapshot dsTrace2 : dataSnapshotTrace2.getChildren()) {
                                        if (dsTrace2.child("allCheckIns").exists()) {
                                            tapDBVal = dsTrace2.child("allCheckIns").child("perCent").getValue().toString();
                                            tapDBDate = dsTrace2.child("allCheckIns").child("checkInDate").getValue().toString();
                                            tapDBVal = tapDBVal.replace("%", "");
                                            if (tapDBVal.equals(myValue) && tapDBDate.equals(myDate)) {
                                                dateNode = dsTrace2.getKey();
                                                DatabaseReference referenceTraceChart3 = referenceTraceChart2.child(dateNode);
                                                referenceTraceChart3.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshotTrace3) {
                                                        if (dataSnapshotTrace3.exists()) {
                                                            for (DataSnapshot dsTrace3 : dataSnapshotTrace3.getChildren()) {
                                                                if (dsTrace3.child("description").exists()) {
                                                                    alertBoxCount1++;
                                                                    AlertDialog.Builder showMessage = new AlertDialog.Builder(getActivity());
                                                                    showMessage.setMessage("").setTitle(dsTrace3.child("checkInDate").getValue().toString() + "\n" + dsTrace3.getKey());
                                                                    showMessage.setMessage("Mood Type: " + dsTrace3.child("type").getValue().toString() + "\n\n" +
                                                                            "Mood Note: " + dsTrace3.child("description").getValue().toString() + "\n\n").setCancelable(false).
                                                                            setPositiveButton("Next", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    alertBoxCount2++;
                                                                                    if (alertBoxCount2 == alertBoxCount1) {
                                                                                        AlertDialog.Builder showMessageLast = new AlertDialog.Builder(getActivity());
                                                                                        showMessageLast.setMessage("").setTitle(myDate + "\n" + "Overall Day Analysis");
                                                                                        showMessageLast.setMessage("Most Experienced Emotion:\n" + myValueLable + " " + myValue + "%" + "\n\n" +
                                                                                                "For more details, tap on\n'Open full stats>>'").setCancelable(false).
                                                                                                setPositiveButton("Hide", new DialogInterface.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                                                    }
                                                                                                });
                                                                                        AlertDialog alert = showMessageLast.create();
                                                                                        alert.show();
                                                                                    }
                                                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                                }
                                                                            });
                                                                    AlertDialog alert = showMessage.create();
                                                                    messageBox.dismiss();
                                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                    alert.show();
                                                                }
                                                            }
                                                        } else {
                                                            messageBox.dismiss();
                                                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                            Toast.makeText(getActivity(), "findMyTap() error: Date Node " + dateNode + " does not exist!!!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseErrorTrace3) {
                                                        messageBox.dismiss();
                                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                        Toast.makeText(getActivity(), "Error: " + databaseErrorTrace3.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                } else {
                                    messageBox.dismiss();
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Toast.makeText(getActivity(), "findMyTap() error: Month Node " + monthNode + " does not exist!!!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseErrorTrace2) {
                                messageBox.dismiss();
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(getActivity(), "Error: " + databaseErrorTrace2.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                } else {
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), "findMyTap() error: MoodCheckIns Node does not exist!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseErrorTrace) {
                messageBox.dismiss();
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getActivity(), "Error: " + databaseErrorTrace.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void totalDailyCheckIns() {
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
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot dsDaily : dataSnapshot.getChildren()) {
                                                        if (dsDaily.child("description").exists()) {
                                                            dailyCheckCount++;
                                                        }
                                                    }
                                                } else {
                                                    messageBox.dismiss();
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                    Toast.makeText(getActivity(), "setupPieChart() error: Date Node " + dateNode + " does not exist!!!", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getActivity(), "setupPieChart() error: Month Node " + monthNode + " does not exist!!!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "setupPieChart() error: MoodCheckin Node  does not exist!!!", Toast.LENGTH_SHORT).show();
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

    private void allPercentCount() {
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
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot dsDaily : dataSnapshot.getChildren()) {
                                                        if (dsDaily.child("classifiedAllAs").exists()) {
                                                            perCent = dsDaily.child("perCent").getValue().toString().trim();
                                                            lineChartvals[valExistCount] = perCent;
                                                            Log.v("", "lineChartvals: "+lineChartvals[valExistCount]);
                                                            valExistCount++;
                                                        }
                                                    }
                                                } else {
                                                    messageBox.dismiss();
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                    Toast.makeText(getActivity(), "setupPieChart() error: Date Node " + dateNode + " does not exist!!!", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getActivity(), "setupPieChart() error: Month Node " + monthNode + " does not exist!!!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "setupPieChart() error: MoodCheckin Node  does not exist!!!", Toast.LENGTH_SHORT).show();
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

