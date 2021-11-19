package com.example.psyccare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {

    CircularProgressButton loginBtn;
    TextView gotoSignUp;
    Handler handler;
    LinearLayout opts_icon_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.login_btn);
        gotoSignUp = findViewById(R.id.signUp_str);

        opts_icon_layout = findViewById(R.id.opts_icon_layout);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.startAnimation();
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loginBtn.revertAnimation();
                    }
                } ,3000);
            }
        });

        gotoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Move = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(Move);
                finish();
            }
        });

    }

    public void signInWithGmail(View view) {
        Snackbar snackbar = Snackbar.make(opts_icon_layout, "Sign in with Google?", Snackbar.LENGTH_LONG)
                .setAction("YES", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbarAction = Snackbar.make(opts_icon_layout, "Sign in Option Working :)", Snackbar.LENGTH_SHORT);
                        snackbarAction.show();
                    }
                });
        snackbar.show();
    }

    public void signInWithFB(View view) {
        Snackbar snackbar = Snackbar.make(opts_icon_layout, "Sign in with Facebook?", Snackbar.LENGTH_LONG)
                .setAction("YES", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbarAction = Snackbar.make(opts_icon_layout, "Sign in Option Working :)", Snackbar.LENGTH_SHORT);
                        snackbarAction.show();
                    }
                });
        snackbar.show();
    }
}