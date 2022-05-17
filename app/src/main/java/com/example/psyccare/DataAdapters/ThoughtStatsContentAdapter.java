package com.example.psyccare.DataAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        checkInModel itemsThought = checkInsThought.get(position);
        holder.checkInTitleThought.setText(itemsThought.getType());
        holder.checkInDateThought.setText(itemsThought.getCheckInDate() + " " + itemsThought.getCheckInTime());
        holder.checkInDescThought.setText(itemsThought.getDescription());
        holder.drawChart(itemsThought.getClassifiedAs());
        boolean isVisible = itemsThought.visibility;
        holder.moreLayoutThought.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        holder.setThoughtImage(itemsThought.getType());
    }

    @Override
    public int getItemCount() {
        return checkInsThought.size();
    }

    public class checkInsThoughtViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout tapLayoutThought, moreLayoutThought;
        TextView checkInDateThought, checkInTitleThought, checkInDescThought;
        HorizontalBarChart mBarChartThought;
        DatabaseReference referenceToThoughtCheckin;
        ImageView DownArrowThought, ThoughtImage;
        String ThoughtImageID;

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

            tapLayoutThought.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkInModel modelThought = checkInsThought.get(getAdapterPosition());
                    modelThought.setVisibility(!modelThought.isVisibility());
                    if (modelThought.visibility == true)
                        DownArrowThought.setImageResource(R.drawable.ic_baseline_down_24);
                    else if (modelThought.visibility == false)
                        DownArrowThought.setImageResource(R.drawable.ic_baseline_up_24);

                    notifyItemChanged(getAdapterPosition());
                }
            });

        }

        private void setThoughtImage(String Title){
            ThoughtImageID = Title;
            if(!ThoughtImageID.equals("")){
                ThoughtImageID = ThoughtImageID.toLowerCase();
                ThoughtImageID = ThoughtImageID.replaceAll(" ", "");
                ThoughtImageID = "thoughts_"+ThoughtImageID;
                int index = ThoughtImageID.indexOf(",");
                ThoughtImageID = ThoughtImageID.substring(0, index);
                ThoughtImageID = "@drawable/"+ThoughtImageID;

                String uri = ThoughtImageID;  // where myresource (without the extension) is the file
                int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
                Drawable res = context.getResources().getDrawable(imageResource);
                ThoughtImage.setImageDrawable(res);
            } else{

            }
        }

        private void drawChart(String mood) {
            String HighValueLable, HighValue;
            HighValue = mood.replaceAll(" ", "");
            HighValue = HighValue.replaceAll("[^\\d.]", "");

            HighValueLable = mood.replaceAll(" ", "");
            HighValueLable = HighValueLable.replaceAll("%", "");
            HighValueLable = HighValueLable.replaceAll("\\.", "");
            HighValueLable = HighValueLable.replaceAll("\\d","");

            ArrayList<String> BarLabel = new ArrayList<>();
            ArrayList<BarEntry> barEntries = new ArrayList<>();

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
            barEntries.add(new BarEntry(0, Float.parseFloat(HighValue)));
            BarLabel.add(HighValue + "% " + HighValueLable);
            barchart(mBarChartThought, barEntries, BarLabel);
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