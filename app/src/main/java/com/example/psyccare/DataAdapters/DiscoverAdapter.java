package com.example.psyccare.DataAdapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.DiscoverViewHolder> {

    Context context;
    ArrayList<DiscoverDataModel> discoverData;
    String firstNode, discoverTitle, dbTitle, dbContent, imageLink;

    public DiscoverAdapter(Context context, ArrayList<DiscoverDataModel> discoverData) {
        this.context = context;
        this.discoverData = discoverData;
    }

    @NonNull
    @Override
    public DiscoverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.discover_card_design, parent, false);
        DiscoverViewHolder discoverViewHolder = new DiscoverViewHolder(view);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_anim);
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

        LinearLayout tapCard;
        ImageView image;
        TextView title;
        DatabaseReference referenceToDiscover, referenceToFirstNode;
        ProgressDialog messageBox;

        public DiscoverViewHolder(@NonNull View itemView) {
            super(itemView);

            tapCard = itemView.findViewById(R.id.tapCardToShow);
            image = itemView.findViewById(R.id.cardImage);
            title = itemView.findViewById(R.id.cardText);
            referenceToDiscover = FirebaseDatabase.getInstance().getReference("DiscoverData");

            messageBox = new ProgressDialog(context);
            messageBox.setTitle("");
            messageBox.setMessage("Loading...");
            messageBox.setCanceledOnTouchOutside(false);

            messageBox.setOnCancelListener(dialog -> ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE));

            tapCard.setOnClickListener(v -> {
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
