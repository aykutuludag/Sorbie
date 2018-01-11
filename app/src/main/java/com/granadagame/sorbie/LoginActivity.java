package com.granadagame.sorbie;

import android.app.ProgressDialog;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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

import org.json.JSONObject;

import java.text.Normalizer;
import java.util.Hashtable;
import java.util.Map;

import static com.granadagame.sorbie.MainActivity.birthday;
import static com.granadagame.sorbie.MainActivity.email;
import static com.granadagame.sorbie.MainActivity.gender;
import static com.granadagame.sorbie.MainActivity.location;
import static com.granadagame.sorbie.MainActivity.name;
import static com.granadagame.sorbie.MainActivity.photo;
import static com.granadagame.sorbie.MainActivity.username;


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
    String REGISTER_URL = "http://granadagame.com/Sorbie/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Analytics
        t = ((AnalyticsApplication) this.getApplicationContext()).getDefaultTracker();
        t.setScreenName("Giriş yap/Splash");
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
                GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                if (response.getError() != null) {
                                    Toast.makeText(LoginActivity.this, getString(R.string.error_login_fail), Toast.LENGTH_SHORT).show();
                                } else {
                                    name = me.optString("first_name");
                                    name += " " + me.optString("last_name");
                                    email = me.optString("email");
                                    photo = me.optString("profile_pic");
                                    gender = me.optString("gender");
                                    location = me.optString("location");

                                    //USERNAME
                                    String tmpusername = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("[^a-zA-Z]", "").replace(" ", "").toLowerCase();
                                    if (tmpusername.length() > 16) {
                                        username = tmpusername.substring(0, 15);
                                    } else {
                                        username = tmpusername;
                                    }
                                    prefs.edit().putString("UserName", username).apply();

                                    saveUserInfo();
                                }
                            }
                        }).executeAsync();
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
            }, 2000);
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

    private void saveUserInfo() {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(LoginActivity.this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        loading.dismiss();
                        Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
                        signInButton.setVisibility(View.INVISIBLE);
                        prefs.edit().putBoolean("isSigned", true).apply();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }, 2000);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(LoginActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                        prefs.edit().putBoolean("isSigned", false).apply();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new Hashtable<>();

                //Adding parameters
                params.put("username", username);
                params.put("name", name);
                params.put("email", email);
                params.put("photo", photo);
                params.put("gender", gender);
                params.put("birthday", birthday);
                params.put("location", location);
                params.put("accType", "Android");

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == googleSign) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                if (acct != null) {

                    //GOOGLE ID
                    String googleID = acct.getId();
                    prefs.edit().putString("GoogleAccountID", googleID).apply();

                    //NAME
                    name = acct.getDisplayName();
                    prefs.edit().putString("Name", name).apply();

                    //USERNAME
                    String tmpusername = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("[^a-zA-Z]", "").replace(" ", "").toLowerCase();
                    if (tmpusername.length() > 16) {
                        username = tmpusername.substring(0, 15);
                    } else {
                        username = tmpusername;
                    }
                    prefs.edit().putString("UserName", username).apply();

                    //EMAİL
                    email = acct.getEmail();
                    prefs.edit().putString("Email", email).apply();

                    //PHOTO
                    if (acct.getPhotoUrl() != null) {
                        photo = acct.getPhotoUrl().toString();
                        prefs.edit().putString("ProfilePhoto", photo).apply();
                    } else {
                        photo = "http://granadagame.com/Sorbie/profile.png";
                        prefs.edit().putString("ProfilePhoto", photo).apply();
                    }

                    // G+
                    Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                    if (person != null) {
                        //GENDER
                        if (person.getGender() == 0) {
                            gender = "Male";
                            prefs.edit().putString("Gender", gender).apply();
                        } else if (person.getGender() == 1) {
                            gender = "Female";
                            prefs.edit().putString("Gender", gender).apply();
                        } else {
                            gender = "Other";
                            prefs.edit().putString("Gender", gender).apply();
                        }

                        //BIRTHDAY
                        if (person.getBirthday() != null) {
                            birthday = person.getBirthday();
                            prefs.edit().putString("Birthday", birthday).apply();
                        }

                        //LOCATION
                        if (person.getCurrentLocation() != null) {
                            location = person.getCurrentLocation();
                            prefs.edit().putString("Location", location).apply();
                        }
                    } else {
                        //Default values
                        gender = "Male";
                        birthday = "01-01-2000";
                        location = "World";
                        prefs.edit().putString("Gender", gender).apply();
                        prefs.edit().putString("Birthday", null).apply();
                        prefs.edit().putString("Location", null).apply();
                    }
                    saveUserInfo();
                } else {
                    Toast.makeText(this, getString(R.string.error_login_no_account), Toast.LENGTH_SHORT).show();
                    prefs.edit().putBoolean("isSigned", false).apply();
                }
            } else {
                Toast.makeText(this, getString(R.string.error_login_fail), Toast.LENGTH_SHORT).show();
                prefs.edit().putBoolean("isSigned", false).apply();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, getString(R.string.error_login_fail), Toast.LENGTH_SHORT).show();
    }
}

