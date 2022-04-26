package com.example.psyccare.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.psyccare.DataAdapters.DiscoverAdapter;
import com.example.psyccare.DataModels.DiscoverDataModel;
import com.example.psyccare.HomeContainer;
import com.example.psyccare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Discover extends Fragment {

    RecyclerView recyclerView1, recyclerView2, recyclerView3, recyclerView4;
    DatabaseReference referenceDiscover;
    DiscoverAdapter adapter1, adapter2;
    ArrayList<DiscoverDataModel> discoverData1, discoverData2;
    ProgressDialog messageBox;
    RelativeLayout Layout1, Layout2, Layout3, Layout4, Layout5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_discover, container, false);

        recyclerView1 = rootView.findViewById(R.id.discoverView1);
        recyclerView2 = rootView.findViewById(R.id.discoverView2);
        recyclerView3 = rootView.findViewById(R.id.discoverView3);
        recyclerView4 = rootView.findViewById(R.id.discoverView4);
        Layout1 = rootView.findViewById(R.id.Layout1);
        Layout2 = rootView.findViewById(R.id.Layout2);
        Layout3 = rootView.findViewById(R.id.Layout3);
        Layout4 = rootView.findViewById(R.id.Layout4);
        Layout5 = rootView.findViewById(R.id.Layout5);

        Layout1.setVisibility(View.GONE);
        Layout2.setVisibility(View.GONE);
        Layout3.setVisibility(View.GONE);
        Layout4.setVisibility(View.GONE);
        Layout5.setVisibility(View.GONE);

        referenceDiscover = FirebaseDatabase.getInstance().getReference("DiscoverData");

        messageBox = new ProgressDialog(getActivity());
        messageBox.setTitle("");
        messageBox.setMessage("Loading...");

        discoverData1 = new ArrayList<>();
        discoverData2 = new ArrayList<>();

        adapter1 = new DiscoverAdapter(getActivity(), discoverData1);
        adapter2 = new DiscoverAdapter(getActivity(), discoverData2);

        recyclerView1.setHasFixedSize(true);
        recyclerView2.setHasFixedSize(true);
        recyclerView3.setHasFixedSize(true);
        recyclerView4.setHasFixedSize(true);

        recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView3.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView4.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        recyclerView1.setAdapter(adapter1);
        recyclerView2.setAdapter(adapter2);
        recyclerView3.setAdapter(adapter1);
        recyclerView4.setAdapter(adapter2);

        if (isConnected(this)) {
            messageBox.show();
            messageBox.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            getDiscoverData();
        } else {
            Toast.makeText(getActivity(), "You're device is not connected to internet", Toast.LENGTH_LONG).show();
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent moveTo = new Intent(getActivity(), HomeContainer.class);
                startActivity(moveTo);
                getActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        return rootView;
    }

    private void getDiscoverData() {
        referenceDiscover.child("Introduction").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        DiscoverDataModel newModel = ds.getValue(DiscoverDataModel.class);
                        discoverData1.add(newModel);

                        Layout1.setVisibility(View.VISIBLE);
                        Layout2.setVisibility(View.VISIBLE);
                        Layout3.setVisibility(View.VISIBLE);
                        Layout4.setVisibility(View.VISIBLE);
                        Layout5.setVisibility(View.VISIBLE);

                        LayoutAnimationController layoutAnimationController =
                                AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_falldown);

                        Layout1.setLayoutAnimation(layoutAnimationController);
                        Layout2.setLayoutAnimation(layoutAnimationController);
                        Layout3.setLayoutAnimation(layoutAnimationController);
                        Layout4.setLayoutAnimation(layoutAnimationController);
                        Layout5.setLayoutAnimation(layoutAnimationController);
                    }
                    messageBox.dismiss();
                    messageBox.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), "Error while fetcing data", Toast.LENGTH_SHORT).show();
                }
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                messageBox.dismiss();
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        referenceDiscover.child("Thoughts & Feelings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        DiscoverDataModel newModel = ds.getValue(DiscoverDataModel.class);
                        discoverData2.add(newModel);
                    }
                    messageBox.dismiss();
                    messageBox.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    messageBox.dismiss();
                    messageBox.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), "Error while fetcing data", Toast.LENGTH_SHORT).show();
                }
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                messageBox.dismiss();
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isConnected(Discover CheckInternet) {
        ConnectivityManager connectivityManager = (ConnectivityManager) CheckInternet.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiCon != null && wifiCon.isConnected()) || (mobileCon != null && mobileCon.isConnected()))
            return true;
        else
            return false;
    }
}