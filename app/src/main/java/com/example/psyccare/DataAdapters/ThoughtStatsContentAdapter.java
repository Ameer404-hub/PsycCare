package com.example.psyccare.DataAdapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.psyccare.DataModels.checkInModel;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tensorflow.lite.examples.textclassification.client.Result;
import org.tensorflow.lite.examples.textclassification.client.TextClassificationClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThoughtStatsContentAdapter extends RecyclerView.Adapter<ThoughtStatsContentAdapter.checkInsThoughtViewHolder> {

    Context context;
    ArrayList<checkInModel> checkInsThought;

    public ThoughtStatsContentAdapter(Context context, ArrayList<checkInModel> checkInsThought) {
        this.context = context;
        this.checkInsThought = checkInsThought;
    }

    @NonNull
    @Override
    public checkInsThoughtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stats_cardview, parent, false);
        checkInsThoughtViewHolder thoughtCheckInsViewHolder = new checkInsThoughtViewHolder(view);
        return thoughtCheckInsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull checkInsThoughtViewHolder holder, int position) {
        checkInModel items = checkInsThought.get(position);
        holder.checkInTitleThought.setText(items.getType());
        holder.checkInDateThought.setText(items.getCheckInDate() + " " + items.getCheckInTime());
        holder.checkInDescThought.setText(items.getDescription());
        holder.classify(items.getDescription(), items.getCheckInDate() + " " + items.getCheckInTime());
        boolean isVisible = items.visibility;
        holder.moreLayoutThought.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return checkInsThought.size();
    }

    public class checkInsThoughtViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout tapLayoutThought, moreLayoutThought;
        TextView checkInDateThought, checkInTitleThought, checkInDescThought;
        HorizontalBarChart mBarChartThought;
        TextClassificationClient client;
        DatabaseReference referenceToThoughtCheckin;
        ImageView DownArrowThought, ThoughtImage;
        Handler handler;
        String LableThought, ThoughtImageID;
        Float ValueThought;

        public checkInsThoughtViewHolder(@NonNull View itemView) {
            super(itemView);

            tapLayoutThought = itemView.findViewById(R.id.tapToShow);
            moreLayoutThought = itemView.findViewById(R.id.moodStatsCheckIn);
            checkInDateThought = itemView.findViewById(R.id.dateViewStats);
            checkInTitleThought = itemView.findViewById(R.id.moodTitleStats);
            checkInDescThought = itemView.findViewById(R.id.moodDescStats);
            mBarChartThought = itemView.findViewById(R.id.chartStats);
            DownArrowThought = itemView.findViewById(R.id.downArrow);
            ThoughtImage = itemView.findViewById(R.id.moodImage);

            referenceToThoughtCheckin = FirebaseDatabase.getInstance().getReference("User")
                    .child(FirebaseAuth.getInstance().getUid()).child("ThoughtCheckIns");

            handler = new Handler();
            client = new TextClassificationClient(context.getApplicationContext());
            client.load();

            tapLayoutThought.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkInModel modelThought = checkInsThought.get(getAdapterPosition());
                    modelThought.setVisibility(!modelThought.isVisibility());
                    if (modelThought.visibility == true)
                        DownArrowThought.setImageResource(R.drawable.ic_baseline_up_24);
                    else if (modelThought.visibility == false)
                        DownArrowThought.setImageResource(R.drawable.ic_baseline_down_24);

                    notifyItemChanged(getAdapterPosition());
                }
            });

        }

        private void classify(final String userText, String ID) {
            handler.post(
                    () -> {
                        // Run text classification with TF Lite.
                        List<Result> results = client.classify(userText);
                        // Show classification result on screen
                        showResult(userText, results);
                        HashMap<String, Object> Update = new HashMap<>();
                        Update.put("classifiedAs", LableThought + " " + ValueThought * 1000 / 10.0 + "%");
                        referenceToThoughtCheckin.child(ID).updateChildren(Update);
                    });
        }

        private void barchart(BarChart barChart, ArrayList<BarEntry> arrayList, final ArrayList<String> xAxisValues) {
            barChart.setDrawBarShadow(false);
            barChart.setFitBars(true);
            barChart.setDrawValueAboveBar(true);
            barChart.setMaxVisibleValueCount(25);
            barChart.setPinchZoom(true);

            barChart.setDrawGridBackground(true);
            BarDataSet barDataSet = new BarDataSet(arrayList, "Class");
            barDataSet.setColors(new int[]{Color.parseColor("#03A9F4"),
                    Color.parseColor("#FF9800"), Color.parseColor("#76FF03"),
                    Color.parseColor("#000000"), Color.parseColor("#E91E63"),
                    Color.parseColor("#2962FF")});
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
            // To set components of x axis
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

        private void showResult(final String inputText, final List<Result> results) {
            ((AppCompatActivity) context).runOnUiThread(
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

                        mBarChartThought.setDrawBarShadow(false);
                        mBarChartThought.setDrawValueAboveBar(true);
                        mBarChartThought.getDescription().setEnabled(false);
                        mBarChartThought.setPinchZoom(false);
                        mBarChartThought.setDrawGridBackground(false);


                        XAxis xl = mBarChartThought.getXAxis();
                        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xl.setDrawAxisLine(true);
                        xl.setDrawGridLines(false);
                        xl.setGranularity(1);

                        YAxis yl = mBarChartThought.getAxisLeft();
                        yl.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                        yl.setDrawGridLines(false);
                        yl.setEnabled(false);
                        yl.setAxisMinimum(0f);

                        YAxis yr = mBarChartThought.getAxisRight();
                        yr.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                        yr.setDrawGridLines(false);
                        yr.setAxisMinimum(0f);

                        // PREPARING THE ARRAY LIST OF BAR ENTRIES

                        HighValueLable = labels.get(probability.size() - 1);
                        HighValue = probability.get(probability.size() - 1);

                        barEntries.add(new BarEntry(0, HighValue));
                        BarLabel.add(Math.round(HighValue * 1000) / 10.0 + "% " + HighValueLable);
                        barchart(mBarChartThought, barEntries, BarLabel);

                        ValueThought = HighValue;
                        LableThought = HighValueLable;
                    });
        }
    }

}
