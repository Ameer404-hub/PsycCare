package com.example.psyccare;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OngoingPsycProb extends AppCompatActivity {

    LinearLayout psycProbLayout;
    int selectCount = 0;
    private Handler handler;
    ProgressDialog messageBox;
    DatabaseReference referenceUser;
    String psycProbs = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_psyc_prob);

        psycProbLayout = findViewById(R.id.psycProbLayout);

        referenceUser = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getUid());

        messageBox = new ProgressDialog(this);
        messageBox.setTitle("");
        messageBox.setMessage("Setting up your preferences...");

        LayoutAnimationController layoutAnimationController =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_falldown);
        psycProbLayout.setLayoutAnimation(layoutAnimationController);
    }

    public void Depression(View view) {
        RelativeLayout depression = findViewById(R.id.probDepression);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            selectCount++;
            psycProbs = psycProbs + "Depression,";
            depression.setBackground(getDrawable(R.drawable.bg_for_tapped_ii));
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            selectCount--;
            psycProbs = psycProbs.replace("Depression,", "");
            depression.setBackground(getDrawable(R.drawable.bg_for_untapped_ii));
        }
    }

    public void PTSD(View view) {
        RelativeLayout PTSD = findViewById(R.id.probStress);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            selectCount++;
            psycProbs = psycProbs + "Post Traumatic Stress Disorder,";
            PTSD.setBackground(getDrawable(R.drawable.bg_for_tapped_ii));
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            selectCount--;
            psycProbs = psycProbs.replace("Post Traumatic Stress Disorder,", "");
            PTSD.setBackground(getDrawable(R.drawable.bg_for_untapped_ii));
        }
    }

    public void Anxiety(View view) {
        RelativeLayout Anxiety = findViewById(R.id.probAnxiety);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            selectCount++;
            psycProbs = psycProbs + "Anxiety,";
            Anxiety.setBackground(getDrawable(R.drawable.bg_for_tapped_ii));
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            selectCount--;
            psycProbs = psycProbs.replace("Anxiety,", "");
            Anxiety.setBackground(getDrawable(R.drawable.bg_for_untapped_ii));
        }
    }

    public void moodDisorder(View view) {
        RelativeLayout moodDisorder = findViewById(R.id.probMood);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            selectCount++;
            psycProbs = psycProbs + "Mood Disorder,";
            moodDisorder.setBackground(getDrawable(R.drawable.bg_for_tapped_ii));
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            selectCount--;
            psycProbs = psycProbs.replace("Mood Disorder,", "");
            moodDisorder.setBackground(getDrawable(R.drawable.bg_for_untapped_ii));
        }
    }

    public void Psychotic(View view) {
        RelativeLayout Psychotic = findViewById(R.id.probPsychotic);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            selectCount++;
            psycProbs = psycProbs + "Psychotic Disorder,";
            Psychotic.setBackground(getDrawable(R.drawable.bg_for_tapped_ii));
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            selectCount--;
            psycProbs = psycProbs.replace("Psychotic Disorder,", "");
            Psychotic.setBackground(getDrawable(R.drawable.bg_for_untapped_ii));
        }
    }

    public void OCD(View view) {
        RelativeLayout OCD = findViewById(R.id.probOCD);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            selectCount++;
            psycProbs = psycProbs + "Obsessive-Compulsive  Disorder,";
            OCD.setBackground(getDrawable(R.drawable.bg_for_tapped_ii));
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            selectCount--;
            psycProbs = psycProbs.replace("Obsessive-Compulsive Disorder,", "");
            OCD.setBackground(getDrawable(R.drawable.bg_for_untapped_ii));
        }
    }

    public void IED(View view) {
        RelativeLayout IED = findViewById(R.id.probIED);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            selectCount++;
            psycProbs = psycProbs + "Intermittent Explosive  Disorder,";
            IED.setBackground(getDrawable(R.drawable.bg_for_tapped_ii));
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            selectCount--;
            psycProbs = psycProbs.replace("Intermittent Explosive Disorder,", "");
            IED.setBackground(getDrawable(R.drawable.bg_for_untapped_ii));
        }
    }

    public void Personality(View view) {
        RelativeLayout Personality = findViewById(R.id.probPersonality);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            selectCount++;
            psycProbs = psycProbs + "Personality  Disorder,";
            Personality.setBackground(getDrawable(R.drawable.bg_for_tapped_ii));
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            selectCount--;
            psycProbs = psycProbs.replace("Personality  Disorder,", "");
            Personality.setBackground(getDrawable(R.drawable.bg_for_untapped_ii));
        }
    }

    public void Phobia(View view) {
        RelativeLayout Phobia = findViewById(R.id.probPhobia);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            selectCount++;
            psycProbs = psycProbs + "Social Phobia,";
            Phobia.setBackground(getDrawable(R.drawable.bg_for_tapped_ii));
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            selectCount--;
            psycProbs = psycProbs.replace("Social Phobia,", "");
            Phobia.setBackground(getDrawable(R.drawable.bg_for_untapped_ii));
        }
    }

    public void skipScreen(View view) {
        if (isConnected(this)) {
            handler = new Handler();
            messageBox.show();
            messageBox.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    referenceUser.child("ReportedProblems").child("PsycProblems").setValue("NA");
                    messageBox.dismiss();
                    messageBox.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                }
            }, 1000);
        } else
            Toast.makeText(this, "You're device is not connected to internet", Toast.LENGTH_LONG).show();
    }

    public void nextScreen(View view) {
        if (selectCount == 0) {
            Toast.makeText(this, "Please select at least one psychological problem", Toast.LENGTH_SHORT).show();
        } else {
            if (isConnected(this)) {
                handler = new Handler();
                messageBox.show();
                messageBox.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        referenceUser.child("ReportedProblems").child("PsycProblems").setValue(psycProbs);
                        messageBox.dismiss();
                        messageBox.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }
                }, 2500);
            } else
                Toast.makeText(this, "You're device is not connected to internet", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isConnected(OngoingPsycProb CheckInternet) {
        ConnectivityManager connectivityManager = (ConnectivityManager) CheckInternet.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiCon != null && wifiCon.isConnected()) || (mobileCon != null && mobileCon.isConnected()))
            return true;
        else
            return false;
    }
}