package com.example.psyccare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class SignupActivity extends AppCompatActivity {

    CircularProgressButton signupBtn;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupBtn = findViewById(R.id.signup_btn);
    }

    public void validateUser(View view) {
        signupBtn.startAnimation();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                signupBtn.revertAnimation();
            }
        } ,3000);
    }

    public void gotoLogin(View view) {
        Intent Move = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(Move);
        finish();
    }
}