package com.example.psyccare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.example.psyccare.Authentications.FacebookAuthActivity;
import com.example.psyccare.Authentications.GoogleAuthActivity;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class SignupActivity extends AppCompatActivity {

    CircularProgressButton signupBtn;
    TextView gotoLogin;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupBtn = findViewById(R.id.signup_btn);
        gotoLogin = findViewById(R.id.login_here_str);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupBtn.startAnimation();
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        signupBtn.revertAnimation();
                    }
                } ,3000);
            }
        });

        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Move = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(Move);
                finish();
            }
        });
    }

    public void signInWithGoogle(View view) {
        Intent intent = new Intent(getApplicationContext(), GoogleAuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void signInWithFB(View view) {
        Intent intent = new Intent(getApplicationContext(), FacebookAuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }


}