package com.example.psyccare;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
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

import com.example.psyccare.DataModels.checkInModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tensorflow.lite.examples.textclassification.client.Result;
import org.tensorflow.lite.examples.textclassification.client.TextClassificationClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ThoughtCheckin extends AppCompatActivity {

    private TextClassificationClient client;
    ImageView submitThought;
    LinearLayout Snackbar_layout;
    Handler handler;
    TextInputLayout thoughtMessageBox;
    ProgressDialog messageBox;
    String Type = "", Desc = "", classifiedAs = "", perCent = "", Month = "", Date = "", Time = "", dbHeading;
    DatabaseReference referenceToCheckin;
    SimpleDateFormat dateFormat, timeFormat, monthFormat;
    Calendar calendar;
    int selectCount = 0;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thought_checkin);

        client = new TextClassificationClient(getApplicationContext());
        handler = new Handler();

        submitThought = findViewById(R.id.sendThoughtNote);
        Snackbar_layout = findViewById(R.id.snackBarAction);
        thoughtMessageBox = findViewById(R.id.thought_TextArea);

        referenceToCheckin = FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getUid()).child("ThoughtCheckIns");

        messageBox = new ProgressDialog(ThoughtCheckin.this);
        messageBox.setTitle("");
        messageBox.setMessage("Loading...");
        messageBox.setCanceledOnTouchOutside(false);

        calendar = Calendar.getInstance();
        monthFormat = new SimpleDateFormat("MMM, yyyy");
        dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
        timeFormat = new SimpleDateFormat("hh:mm aaa");
        Date = dateFormat.format(calendar.getTime());
        Time = timeFormat.format(calendar.getTime());
        Month = monthFormat.format(calendar.getTime());

        submitThought.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected(ThoughtCheckin.this))
                    Toast.makeText(ThoughtCheckin.this, "You're device is not connected to internet", Toast.LENGTH_LONG).show();
                else {
                    Desc = thoughtMessageBox.getEditText().getText().toString();
                    classify(Desc);
                }
            }
        });
        messageBox.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    public void Positive(View view) {
        MaterialButton thoughtPositive = findViewById(R.id.thoughtPositive);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            thoughtPositive.setBackgroundColor(getColor(R.color.teal_700));
            thoughtPositive.setStrokeColor(ColorStateList.valueOf(Color.BLACK));
            thoughtPositive.setStrokeWidth(2);
            Type = Type + "Positive";
            selectCount++;
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            thoughtPositive.setBackgroundColor(getColor(R.color.ButtonColorThird));
            thoughtPositive.setStrokeWidth(0);
            Type = Type.replace("Positive", "");
            selectCount--;
        }
    }

    public void Negative(View view) {
        MaterialButton thoughtNegative = findViewById(R.id.thoughtNegative);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            thoughtNegative.setBackgroundColor(getColor(R.color.teal_700));
            thoughtNegative.setStrokeColor(ColorStateList.valueOf(Color.BLACK));
            thoughtNegative.setStrokeWidth(2);
            Type = Type + "Negative";
            selectCount++;
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            thoughtNegative.setBackgroundColor(getColor(R.color.ButtonColorThird));
            thoughtNegative.setStrokeWidth(0);
            Type = Type.replace("Negative", "");
            selectCount--;
        }
    }

    public void Emotional(View view) {
        MaterialButton thoughtEmotional = findViewById(R.id.thoughtEmotional);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            thoughtEmotional.setBackgroundColor(getColor(R.color.teal_700));
            thoughtEmotional.setStrokeColor(ColorStateList.valueOf(Color.BLACK));
            thoughtEmotional.setStrokeWidth(2);
            Type = Type + "Emotional";
            selectCount++;
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            thoughtEmotional.setBackgroundColor(getColor(R.color.ButtonColorThird));
            thoughtEmotional.setStrokeWidth(0);
            Type = Type.replace("Emotional", "");
            selectCount--;
        }
    }

    public void Blaming(View view) {
        MaterialButton thoughtBlaming = findViewById(R.id.thoughtBlaming);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            thoughtBlaming.setBackgroundColor(getColor(R.color.teal_700));
            thoughtBlaming.setStrokeColor(ColorStateList.valueOf(Color.BLACK));
            thoughtBlaming.setStrokeWidth(2);
            Type = Type + "Blaming";
            selectCount++;
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            thoughtBlaming.setBackgroundColor(getColor(R.color.ButtonColorThird));
            thoughtBlaming.setStrokeWidth(0);
            Type = Type.replace("Blaming", "");
            selectCount--;
        }
    }

    public void Fear(View view) {
        MaterialButton thoughtFear = findViewById(R.id.thoughtFear);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            thoughtFear.setBackgroundColor(getColor(R.color.teal_700));
            thoughtFear.setStrokeColor(ColorStateList.valueOf(Color.BLACK));
            thoughtFear.setStrokeWidth(2);
            Type = Type + "Fear";
            selectCount++;
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            thoughtFear.setBackgroundColor(getColor(R.color.ButtonColorThird));
            thoughtFear.setStrokeWidth(0);
            Type = Type.replace("Fear", "");
            selectCount--;
        }
    }

    public void Catastrophic(View view) {
        MaterialButton thoughtCatastrophic = findViewById(R.id.thoughtCatastrophic);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            thoughtCatastrophic.setBackgroundColor(getColor(R.color.teal_700));
            thoughtCatastrophic.setStrokeColor(ColorStateList.valueOf(Color.BLACK));
            thoughtCatastrophic.setStrokeWidth(2);
            Type = Type + "Catastrophic";
            selectCount++;
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            thoughtCatastrophic.setBackgroundColor(getColor(R.color.ButtonColorThird));
            thoughtCatastrophic.setStrokeWidth(0);
            Type = Type.replace("Catastrophic", "");
            selectCount--;
        }
    }

    public void Calamitous(View view) {
        MaterialButton thoughtCalamitous = findViewById(R.id.thoughtCalamitous);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            thoughtCalamitous.setBackgroundColor(getColor(R.color.teal_700));
            thoughtCalamitous.setStrokeColor(ColorStateList.valueOf(Color.BLACK));
            thoughtCalamitous.setStrokeWidth(2);
            Type = Type + "Calamitous";
            selectCount++;
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            thoughtCalamitous.setBackgroundColor(getColor(R.color.ButtonColorThird));
            thoughtCalamitous.setStrokeWidth(0);
            Type = Type.replace("Calamitous", "");
            selectCount--;
        }
    }

    public void Assertive(View view) {
        MaterialButton thoughtAssertive = findViewById(R.id.thoughtAssertive);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            thoughtAssertive.setBackgroundColor(getColor(R.color.teal_700));
            thoughtAssertive.setStrokeColor(ColorStateList.valueOf(Color.BLACK));
            thoughtAssertive.setStrokeWidth(2);
            Type = Type + "Assertive";
            selectCount++;
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            thoughtAssertive.setBackgroundColor(getColor(R.color.ButtonColorThird));
            thoughtAssertive.setStrokeWidth(0);
            Type = Type.replace("Assertive", "");
            selectCount--;
        }
    }

    public void Affirmative(View view) {
        MaterialButton thoughtAffirmative = findViewById(R.id.thoughtAffirmative);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            thoughtAffirmative.setBackgroundColor(getColor(R.color.teal_700));
            thoughtAffirmative.setStrokeColor(ColorStateList.valueOf(Color.BLACK));
            thoughtAffirmative.setStrokeWidth(2);
            Type = Type + "Affirmative";
            selectCount++;
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            thoughtAffirmative.setBackgroundColor(getColor(R.color.ButtonColorThird));
            thoughtAffirmative.setStrokeWidth(0);
            Type = Type.replace("Affirmative", "");
            selectCount--;
        }
    }

    public void Optimistic(View view) {
        MaterialButton thoughtOptimistic = findViewById(R.id.thoughtOptimistic);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            thoughtOptimistic.setBackgroundColor(getColor(R.color.teal_700));
            thoughtOptimistic.setStrokeColor(ColorStateList.valueOf(Color.BLACK));
            thoughtOptimistic.setStrokeWidth(2);
            Type = Type + "Optimistic";
            selectCount++;
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            thoughtOptimistic.setBackgroundColor(getColor(R.color.ButtonColorThird));
            thoughtOptimistic.setStrokeWidth(0);
            Type = Type.replace("Optimistic", "");
            selectCount--;
        }
    }

    public void Pessimistic(View view) {
        MaterialButton thoughtPessimistic = findViewById(R.id.thoughtPessimistic);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            thoughtPessimistic.setBackgroundColor(getColor(R.color.teal_700));
            thoughtPessimistic.setStrokeColor(ColorStateList.valueOf(Color.BLACK));
            thoughtPessimistic.setStrokeWidth(2);
            Type = Type + "Pessimistic";
            selectCount++;
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            thoughtPessimistic.setBackgroundColor(getColor(R.color.ButtonColorThird));
            thoughtPessimistic.setStrokeWidth(0);
            Type = Type.replace("Pessimistic", "");
            selectCount--;
        }
    }

    public void Inferring(View view) {
        MaterialButton thoughtInferring = findViewById(R.id.thoughtInferring);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            thoughtInferring.setBackgroundColor(getColor(R.color.teal_700));
            thoughtInferring.setStrokeColor(ColorStateList.valueOf(Color.BLACK));
            thoughtInferring.setStrokeWidth(2);
            Type = Type + "Inferring";
            selectCount++;
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            thoughtInferring.setBackgroundColor(getColor(R.color.ButtonColorThird));
            thoughtInferring.setStrokeWidth(0);
            Type = Type.replace("Inferring", "");
            selectCount--;
        }
    }

    public void Disgust(View view) {
        MaterialButton thoughtDisgust = findViewById(R.id.thoughtDisgust);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            thoughtDisgust.setBackgroundColor(getColor(R.color.teal_700));
            thoughtDisgust.setStrokeColor(ColorStateList.valueOf(Color.BLACK));
            thoughtDisgust.setStrokeWidth(2);
            Type = Type + "Disgust";
            selectCount++;
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            thoughtDisgust.setBackgroundColor(getColor(R.color.ButtonColorThird));
            thoughtDisgust.setStrokeWidth(0);
            Type = Type.replace("Disgust", "");
            selectCount--;
        }
    }

    public void Other(View view) {
        MaterialButton thoughtOther = findViewById(R.id.thoughtOther);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            thoughtOther.setBackgroundColor(getColor(R.color.teal_700));
            thoughtOther.setStrokeColor(ColorStateList.valueOf(Color.BLACK));
            thoughtOther.setStrokeWidth(2);
            Type = Type + "Other";
            selectCount++;
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            thoughtOther.setBackgroundColor(getColor(R.color.ButtonColorThird));
            thoughtOther.setStrokeWidth(0);
            Type = Type.replace("Other", "");
            selectCount--;
        }
    }

    public void NotSure(View view) {
        MaterialButton thoughtNotSure = findViewById(R.id.thoughtNotSure);
        if (view.getTag() == null || view.getTag().toString().equals("untapped")) {
            view.setTag("tapped");
            thoughtNotSure.setBackgroundColor(getColor(R.color.teal_700));
            thoughtNotSure.setStrokeColor(ColorStateList.valueOf(Color.BLACK));
            thoughtNotSure.setStrokeWidth(2);
            Type = Type + "Not Sure";
            selectCount++;
        } else if (view.getTag().toString().equals("tapped")) {
            view.setTag("untapped");
            thoughtNotSure.setBackgroundColor(getColor(R.color.ButtonColorThird));
            thoughtNotSure.setStrokeWidth(0);
            Type = Type.replace("Not Sure", "");
            selectCount--;
        }
    }

    private boolean isConnected(ThoughtCheckin CheckInternet) {
        ConnectivityManager connectivityManager = (ConnectivityManager) CheckInternet.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiCon != null && wifiCon.isConnected()) || (mobileCon != null && mobileCon.isConnected()))
            return true;
        else
            return false;
    }

    private void classify(final String text) {
        if (selectCount == 0) {
            Toast.makeText(this, "Please select at least one thought type", Toast.LENGTH_SHORT).show();
        } else if (selectCount == 2) {
            Toast.makeText(this, "Please select only one thought type", Toast.LENGTH_SHORT).show();
        } else if (Desc.equals("") || Desc.equals(null)) {
            Toast.makeText(this, "Please add thought description before submitting", Toast.LENGTH_SHORT).show();
        } else {
            messageBox.show();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            // Run text classification with TF Lite.
            List<Result> results = client.classify(text);
            // Show classification result on screen
            showResult(results);
        }
    }

    private void showResult(final List<Result> results) {
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
        perCent = Math.round(HighValue * 1000) / 10.0 + "%";
        classifiedAs = HighValueLable;
        insertCheckin();
    }

    public void insertCheckin() {
        dbHeading = getIntent().getStringExtra("checkInHeading");
        checkInModel thoughtCheckIn = new checkInModel(Date, Time, Type, Desc, classifiedAs, perCent);
        referenceToCheckin.child(Month).child(Date).child(dbHeading).setValue(thoughtCheckIn);
        handler = new Handler();
        handler.postDelayed(() -> {
            messageBox.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Intent moveTo = new Intent(getApplicationContext(), HomeContainer.class);
            startActivity(moveTo);
            finish();
            Toast.makeText(getApplicationContext(), "Check-in completed. You can check your stats in 'Progress Tab'", Toast.LENGTH_LONG).show();
        }, 750);
    }

    public void onBackPressed() {
        Snackbar snackbar = Snackbar.make(Snackbar_layout, "Are you sure to exit Thought Check-in?", Snackbar.LENGTH_LONG)
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