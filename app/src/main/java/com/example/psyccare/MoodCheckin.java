package com.example.psyccare;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.psyccare.DataModels.checkInModel;

import org.tensorflow.lite.examples.textclassification.client.Result;
import org.tensorflow.lite.examples.textclassification.client.TextClassificationClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MoodCheckin extends AppCompatActivity {

    private static final String TAG = "TextClassificationDemo";
    private TextClassificationClient client;
    ImageView submitMood;
    LinearLayout Snackbar_layout;
    Handler handler;
    ProgressDialog messageBox;
    TextInputLayout moodMessageBox;
    String Type = "", Desc = "", classifiedAs = "", Date = "", Time = "";
    DatabaseReference referenceToMoodCheckin;
    SimpleDateFormat dateFormat, timeFormat;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_checkin);

        client = new TextClassificationClient(getApplicationContext());
        handler = new Handler();

        submitMood = findViewById(R.id.sendMoodNote);
        Snackbar_layout = findViewById(R.id.moodIconsLayout);
        moodMessageBox = findViewById(R.id.mood_TextArea);

        referenceToMoodCheckin = FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getUid()).child("MoodCheckIns");

        messageBox = new ProgressDialog(MoodCheckin.this);
        messageBox.setTitle("");
        messageBox.setMessage("Loading...");

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
        timeFormat = new SimpleDateFormat("hh:mm aaa");
        Date = dateFormat.format(calendar.getTime());
        Time = timeFormat.format(calendar.getTime());

        submitMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected(MoodCheckin.this))
                    Toast.makeText(MoodCheckin.this, "You're device is not connected to internet", Toast.LENGTH_LONG).show();
                else {
                    messageBox.show();
                    messageBox.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Desc = moodMessageBox.getEditText().getText().toString();
                    classify(Desc);
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            messageBox.dismiss();
                            messageBox.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Intent moveTo = new Intent(getApplicationContext(), ThoughtCheckin.class);
                            startActivity(moveTo);
                            finish();
                        }
                    }, 750);
                }

            }
        });
    }

    private boolean isConnected(MoodCheckin CheckInternet) {
        ConnectivityManager connectivityManager = (ConnectivityManager) CheckInternet.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiCon != null && wifiCon.isConnected()) || (mobileCon != null && mobileCon.isConnected()))
            return true;
        else
            return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        handler.post(
                () -> {
                    client.load();
                }
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.post(
                () -> {
                    client.load();
                }
        );
    }

    private void classify(final String text) {
        handler.post(
                () -> {
                    // Run text classification with TF Lite.
                    List<Result> results = client.classify(text);
                    // Show classification result on screen
                    showResult(results);
                });
    }

    private void showResult(final List<Result> results) {
        runOnUiThread(
                () -> {
                    ArrayList<String> labels = new ArrayList<>();
                    ArrayList<Float> probability = new ArrayList<>();

                    for (int i = 0; i < results.size(); i++) {
                        Result result = results.get(i);
                        labels.add(result.getTitle());   // Extract labels
                        probability.add(result.getConfidence());  // Extract confidence
                    }

                    float tempProb;
                    String tempLable;
                    for (int i = 0; i < probability.size(); i++) {
                        for (int j = i + 1; j < probability.size(); j++) {
                            if (probability.get(i) > probability.get(j)) {
                                tempProb = probability.get(i);
                                probability.set(i, probability.get(j));
                                probability.set(j, tempProb);

                                tempLable = labels.get(i);
                                labels.set(i, labels.get(j));
                                labels.set(j, tempLable);
                            }
                        }
                    }

                    String HighValueLable;
                    Float HighValue;
                    HighValueLable = labels.get(probability.size() - 1);
                    HighValue = probability.get(probability.size() - 1);
                    classifiedAs = HighValueLable + " " + Math.round(HighValue * 1000) / 10.0 + "%";
                    insertCheckin();
                });
    }

    public void Happy(View view) {
        LinearLayout moodHappy = findViewById(R.id.moodHappy);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            moodHappy.setBackground(getDrawable(R.drawable.bg_for_tapped));
            Type = Type + "Happy,";
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            moodHappy.setBackground(getDrawable(R.drawable.bg_for_untapped));
            Type = Type.replace("Happy,", "");
        }
    }

    public void Sad(View view) {
        LinearLayout moodSad = findViewById(R.id.moodSad);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            moodSad.setBackground(getDrawable(R.drawable.bg_for_tapped));
            Type = Type + "Sad,";
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            moodSad.setBackground(getDrawable(R.drawable.bg_for_untapped));
            Type = Type.replace("Sad,", "");
        }
    }

    public void Angry(View view) {
        LinearLayout moodAngry = findViewById(R.id.moodAngry);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            moodAngry.setBackground(getDrawable(R.drawable.bg_for_tapped));
            Type = Type + "Angry,";
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            moodAngry.setBackground(getDrawable(R.drawable.bg_for_untapped));
            Type = Type.replace("Angry,", "");
        }
    }

    public void Anxious(View view) {
        LinearLayout moodAnxious = findViewById(R.id.moodAnxious);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            moodAnxious.setBackground(getDrawable(R.drawable.bg_for_tapped));
            Type = Type + "Anxious,";
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            moodAnxious.setBackground(getDrawable(R.drawable.bg_for_untapped));
            Type = Type.replace("Anxious,", "");
        }
    }

    public void Excited(View view) {
        LinearLayout moodExcited = findViewById(R.id.moodExcited);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            moodExcited.setBackground(getDrawable(R.drawable.bg_for_tapped));
            Type = Type + "Excited,";
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            moodExcited.setBackground(getDrawable(R.drawable.bg_for_untapped));
            Type = Type.replace("Excited,", "");
        }
    }

    public void Stressed(View view) {
        LinearLayout moodStressed = findViewById(R.id.moodStressed);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            moodStressed.setBackground(getDrawable(R.drawable.bg_for_tapped));
            Type = Type + "Stressed,";
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            moodStressed.setBackground(getDrawable(R.drawable.bg_for_untapped));
            Type = Type.replace("Stressed,", "");
        }
    }

    public void Awesome(View view) {
        LinearLayout moodAwesome = findViewById(R.id.moodAwesome);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            moodAwesome.setBackground(getDrawable(R.drawable.bg_for_tapped));
            Type = Type + "Awesome,";
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            moodAwesome.setBackground(getDrawable(R.drawable.bg_for_untapped));
            Type = Type.replace("Awesome,", "");
        }
    }

    public void Terrible(View view) {
        LinearLayout moodTerrible = findViewById(R.id.moodTerrible);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            moodTerrible.setBackground(getDrawable(R.drawable.bg_for_tapped));
            Type = Type + "Terrible,";
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            moodTerrible.setBackground(getDrawable(R.drawable.bg_for_untapped));
            Type = Type.replace("Terrible,", "");
        }
    }

    public void Tired(View view) {
        LinearLayout moodTired = findViewById(R.id.moodTired);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            moodTired.setBackground(getDrawable(R.drawable.bg_for_tapped));
            Type = Type + "Tired,";
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            moodTired.setBackground(getDrawable(R.drawable.bg_for_untapped));
            Type = Type.replace("Tired,", "");
        }
    }

    public void Hopeful(View view) {
        LinearLayout moodHopeful = findViewById(R.id.moodHopeful);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            moodHopeful.setBackground(getDrawable(R.drawable.bg_for_tapped));
            Type = Type + "Hopeful,";
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            moodHopeful.setBackground(getDrawable(R.drawable.bg_for_untapped));
            Type = Type.replace("Hopeful,", "");
        }
    }

    public void Okay(View view) {
        LinearLayout moodOkay = findViewById(R.id.moodOkay);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            moodOkay.setBackground(getDrawable(R.drawable.bg_for_tapped));
            Type = Type + "Okay,";
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            moodOkay.setBackground(getDrawable(R.drawable.bg_for_untapped));
            Type = Type.replace("Okay,", "");
        }
    }

    public void Calm(View view) {
        LinearLayout moodCalm = findViewById(R.id.moodCalm);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            moodCalm.setBackground(getDrawable(R.drawable.bg_for_tapped));
            Type = Type + "Calm,";
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            moodCalm.setBackground(getDrawable(R.drawable.bg_for_untapped));
            Type = Type.replace("Calm,", "");
        }
    }

    public void Satisfied(View view) {
        LinearLayout moodSatisfied = findViewById(R.id.moodSatisfied);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            moodSatisfied.setBackground(getDrawable(R.drawable.bg_for_tapped));
            Type = Type + "Satisfied,";
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            moodSatisfied.setBackground(getDrawable(R.drawable.bg_for_untapped));
            Type = Type.replace("Satisfied,", "");
        }
    }

    public void Frustrated(View view) {
        LinearLayout moodFrustated = findViewById(R.id.moodFrustated);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            moodFrustated.setBackground(getDrawable(R.drawable.bg_for_tapped));
            Type = Type + "Frustrated,";
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            moodFrustated.setBackground(getDrawable(R.drawable.bg_for_untapped));
            Type = Type.replace("Frustrated,", "");
        }
    }

    public void notSure(View view) {
        LinearLayout moodnotSure = findViewById(R.id.moodNotSure);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            moodnotSure.setBackground(getDrawable(R.drawable.bg_for_tapped));
            Type = Type + "Not Sure,";
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            moodnotSure.setBackground(getDrawable(R.drawable.bg_for_untapped));
            Type = Type.replace("Not Sure,", "");
        }
    }

    public void insertCheckin() {
        checkInModel moodCheckIn = new checkInModel(Date, Time, Type, Desc, classifiedAs);
        referenceToMoodCheckin.child(Date + " " + Time).setValue(moodCheckIn);
    }

    public void onBackPressed() {
        Snackbar snackbar = Snackbar.make(Snackbar_layout, "Are you sure to exit Mood Check-in?", Snackbar.LENGTH_LONG)
                .setAction("Exit", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent goBack = new Intent(getApplicationContext(), HomeContainer.class);
                        startActivity(goBack);
                        finish();
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }
}