package com.example.psyccare.DataAdapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.psyccare.DataModels.DiscoverDataModel;
import com.example.psyccare.R;
import com.example.psyccare.RecommendContent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.RecommendationsViewHolder> {

    Context context;
    ArrayList<DiscoverDataModel> discoverData;
    String firstNode, discoverTitle, dbTitle, dbContent, imageLink;

    public RecommendationsAdapter(Context context, ArrayList<DiscoverDataModel> discoverData) {
        this.context = context;
        this.discoverData = discoverData;
    }

    @NonNull
    @Override
    public RecommendationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recommend_card_design, parent, false);
        RecommendationsViewHolder recommendationsViewHolder = new RecommendationsViewHolder(view);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_anim);
        view.startAnimation(animation);
        return recommendationsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationsViewHolder holder, int position) {
        DiscoverDataModel items = discoverData.get(position);
        holder.title.setText(items.getTitle());
        holder.Summary.setText("A confidence boosting journal, great for beginners!");
        Glide.with(context).load(discoverData.get(position).getImageUri()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return discoverData.size();
    }

    public class RecommendationsViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout tapLayout;
        ImageView image;
        TextView title, Summary;
        DatabaseReference referenceToDiscover, referenceToFirstNode;
        ProgressDialog messageBox;

        public RecommendationsViewHolder(@NonNull View itemView) {
            super(itemView);
            tapLayout = itemView.findViewById(R.id.tapToShow);
            image = itemView.findViewById(R.id.cardImage);
            title = itemView.findViewById(R.id.cardHeading);
            Summary = itemView.findViewById(R.id.cardSummary);
            referenceToDiscover = FirebaseDatabase.getInstance().getReference("DiscoverData");

            messageBox = new ProgressDialog(context);
            messageBox.setTitle("");
            messageBox.setMessage("Loading...");
            messageBox.setCanceledOnTouchOutside(false);

            messageBox.setOnCancelListener(dialog -> ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE));

            tapLayout.setOnClickListener(v -> {
                messageBox.show();
                ((Activity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                DiscoverDataModel openItem = discoverData.get(getAdapterPosition());
                referenceToDiscover.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                firstNode = ds.getKey();
                                referenceToFirstNode = referenceToDiscover.child(firstNode);
                                referenceToFirstNode.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot dsInside : dataSnapshot.getChildren()) {
                                                if (dsInside.child("title").exists()) {
                                                    dbTitle = dsInside.child("title").getValue().toString();
                                                    if(dsInside.child("content").exists())
                                                        dbContent = dsInside.child("content").getValue().toString().replace("_","\n\n");
                                                    else
                                                        dbContent = "Ops! No content available at the moment.";
                                                    if(dsInside.child("imageUri").exists())
                                                        imageLink = dsInside.child("imageUri").getValue().toString();
                                                    else
                                                        imageLink = "";
                                                    discoverTitle = openItem.getTitle();
                                                    if (dbTitle.equals(discoverTitle)) {
                                                        Intent moveTo = new Intent(context.getApplicationContext(), RecommendContent.class);
                                                        moveTo.putExtra("activityTitle", dbTitle);
                                                        moveTo.putExtra("activityContent", dbContent);
                                                        moveTo.putExtra("activityImage", imageLink);
                                                        context.startActivity(moveTo);
                                                        messageBox.dismiss();
                                                        ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                        break;
                                                    }
                                                }
                                            }
                                        } else{
                                            messageBox.dismiss();
                                            ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Toast.makeText(context, "Node " + referenceToFirstNode.getKey() + " does not exist!!! ", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        messageBox.dismiss();
                                        ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        Toast.makeText(context, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            messageBox.dismiss();
                            ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(context, "Node " + referenceToDiscover.getKey() + " does not exist!!! ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        messageBox.dismiss();
                        ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }
}
