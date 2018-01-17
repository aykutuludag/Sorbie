package com.granadagame.sorbie;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.granadagame.sorbie.adapter.FeedAdapter;
import com.granadagame.sorbie.model.FeedItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    String FETCH_USER_INFO = "http://granadagame.com/Sorbie/fetch_user_info.php";
    String FETCH_USER_QUESTIONS = "http://granadagame.com/Sorbie/fetch_user_questions.php";

    Toolbar toolbar;
    Window window;

    SwipeRefreshLayout swipeContainer;
    RecyclerView mRecyclerView;
    GridLayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    List<FeedItem> feedsList;

    ImageView profilePic;
    TextView displayName, navEmail, birthtext, loc, sex, work;
    String whichUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.drawable.sorbie);

        //Window
        window = this.getWindow();
        coloredBars(Color.parseColor("#626262"), Color.parseColor("#ffffff"));

        // Analytics
        Tracker t = ((AnalyticsApplication) this.getApplicationContext()).getDefaultTracker();
        t.setScreenName("Profil: " + whichUser);
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        Intent intent = getIntent();
        whichUser = intent.getStringExtra("whichProfile");

        //Name
        displayName = findViewById(R.id.other_profile_name);

        //E-mail
        navEmail = findViewById(R.id.other_profile_mail);

        //ProfilePicture
        profilePic = findViewById(R.id.other_profile_pic);

        //Age
        birthtext = findViewById(R.id.other_profile_birthday);

        //Location
        loc = findViewById(R.id.other_profile_loc);

        //Gender
        sex = findViewById(R.id.other_profile_gender);

        //Job
        work = findViewById(R.id.other_profile_job);

        swipeContainer = findViewById(R.id.swipeContainerOther);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchUserQuestions();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        feedsList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.other_feedView);

        fetchUserInfo();
        fetchUserQuestions();
    }

    private void fetchUserInfo() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_USER_INFO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray res = new JSONArray(response);
                            for (int i = 0; i < res.length(); i++) {
                                JSONObject obj = res.getJSONObject(i);
                                displayName.setText(obj.getString("name"));
                                navEmail.setText(obj.getString("email"));
                                Picasso.with(ProfileActivity.this).load(obj.getString("photo").replace("\\/", "/")).error(R.drawable.profile).placeholder(R.drawable.profile)
                                        .into(profilePic);
                                birthtext.setText(obj.getString("birthday"));
                                loc.setText(obj.getString("location"));
                                sex.setText(obj.getString("gender"));
                                work.setText(obj.getString("job"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Showing toast
                        Toast.makeText(ProfileActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new Hashtable<>();

                //Adding parameters
                params.put("username", whichUser);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void fetchUserQuestions() {
        feedsList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_USER_QUESTIONS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray res = new JSONArray(response);
                            for (int i = 0; i < res.length(); i++) {
                                JSONObject obj = res.getJSONObject(i);

                                FeedItem item = new FeedItem();
                                item.setID(obj.getInt("id"));
                                item.setUsername(obj.getString("username"));
                                item.setImageURI(obj.getString("photo").replace("\\/", "/"));
                                item.setQuestion(obj.getString("question"));
                                item.setTime(obj.getString("time"));
                                item.setIsAnswered(obj.getInt("isAnswered"));
                                item.setComment_number(obj.getInt("comment_number"));
                                item.setProfile_pic(obj.getString("user_photo"));
                                feedsList.add(item);

                                mAdapter = new FeedAdapter(ProfileActivity.this, feedsList);
                                mLayoutManager = new GridLayoutManager(ProfileActivity.this, 1);

                                mAdapter.notifyDataSetChanged();
                                mRecyclerView.setAdapter(mAdapter);
                                mRecyclerView.setLayoutManager(mLayoutManager);
                                swipeContainer.setRefreshing(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Showing toast
                        Toast.makeText(ProfileActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new Hashtable<>();

                //Adding parameters
                params.put("username", whichUser);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public void coloredBars(int color1, int color2) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color1);
            toolbar.setBackgroundColor(color2);
        } else {
            toolbar.setBackgroundColor(color2);
        }
    }

}
