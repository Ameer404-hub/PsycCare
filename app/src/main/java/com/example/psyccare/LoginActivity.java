package com.example.psyccare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {

    CircularProgressButton loginBtn;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.login_btn);
    }

    public void validateUser(View view) {
        loginBtn.startAnimation();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loginBtn.revertAnimation();
            }
        } ,3000);
    }
}