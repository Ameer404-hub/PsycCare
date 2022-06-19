package com.example.psyccare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.psyccare.Authentications.FacebookAuth;
import com.example.psyccare.Authentications.GoogleAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class Login extends AppCompatActivity {

    TextInputLayout emailInput, passInput;
    String Email, Pass;
    CircularProgressButton loginBtn;
    TextView gotoSignUp;
    Handler handler;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    LinearLayout Snackbar_layout;
    SharedPreferences psycProbActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        emailInput = findViewById(R.id.eMail);
        passInput = findViewById(R.id.passWord);
        loginBtn = findViewById(R.id.login_btn);
        gotoSignUp = findViewById(R.id.signUp_str);
        Snackbar_layout = findViewById(R.id.login_layout);

        loginBtn.setOnClickListener(v -> {
            Email = emailInput.getEditText().getText().toString().trim();
            Pass = passInput.getEditText().getText().toString().trim();
            if (!validateEmail() || !validatePass())
                return;
            else if (!isConnected(Login.this)) {
                Snackbar snackbar = Snackbar.make(Snackbar_layout, "No internet connection!", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                loginBtn.startAnimation();
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        performLogin();
                    }
                }, 750);
            }
        });

        gotoSignUp.setOnClickListener(v -> {
            Intent Move = new Intent(getApplicationContext(), Signup.class);
            startActivity(Move);
            finish();
        });

    }

    private boolean validateEmail() {
        if (Email.isEmpty()) {
            emailInput.setError("Email required!");
            emailInput.setErrorEnabled(true);
            return false;
        } else if (!Email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            emailInput.setError("Invalid Email Format!");
            emailInput.setErrorEnabled(true);
            return false;
        } else {
            emailInput.setError(null);
            emailInput.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePass() {
        if (Pass.isEmpty()) {
            passInput.setError("Password required!");
            passInput.setErrorEnabled(true);
            return false;
        } else if (Pass.length() < 6) {
            passInput.setError("Password must be greater than six characters!");
            passInput.setErrorEnabled(true);
            return false;
        } else {
            passInput.setError(null);
            passInput.setErrorEnabled(false);
            return true;
        }
    }

    private void performLogin() {
        mAuth.signInWithEmailAndPassword(Email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loginBtn.revertAnimation();
                    mUser = mAuth.getCurrentUser();

                    psycProbActivity = getSharedPreferences("psycProbScreen", MODE_PRIVATE);
                    boolean isFirstTime = psycProbActivity.getBoolean("firstTime", true);

                    if (isFirstTime) {
                        SharedPreferences.Editor editor = psycProbActivity.edit();
                        editor.putBoolean("firstTime", false);
                        editor.commit();

                        Intent intent = new Intent(getApplicationContext(), OngoingPsycProb.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), HomeContainer.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    loginBtn.revertAnimation();
                    Toast.makeText(Login.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void signInWithGoogle(View view) {
        Intent intent = new Intent(getApplicationContext(), GoogleAuth.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void signInWithFB(View view) {
        Intent intent = new Intent(getApplicationContext(), FacebookAuth.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private boolean isConnected(Login CheckInternet) {
        ConnectivityManager connectivityManager = (ConnectivityManager) CheckInternet.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiCon != null && wifiCon.isConnected()) || (mobileCon != null && mobileCon.isConnected()))
            return true;
        else
            return false;
    }
}