package com.example.psyccare.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

    RecyclerView discoverSec1, discoverSec2, discoverSec3, discoverSec4, discoverSec5,
            discoverSec6, discoverSec7, discoverSec8, discoverSec9, discoverSec10;
    DatabaseReference referenceDiscover;
    DiscoverAdapter adapter1, adapter2, adapter3, adapter4, adapter5, adapter6, adapter7, adapter8, adapter9, adapter10;
    ArrayList<DiscoverDataModel> discoverData1, discoverData2, discoverData3, discoverData4, discoverData5, discoverData6, discoverData7, discoverData8, discoverData9, discoverData10;
    ProgressDialog messageBox;
    RelativeLayout Layout1, Layout2, Layout3, Layout4, Layout5, Layout6, Layout7, Layout8, Layout9, Layout10, Layout11;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_discover, container, false);

        Layout1 = rootView.findViewById(R.id.Layout1);
        Layout2 = rootView.findViewById(R.id.Layout2);
        Layout3 = rootView.findViewById(R.id.Layout3);
        Layout4 = rootView.findViewById(R.id.Layout4);
        Layout5 = rootView.findViewById(R.id.Layout5);
        Layout6 = rootView.findViewById(R.id.Layout6);
        Layout7 = rootView.findViewById(R.id.Layout7);
        Layout8 = rootView.findViewById(R.id.Layout8);
        Layout9 = rootView.findViewById(R.id.Layout9);
        Layout10 = rootView.findViewById(R.id.Layout10);
        Layout11 = rootView.findViewById(R.id.Layout11);

        discoverSec1 = rootView.findViewById(R.id.discoverView1);
        discoverSec2 = rootView.findViewById(R.id.discoverView2);
        discoverSec3 = rootView.findViewById(R.id.discoverView3);
        discoverSec4 = rootView.findViewById(R.id.discoverView4);
        discoverSec5 = rootView.findViewById(R.id.discoverView5);
        discoverSec6 = rootView.findViewById(R.id.discoverView6);
        discoverSec7 = rootView.findViewById(R.id.discoverView7);
        discoverSec8 = rootView.findViewById(R.id.discoverView8);
        discoverSec9 = rootView.findViewById(R.id.discoverView9);
        discoverSec10 = rootView.findViewById(R.id.discoverView10);

        Layout1.setVisibility(View.GONE);
        Layout2.setVisibility(View.GONE);
        Layout3.setVisibility(View.GONE);
        Layout4.setVisibility(View.GONE);
        Layout5.setVisibility(View.GONE);
        Layout6.setVisibility(View.GONE);
        Layout7.setVisibility(View.GONE);
        Layout8.setVisibility(View.GONE);
        Layout9.setVisibility(View.GONE);
        Layout10.setVisibility(View.GONE);
        Layout11.setVisibility(View.GONE);

        referenceDiscover = FirebaseDatabase.getInstance().getReference("DiscoverData");

        messageBox = new ProgressDialog(getActivity());
        messageBox.setTitle("");
        messageBox.setMessage("Loading...");
        messageBox.setCanceledOnTouchOutside(false);

        discoverData1 = new ArrayList<>();
        discoverData2 = new ArrayList<>();
        discoverData3 = new ArrayList<>();
        discoverData4 = new ArrayList<>();
        discoverData5 = new ArrayList<>();
        discoverData6 = new ArrayList<>();
        discoverData7 = new ArrayList<>();
        discoverData8 = new ArrayList<>();
        discoverData9 = new ArrayList<>();
        discoverData10 = new ArrayList<>();

        adapter1 = new DiscoverAdapter(getActivity(), discoverData1);
        adapter2 = new DiscoverAdapter(getActivity(), discoverData2);
        adapter3 = new DiscoverAdapter(getActivity(), discoverData3);
        adapter4 = new DiscoverAdapter(getActivity(), discoverData4);
        adapter5 = new DiscoverAdapter(getActivity(), discoverData5);
        adapter6 = new DiscoverAdapter(getActivity(), discoverData6);
        adapter7 = new DiscoverAdapter(getActivity(), discoverData7);
        adapter8 = new DiscoverAdapter(getActivity(), discoverData8);
        adapter9 = new DiscoverAdapter(getActivity(), discoverData9);
        adapter10 = new DiscoverAdapter(getActivity(), discoverData10);

        discoverSec1.setHasFixedSize(true);
        discoverSec2.setHasFixedSize(true);
        discoverSec3.setHasFixedSize(true);
        discoverSec4.setHasFixedSize(true);
        discoverSec5.setHasFixedSize(true);
        discoverSec6.setHasFixedSize(true);
        discoverSec7.setHasFixedSize(true);
        discoverSec8.setHasFixedSize(true);
        discoverSec9.setHasFixedSize(true);
        discoverSec10.setHasFixedSize(true);

        discoverSec1.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        discoverSec2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        discoverSec3.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        discoverSec4.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        discoverSec5.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        discoverSec6.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        discoverSec7.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        discoverSec8.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        discoverSec9.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        discoverSec10.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        discoverSec1.setAdapter(adapter1);
        discoverSec2.setAdapter(adapter2);
        discoverSec3.setAdapter(adapter3);
        discoverSec4.setAdapter(adapter4);
        discoverSec5.setAdapter(adapter5);
        discoverSec6.setAdapter(adapter6);
        discoverSec7.setAdapter(adapter7);
        discoverSec8.setAdapter(adapter8);
        discoverSec9.setAdapter(adapter9);
        discoverSec10.setAdapter(adapter10);

        if (isConnected(this)) {
            messageBox.show();
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            getDiscoverData();
        } else
            Toast.makeText(getActivity(), "You're device is not connected to internet", Toast.LENGTH_LONG).show();

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent moveTo = new Intent(getActivity(), HomeContainer.class);
                startActivity(moveTo);
                getActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        messageBox.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });

        return rootView;
    }

    private void getDiscoverData() {
        referenceDiscover.child("Introduction").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        DiscoverDataModel newModel1 = ds.getValue(DiscoverDataModel.class);
                        discoverData1.add(newModel1);
                        Layout1.setVisibility(View.VISIBLE);
                        Layout2.setVisibility(View.VISIBLE);
                    }
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), "getDiscoverData() error: DataSnapShot does not exist!!!", Toast.LENGTH_SHORT).show();
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

        /*referenceDiscover.child("Popular").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        DiscoverDataModel newModel2 = ds.getValue(DiscoverDataModel.class);
                        discoverData2.add(newModel2);
                        Layout3.setVisibility(View.VISIBLE);
                    }
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), "getDiscoverData() error: DataSnapShot does not exist!!!", Toast.LENGTH_SHORT).show();
                }
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                messageBox.dismiss();
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/

        referenceDiscover.child("Mental Health Guides").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        DiscoverDataModel newModel10 = ds.getValue(DiscoverDataModel.class);
                        discoverData10.add(newModel10);
                        Layout11.setVisibility(View.VISIBLE);
                    }
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), "getDiscoverData() error: DataSnapShot does not exist!!!", Toast.LENGTH_SHORT).show();
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

        referenceDiscover.child("Thoughts & Feelings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        DiscoverDataModel newModel3 = ds.getValue(DiscoverDataModel.class);
                        discoverData3.add(newModel3);
                        Layout4.setVisibility(View.VISIBLE);
                    }
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), "getDiscoverData() error: DataSnapShot does not exist!!!", Toast.LENGTH_SHORT).show();
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

        referenceDiscover.child("Relationships").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        DiscoverDataModel newModel4 = ds.getValue(DiscoverDataModel.class);
                        discoverData4.add(newModel4);
                        Layout5.setVisibility(View.VISIBLE);
                    }
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), "getDiscoverData() error: DataSnapShot does not exist!!!", Toast.LENGTH_SHORT).show();
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

        referenceDiscover.child("Tackling Unhelpful Thoughts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        DiscoverDataModel newModel5 = ds.getValue(DiscoverDataModel.class);
                        discoverData5.add(newModel5);
                        Layout6.setVisibility(View.VISIBLE);
                    }
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), "getDiscoverData() error: DataSnapShot does not exist!!!", Toast.LENGTH_SHORT).show();
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

        referenceDiscover.child("Growth Mindset").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        DiscoverDataModel newModel6 = ds.getValue(DiscoverDataModel.class);
                        discoverData6.add(newModel6);
                        Layout7.setVisibility(View.VISIBLE);
                    }
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), "getDiscoverData() error: DataSnapShot does not exist!!!", Toast.LENGTH_SHORT).show();
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

        referenceDiscover.child("Mindfulness").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        DiscoverDataModel newModel7 = ds.getValue(DiscoverDataModel.class);
                        discoverData7.add(newModel7);
                        Layout8.setVisibility(View.VISIBLE);
                    }
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), "getDiscoverData() error: DataSnapShot does not exist!!!", Toast.LENGTH_SHORT).show();
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

        referenceDiscover.child("Gratitude").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        DiscoverDataModel newModel8 = ds.getValue(DiscoverDataModel.class);
                        discoverData8.add(newModel8);
                        Layout9.setVisibility(View.VISIBLE);
                    }
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), "getDiscoverData() error: DataSnapShot does not exist!!!", Toast.LENGTH_SHORT).show();
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

        referenceDiscover.child("Habits Assessment").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        DiscoverDataModel newModel9 = ds.getValue(DiscoverDataModel.class);
                        discoverData9.add(newModel9);
                        Layout10.setVisibility(View.VISIBLE);
                    }
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    messageBox.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity(), "getDiscoverData() error: DataSnapShot does not exist!!!", Toast.LENGTH_SHORT).show();
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