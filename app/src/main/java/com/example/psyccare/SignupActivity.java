package com.example.psyccare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class SignupActivity extends AppCompatActivity {

    CircularProgressButton signupBtn;
    TextView gotoLogin, googleTextSignup;
    ImageView googleImgSignup;
    Handler handler;
    LinearLayout opts_icon_layout;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    CallbackManager callbackManager;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        createRequest();

        signupBtn = findViewById(R.id.signup_btn);
        gotoLogin = findViewById(R.id.login_here_str);
        googleTextSignup = findViewById(R.id.googleLogoName);
        googleImgSignup = findViewById(R.id.logoGoogle);
        opts_icon_layout = findViewById(R.id.opts_icon_layout);

        googleTextSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        googleImgSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

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

    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar snackbar = Snackbar.make(opts_icon_layout, "Authentication failed!", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });
    }

    public void signInWithFB(View view) {

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar snackbar = Snackbar.make(opts_icon_layout, "Authentication failed!", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });
    }
}