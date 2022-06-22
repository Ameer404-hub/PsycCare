package com.example.psyccare.DataAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;

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
        holder.drawChart(itemsMood.getClassifiedAs(), itemsMood.getPerCent());
        boolean isVisible = itemsMood.visibility;
        holder.moreLayoutMood.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        holder.setMoodImage(itemsMood.getType());
    }

    @Override
    public int getItemCount() {
        return checkInsMood.size();
    }

    public class checkInsMoodViewHolder extends RecyclerView.ViewHolder {

        LinearLayout fullLayout;
        RelativeLayout tapLayoutMood, moreLayoutMood;
        TextView checkInDateMood, checkInTitleMood, checkInDescMood;
        HorizontalBarChart mBarChartMood;
        ImageView DownArrowMood, moodImage;
        String moodImageID;

        public checkInsMoodViewHolder(@NonNull View itemView) {
            super(itemView);

            fullLayout = itemView.findViewById(R.id.moodStatsLayout);
            tapLayoutMood = itemView.findViewById(R.id.tapToShow);
            moreLayoutMood = itemView.findViewById(R.id.moodStatsCheckIn);
            checkInDateMood = itemView.findViewById(R.id.dateViewStats);
            checkInTitleMood = itemView.findViewById(R.id.moodTitleStats);
            checkInDescMood = itemView.findViewById(R.id.moodDescStats);
            mBarChartMood = itemView.findViewById(R.id.chartStats);
            DownArrowMood = itemView.findViewById(R.id.downArrow);
            moodImage = itemView.findViewById(R.id.moodImage);
            tapLayoutMood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkInModel modelMood = checkInsMood.get(getAdapterPosition());
                    modelMood.setVisibility(!modelMood.isVisibility());
                    if (modelMood.visibility == true)
                        DownArrowMood.setImageResource(R.drawable.ic_baseline_down);
                    else if (modelMood.visibility == false)
                        DownArrowMood.setImageResource(R.drawable.ic_baseline_up);

                    notifyItemChanged(getAdapterPosition());
                }
            });

        }

        private void setMoodImage(String Title) {
            moodImageID = Title;
            if (!moodImageID.equals("")) {
                moodImageID = moodImageID.toLowerCase();
                moodImageID = "@drawable/moods_" + moodImageID;

                String uri = moodImageID;  // where myresource (without the extension) is the file
                int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
                Drawable res = context.getResources().getDrawable(imageResource);
                moodImage.setImageDrawable(res);
            } else {

            }
        }

        private void drawChart(String moodType, String moodPercent) {
            String HighValueLable, HighValue;
            HighValue = moodPercent.replaceAll("[^\\d.]", "");
            HighValueLable = moodType;

            ArrayList<String> BarLabel = new ArrayList<>();
            ArrayList<BarEntry> barEntries = new ArrayList<>();

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
            barEntries.add(new BarEntry(0, Float.parseFloat(HighValue)));
            BarLabel.add(HighValue + "% " + HighValueLable);
            barchart(mBarChartMood, barEntries, BarLabel);
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
    }

}
