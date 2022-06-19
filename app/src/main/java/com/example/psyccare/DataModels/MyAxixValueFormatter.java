package com.example.psyccare.DataModels;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class MyAxixValueFormatter implements IAxisValueFormatter {
    private String[] mValues;

    public MyAxixValueFormatter(String[] Values) {
        this.mValues = Values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (((int) value) > -1) {
            if(mValues[((int) value)] != null)
                return mValues[((int) value)];
        }
        return "";
    }
}
