package com.granadagame.sorbie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    Tracker t;
    GoogleApiClient mGoogleApiClient;
    SignInButton signInButton;
    SharedPreferences prefs;
    boolean isSigned;
    CallbackManager callbackManager;
    LoginButton loginButton;
    int googleSign = 9001;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Analytics
        Tracker t = ((AnalyticsApplication) this.getApplicationContext()).getDefaultTracker();
        t.setScreenName("Giriş yap");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        prefs = this.getSharedPreferences("ProfileInformation", Context.MODE_PRIVATE);
        isSigned = prefs.getBoolean("isSigned", false);

        //ProgressBar
        pb = findViewById(R.id.progressBar);

          /* Google Sign-In */
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Plus.SCOPE_PLUS_LOGIN)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();

        /* Facebook Login */
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //BURADA KULLANICI BİLGİLERİ ÇEKİLECEK


                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, getString(R.string.error_login_cancel), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, getString(R.string.error_login_fail), Toast.LENGTH_SHORT).show();
            }
        });

        if (isSigned) {
            signInButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();

                }
            }, 3000);
        } else {
            signInButton.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
        }
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, googleSign);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == googleSign) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                if (acct != null) {
                    System.out.println(acct.getGrantedScopes());
                    prefs.edit().putString("GoogleAccountID", acct.getId()).apply();
                    prefs.edit().putString("Name", acct.getDisplayName()).apply();
                    prefs.edit().putString("Email", acct.getEmail()).apply();
                    if (acct.getPhotoUrl() != null) {
                        prefs.edit().putString("ProfilePhoto", acct.getPhotoUrl().toString()).apply();
                    } else {
                        prefs.edit().putString("ProfilePhoto", "android.resource://com.granadagame.sorbie/R.drawable.profile").apply();
                    }
                    // G+
                    Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                    if (person != null) {
                        if (person.getGender() == 0) {
                            prefs.edit().putString("Gender", "Male").apply();
                        } else if (person.getGender() == 1) {
                            prefs.edit().putString("Gender", "Female").apply();
                        } else {
                            prefs.edit().putString("Gender", "Other").apply();
                        }
                        if (person.getBirthday() != null) {
                            prefs.edit().putString("Birthday", person.getBirthday()).apply();
                        }
                        if (person.getCurrentLocation() != null) {
                            prefs.edit().putString("Location", person.getCurrentLocation()).apply();
                        }
                    } else {
                        prefs.edit().putString("Gender", "Male").apply();
                        prefs.edit().putString("Birthday", null).apply();
                        prefs.edit().putString("Location", null).apply();
                    }

                    prefs.edit().putBoolean("isSigned", result.isSuccess()).apply();
                    Toast.makeText(this, getString(R.string.account_created), Toast.LENGTH_SHORT).show();
                    signInButton.setVisibility(View.INVISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }, 2000);
                } else {
                    Toast.makeText(this, getString(R.string.error_login_no_account), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.error_login_fail), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, getString(R.string.error_login_fail), Toast.LENGTH_SHORT).show();
    }
}

