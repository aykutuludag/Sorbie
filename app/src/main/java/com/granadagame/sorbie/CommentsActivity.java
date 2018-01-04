package com.granadagame.sorbie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.granadagame.sorbie.adapter.CommentAdapter;
import com.granadagame.sorbie.model.CommentItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static com.granadagame.sorbie.MainActivity.photo;
import static com.granadagame.sorbie.MainActivity.username;

public class CommentsActivity extends AppCompatActivity {

    Toolbar toolbar;
    Window window;
    EditText commentHolder;
    ImageView sendComment;
    RecyclerView mRecyclerView;
    GridLayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    List<CommentItem> feedsList;

    String comment;
    int questionID;
    int commentCount;

    private String FETCH_COMMENT = "http://granadagame.com/Sorbie/fetch_comments.php";
    private String SEND_COMMENT = "http://granadagame.com/Sorbie/add_comment.php";
    private String UPDATE_COMMENT_COUNT = "http://granadagame.com/Sorbie/comment_count.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.drawable.sorbie);

        //Window
        window = this.getWindow();
        coloredBars(Color.parseColor("#626262"), Color.parseColor("#ffffff"));

        Intent intent = getIntent();
        questionID = intent.getIntExtra("question_ID", 0);
        commentCount = intent.getIntExtra("commentCount", 0);

        feedsList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.commentView);

        commentHolder = findViewById(R.id.editText);
        commentHolder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    comment = editable.toString();
                }
            }
        });

        sendComment = findViewById(R.id.imageView4);
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendComment();
            }
        });

        fetchComments();
    }

    public void fetchComments() {
        feedsList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray res = new JSONArray(response);
                            for (int i = 0; i < res.length(); i++) {
                                JSONObject obj = res.getJSONObject(i);

                                CommentItem item = new CommentItem();
                                item.setID(obj.getInt("id"));
                                item.setComment(obj.getString("comment"));
                                item.setTime(obj.getString("time"));
                                item.setProfile_pic(obj.getString("user_photo"));
                                item.setUsername(obj.getString("username"));
                                feedsList.add(item);

                                mAdapter = new CommentAdapter(CommentsActivity.this, feedsList);
                                mLayoutManager = new GridLayoutManager(CommentsActivity.this, 1);

                                mAdapter.notifyDataSetChanged();
                                mRecyclerView.setAdapter(mAdapter);
                                mRecyclerView.setLayoutManager(mLayoutManager);
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
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new Hashtable<>();

                //Adding parameters
                params.put("id", String.valueOf(questionID));

                //returning parameters
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(CommentsActivity.this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void sendComment() {
        final ProgressDialog loading = ProgressDialog.show(CommentsActivity.this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(CommentsActivity.this, response, Toast.LENGTH_SHORT).show();
                        updateCommentCount();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Showing toast
                        loading.dismiss();
                        Toast.makeText(CommentsActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new Hashtable<>();

                //Adding parameters
                params.put("comment", comment);
                params.put("question_id", String.valueOf(questionID));
                params.put("username", username);
                params.put("user_photo", photo);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(CommentsActivity.this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void updateCommentCount() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_COMMENT_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Toast.makeText(CommentsActivity.this, s, Toast.LENGTH_LONG).show();
                        fetchComments();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new Hashtable<>();

                //Adding parameters
                params.put("id", String.valueOf(questionID));
                params.put("comment_number", String.valueOf(commentCount++));

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(CommentsActivity.this);

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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
