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

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.psyccare.HomeContainer;
import com.example.psyccare.Login;
import com.example.psyccare.MyStats;
import com.example.psyccare.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class More extends Fragment {

    LinearLayout Stats, crisisSupport, psycProbs, recommend, settings, aboutUs, logOut;
    private GoogleSignInClient mGoogleSignInClient;
    ProgressDialog messageBox;
    Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_more, container, false);

        messageBox = new ProgressDialog(getActivity());
        messageBox.setTitle("");
        messageBox.setMessage("logging out...");

        Stats = rootView.findViewById(R.id.myStats);
        crisisSupport = rootView.findViewById(R.id.support);
        psycProbs = rootView.findViewById(R.id.onGoingProbs);
        recommend = rootView.findViewById(R.id.recommend);
        settings = rootView.findViewById(R.id.settings);
        aboutUs = rootView.findViewById(R.id.aboutUs);
        logOut = rootView.findViewById(R.id.logOut);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (signInAccount != null) {

        }

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN);

        Stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveTo = new Intent(getActivity(), MyStats.class);
                startActivity(moveTo);
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isConnected(More.this)) {
                    messageBox.show();
                    messageBox.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    userLogOut();
                } else {
                    Toast.makeText(getActivity(), "You're device is not connected to internet", Toast.LENGTH_LONG).show();
                }
            }
        });

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

    private void userLogOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity(), Login.class);
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            messageBox.dismiss();
                            messageBox.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }, 750);

                }
            }
        });
    }

    private boolean isConnected(More CheckInternet) {
        ConnectivityManager connectivityManager = (ConnectivityManager) CheckInternet.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiCon != null && wifiCon.isConnected()) || (mobileCon != null && mobileCon.isConnected()))
            return true;
        else
            return false;
    }
}