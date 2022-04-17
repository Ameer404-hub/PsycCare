package com.example.psyccare.DataAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.example.psyccare.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    int images[] = {
            R.drawable.afternoon_checkin1,
            R.drawable.afternoon_checkin2,
            R.drawable.morning_checkin1,
            R.drawable.morning_checkin2
    };

    int titles[] = {
            R.string.title1_str,
            R.string.title2_str,
            R.string.title3_str,
            R.string.title4_str
    };

    int descriptions[] = {
            R.string.desc1_str,
            R.string.desc2_str,
            R.string.desc3_str,
            R.string.desc4_str
    };

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.onboarding_slides, container, false);

        ImageView imageView = view.findViewById(R.id.sliderImage);
        TextView title = view.findViewById(R.id.sliderTitle);
        TextView description = view.findViewById(R.id.sliderDesc);

        imageView.setImageResource(images[position]);
        title.setText(titles[position]);
        description.setText(descriptions[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
