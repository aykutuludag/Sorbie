package com.granadagame.sorbie;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static com.granadagame.sorbie.MainActivity.birthday;
import static com.granadagame.sorbie.MainActivity.email;
import static com.granadagame.sorbie.MainActivity.gender;
import static com.granadagame.sorbie.MainActivity.location;
import static com.granadagame.sorbie.MainActivity.username;

public class FragmentProfile extends Fragment {

    String FETCH_URL = "http://granadagame.com/Sorbie/user-fetch.php";

    SwipeRefreshLayout swipeContainer;
    RecyclerView mRecyclerView;
    GridLayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    List<FeedItem> feedsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Analytics
        Tracker t = ((AnalyticsApplication) getActivity().getApplicationContext()).getDefaultTracker();
        t.setScreenName("Profil");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        //Name
        TextView navUsername = v.findViewById(R.id.profile_name);
        navUsername.setText(username);
        //E-mail
        TextView navEmail = v.findViewById(R.id.profile_mail);
        navEmail.setText(email);
        //ProfilePicture
        ImageView profilePic = v.findViewById(R.id.profile_pic);
        Picasso.with(getActivity()).load(MainActivity.photo).error(R.drawable.profile).placeholder(R.drawable.profile)
                .into(profilePic);
        //Age
        TextView birthtext = v.findViewById(R.id.profile_birthday);
        birthtext.setText(birthday);

        //Location
        TextView loc = v.findViewById(R.id.profile_loc);
        loc.setText(location);

        //Gender
        TextView sex = v.findViewById(R.id.profile_gender);
        sex.setText(gender);

        swipeContainer = v.findViewById(R.id.swipeContainer);
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
        mRecyclerView = v.findViewById(R.id.feedView);

        fetchUserQuestions();

        return v;
    }

    private void fetchUserQuestions() {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Loading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        feedsList.clear();
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://granadagame.com/Sorbie/fetch.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONArray res = new JSONArray(response);
                                            for (int i = 0; i < res.length(); i++) {
                                                JSONObject obj = res.getJSONObject(i);

                                                FeedItem item = new FeedItem();
                                                item.setID(Integer.parseInt(obj.getString("id")));
                                                item.setImageURI(obj.getString("photo").replace("\\/", "/"));
                                                item.setQuestion(obj.getString("question"));
                                                feedsList.add(item);

                                                System.out.println(feedsList);

                                                mAdapter = new FeedAdapter(getActivity(), feedsList);
                                                mLayoutManager = new GridLayoutManager(getActivity(), 1);

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
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }) {
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

                        //Adding request to the queue
                        requestQueue.add(stringRequest);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        //Showing toast
                        Toast.makeText(getActivity(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new Hashtable<>();

                //Adding parameters
                params.put("username", username);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

}
