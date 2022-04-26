package com.example.psyccare.DataAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

public class MoodStatsContentAdapter extends RecyclerView.Adapter<MoodStatsContentAdapter.checkInsMoodViewHolder> {

    Context context;
    ArrayList<checkInModel> checkInsMood;

    public MoodStatsContentAdapter(Context context, ArrayList<checkInModel> checkInsMood) {
        this.context = context;
        this.checkInsMood = checkInsMood;
    }

    @NonNull
    @Override
    public checkInsMoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stats_cardview, parent, false);
        checkInsMoodViewHolder moodCheckInsViewHolder = new checkInsMoodViewHolder(view);
        return moodCheckInsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull checkInsMoodViewHolder holder, int position) {
        checkInModel itemsMood = checkInsMood.get(position);
        holder.checkInTitleMood.setText(itemsMood.getType());
        holder.checkInDateMood.setText(itemsMood.getCheckInDate() + " " + itemsMood.getCheckInTime());
        holder.checkInDescMood.setText(itemsMood.getDescription());
        holder.classify(itemsMood.getDescription(), itemsMood.getCheckInDate() + " " + itemsMood.getCheckInTime());
        boolean isVisible = itemsMood.visibility;
        holder.moreLayoutMood.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        holder.setMoodImage(itemsMood.getType());
    }

    @Override
    public int getItemCount() {
        return checkInsMood.size();
    }

    public class checkInsMoodViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout tapLayoutMood, moreLayoutMood;
        TextView checkInDateMood, checkInTitleMood, checkInDescMood;
        HorizontalBarChart mBarChartMood;
        TextClassificationClient client;
        DatabaseReference referenceToMoodCheckin;
        ImageView DownArrowMood, moodImage;
        Handler handler;
        String LableMood, moodImageID;
        Float ValueMood;

        public checkInsMoodViewHolder(@NonNull View itemView) {
            super(itemView);

            tapLayoutMood = itemView.findViewById(R.id.tapToShow);
            moreLayoutMood = itemView.findViewById(R.id.moodStatsCheckIn);
            checkInDateMood = itemView.findViewById(R.id.dateViewStats);
            checkInTitleMood = itemView.findViewById(R.id.moodTitleStats);
            checkInDescMood = itemView.findViewById(R.id.moodDescStats);
            mBarChartMood = itemView.findViewById(R.id.chartStats);
            DownArrowMood = itemView.findViewById(R.id.downArrow);
            moodImage = itemView.findViewById(R.id.moodImage);

            referenceToMoodCheckin = FirebaseDatabase.getInstance().getReference("User")
                    .child(FirebaseAuth.getInstance().getUid()).child("MoodCheckIns");

            handler = new Handler();
            client = new TextClassificationClient(context.getApplicationContext());
            client.load();

            tapLayoutMood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkInModel modelMood = checkInsMood.get(getAdapterPosition());
                    modelMood.setVisibility(!modelMood.isVisibility());
                    if (modelMood.visibility == true)
                        DownArrowMood.setImageResource(R.drawable.ic_baseline_up_24);
                    else if (modelMood.visibility == false)
                        DownArrowMood.setImageResource(R.drawable.ic_baseline_down_24);

                    notifyItemChanged(getAdapterPosition());
                }
            });

        }

        private void setMoodImage(String Title) {
            moodImageID = Title;
            if (!moodImageID.equals("")) {
                moodImageID = moodImageID.toLowerCase();
                moodImageID = moodImageID.replaceAll(" ", "");
                moodImageID = "moods_" + moodImageID;
                int index = moodImageID.indexOf(",");
                moodImageID = moodImageID.substring(0, index);
                moodImageID = "@drawable/" + moodImageID;

                String uri = moodImageID;  // where myresource (without the extension) is the file
                int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
                Drawable res = context.getResources().getDrawable(imageResource);
                moodImage.setImageDrawable(res);
            } else {

            }
        }

        private void classify(final String userText, String ID) {
            handler.post(
                    () -> {
                        // Run text classification with TF Lite.
                        List<Result> results = client.classify(userText);
                        // Show classification result on screen
                        showResult(userText, results);
                        HashMap<String, Object> Update = new HashMap<>();
                        Update.put("classifiedAs", LableMood + " " + ValueMood * 1000 / 10.0 + "%");
                        referenceToMoodCheckin.child(ID).updateChildren(Update);
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

                        mBarChartMood.setDrawBarShadow(false);
                        mBarChartMood.setDrawValueAboveBar(true);
                        mBarChartMood.getDescription().setEnabled(false);
                        mBarChartMood.setPinchZoom(false);
                        mBarChartMood.setDrawGridBackground(false);


                        XAxis xl = mBarChartMood.getXAxis();
                        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xl.setDrawAxisLine(true);
                        xl.setDrawGridLines(false);
                        xl.setGranularity(1);

                        YAxis yl = mBarChartMood.getAxisLeft();
                        yl.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                        yl.setDrawGridLines(false);
                        yl.setEnabled(false);
                        yl.setAxisMinimum(0f);

                        YAxis yr = mBarChartMood.getAxisRight();
                        yr.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                        yr.setDrawGridLines(false);
                        yr.setAxisMinimum(0f);

                        // PREPARING THE ARRAY LIST OF BAR ENTRIES

                        HighValueLable = labels.get(probability.size() - 1);
                        HighValue = probability.get(probability.size() - 1);

                        barEntries.add(new BarEntry(0, HighValue));
                        BarLabel.add(Math.round(HighValue * 1000) / 10.0 + "% " + HighValueLable);
                        barchart(mBarChartMood, barEntries, BarLabel);

                        ValueMood = HighValue;
                        LableMood = HighValueLable;
                    });
        }
    }

}
