package com.example.psyccare.DataAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.psyccare.DataModels.DiscoverDataModel;
import com.example.psyccare.HomeActivity;
import com.example.psyccare.R;

import java.util.ArrayList;

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.DiscoverViewHolder> {

    Context context;
    ArrayList<DiscoverDataModel> discoverData;

    public DiscoverAdapter(Context context, ArrayList<DiscoverDataModel> discoverData) {
        this.context = context;
        this.discoverData = discoverData;
    }

    @NonNull
    @Override
    public DiscoverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.discover_card_design, parent, false);
        DiscoverViewHolder discoverViewHolder = new DiscoverViewHolder(view);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fui_slide_in_right);
        view.startAnimation(animation);
        return discoverViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverViewHolder holder, int position) {
        DiscoverDataModel items = discoverData.get(position);
        holder.title.setText(items.getTitle());
        Glide.with(context).load(discoverData.get(position).getImageUri()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return discoverData.size();
    }

    public class DiscoverViewHolder extends RecyclerView.ViewHolder {

        CardView cardLayout;
        ImageView image;
        TextView title;

        public DiscoverViewHolder(@NonNull View itemView) {
            super(itemView);

            cardLayout = itemView.findViewById(R.id.cardLayout);
            image = itemView.findViewById(R.id.cardImage);
            title = itemView.findViewById(R.id.cardText);

            cardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DiscoverDataModel openItem = discoverData.get(getAdapterPosition());
                    String title = openItem.getTitle();
                    if (title.equals("Getting Started")) {
                        Intent moveTo = new Intent(context.getApplicationContext(), HomeActivity.class);
                        context.startActivity(moveTo);
                    }
                }
            });
        }
    }
}
