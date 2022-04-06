package com.example.psyccare.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.psyccare.DataAdapters.DiscoverAdapter;
import com.example.psyccare.DataModels.DiscoverDataModel;
import com.example.psyccare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DiscoverFragment extends Fragment {

    RecyclerView viewIntroduction, viewFeatured;
    DatabaseReference referenceToDiscover;
    DiscoverAdapter adapter;
    ArrayList<DiscoverDataModel> discoverData;
    ProgressDialog messageBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_discover, container, false);

        viewIntroduction = rootView.findViewById(R.id.discoverView);
        viewFeatured = rootView.findViewById(R.id.discoverView2);
        referenceToDiscover = FirebaseDatabase.getInstance().getReference("DiscoverData");

        messageBox = new ProgressDialog(getActivity());
        messageBox.setTitle("");
        messageBox.setMessage("Loading...");

        discoverData = new ArrayList<>();
        adapter = new DiscoverAdapter(getActivity(), discoverData);

        viewIntroduction.setHasFixedSize(true);
        viewFeatured.setHasFixedSize(true);
        viewIntroduction.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        viewFeatured.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        viewIntroduction.setAdapter(adapter);
        viewFeatured.setAdapter(adapter);

        messageBox.show();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        referenceToDiscover.child("Introduction").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        DiscoverDataModel newModel = ds.getValue(DiscoverDataModel.class);
                        discoverData.add(newModel);
                    }
                }
                else{
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), "Snapshot does not exist", Toast.LENGTH_SHORT).show();
                }
                messageBox.dismiss();
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return  rootView;
    }
}