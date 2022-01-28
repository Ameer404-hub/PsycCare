package com.example.psyccare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.psyccare.Models.UserSignupData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class SignupActivity extends AppCompatActivity {

    TextInputLayout nameInput, emailInput, passInput, conPassInput;
    String Name, Email, Pass, ConPass;
    CircularProgressButton signupBtn;
    CheckBox TnCbox;
    TextView gotoLogin;
    Handler handler;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    LinearLayout Snackbar_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameInput = findViewById(R.id.fullName);
        emailInput = findViewById(R.id.eMail);
        passInput = findViewById(R.id.passWord);
        conPassInput = findViewById(R.id.confirmPass);
        signupBtn = findViewById(R.id.signup_btn);
        gotoLogin = findViewById(R.id.login_here_str);
        TnCbox = findViewById(R.id.TCBox);
        Snackbar_layout = findViewById(R.id.login_layout);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = nameInput.getEditText().getText().toString().trim();
                Email = emailInput.getEditText().getText().toString().trim();
                Pass = passInput.getEditText().getText().toString().trim();
                ConPass = conPassInput.getEditText().getText().toString().trim();
                if (!validateName() || !validateEmail() || !validatePass() || !validateConPass() || !validatetermsCons())
                    return;
                else if (!isConnected(SignupActivity.this)) {
                    Snackbar snackbar = Snackbar.make(Snackbar_layout, "No internet connection!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    signupBtn.startAnimation();
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            performSignup();
                        }
                    }, 750);
                }
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

    private boolean validateName() {
        if (Name.isEmpty()) {
            nameInput.setError("Name required!");
            nameInput.setErrorEnabled(true);
            return false;
        } else if (Name.matches(".*\\d.*") || Name.matches("[`~!@#$%^&*()_+=-]+")) {
            nameInput.setError("Numerics and special characters not allowed!");
            nameInput.setErrorEnabled(true);
            return false;
        } else {
            nameInput.setError(null);
            nameInput.setErrorEnabled(false);
            return true;
        }
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

    private boolean validateConPass() {
        if (ConPass.isEmpty()) {
            conPassInput.setError("Confirm Password required!");
            conPassInput.setErrorEnabled(true);
            return false;
        } else if (!ConPass.equals(Pass)) {
            conPassInput.setError("Passwords does not match!");
            conPassInput.setErrorEnabled(true);
            return false;
        } else {
            conPassInput.setError(null);
            conPassInput.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatetermsCons() {
        if(!TnCbox.isChecked()){
            Toast.makeText(SignupActivity.this, "Please check Terms and Conditions", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }

    private void performSignup() {
        mAuth.createUserWithEmailAndPassword(Email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    signupBtn.revertAnimation();
                    mUser = mAuth.getCurrentUser();
                    recordUserData();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    signupBtn.revertAnimation();
                    Toast.makeText(SignupActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void recordUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        UserSignupData NewUser = new UserSignupData(Name, Email, Pass);
        reference.child(FirebaseAuth.getInstance().getUid()).child("ProfileInfo").setValue(NewUser);
    }

    private boolean isConnected(SignupActivity CheckInternet) {
        ConnectivityManager connectivityManager = (ConnectivityManager) CheckInternet.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiCon != null && wifiCon.isConnected()) || (mobileCon != null && mobileCon.isConnected()))
            return true;
        else
            return false;
    }
}