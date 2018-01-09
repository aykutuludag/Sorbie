package com.granadagame.sorbie.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.granadagame.sorbie.CommentsActivity;
import com.granadagame.sorbie.MainActivity;
import com.granadagame.sorbie.R;
import com.granadagame.sorbie.model.CommentItem;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static com.granadagame.sorbie.CommentsActivity.commentCount;
import static com.granadagame.sorbie.CommentsActivity.questionID;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private int commentID;
    private CommentItem feedItem;
    private List<CommentItem> feedItemList;
    private Context mContext;
    private String userName;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CommentAdapter.ViewHolder holder = (CommentAdapter.ViewHolder) view.getTag();
            int position = holder.getAdapterPosition();
            commentID = feedItemList.get(position).getID();
            userName = feedItemList.get(position).getUsername();

            //Creating the instance of PopupMenu
            PopupMenu popup;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                popup = new PopupMenu(mContext, view, Gravity.RIGHT);
            } else {
                popup = new PopupMenu(mContext, view);
            }
            popup.getMenuInflater().inflate(R.menu.comment, popup.getMenu());

            String questionOwner = CommentsActivity.questionOwner;
            String localUser = MainActivity.username;

            if (localUser.equals(questionOwner)) {
                popup.getMenu().findItem(R.id.comment_accept).setVisible(true);
            } else {
                popup.getMenu().findItem(R.id.comment_accept).setVisible(false);
            }

            if (localUser.equals(userName)) {
                popup.getMenu().findItem(R.id.comment_delete).setVisible(true);
            } else {
                popup.getMenu().findItem(R.id.comment_delete).setVisible(false);
            }

            popup.show();

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.comment_accept:
                            setAsTrue(questionID, commentID);
                            break;
                        case R.id.comment_delete:
                            deleteComment(commentID);
                            break;
                    }
                    return true;
                }
            });
        }
    };

    public CommentAdapter(Context context, List<CommentItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    void setAsTrue(final int id, final int id2) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://granadagame.com/Sorbie/set_as_true.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(mContext, response, Toast.LENGTH_LONG).show();
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
                params.put("id", String.valueOf(id));
                params.put("id2", String.valueOf(id2));

                //returning parameters
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    void deleteComment(final int id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://granadagame.com/Sorbie/delete_comment.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(mContext, response, Toast.LENGTH_LONG).show();
                        updateCommentCount();
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
                params.put("id", String.valueOf(id));

                //returning parameters
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void updateCommentCount() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://granadagame.com/Sorbie/comment_count.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        ((CommentsActivity) mContext).fetchComments();
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

                commentCount--;

                //Adding parameters
                params.put("id", String.valueOf(questionID));
                params.put("comment_number", String.valueOf(commentCount));

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_comment, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        feedItem = feedItemList.get(i);
        commentID = feedItem.getID();
        userName = feedItem.getUsername();

        viewHolder.username.setText(userName);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = format.parse(feedItem.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.time.setReferenceTime(date.getTime());

        viewHolder.commentHolder.setText(feedItem.getComment());

        Picasso.with(mContext).load(feedItem.getProfile_pic()).error(R.drawable.empty).placeholder(R.drawable.empty)
                .into(viewHolder.profilePic);

        if (feedItem.getIsTrue() == 1) {
            viewHolder.card.setBackgroundColor(Color.parseColor("#9fc465"));
        }

        // Handle click event on image click
        viewHolder.card.setOnClickListener(clickListener);
        viewHolder.card.setTag(viewHolder);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout card;
        TextView commentHolder;
        TextView username;
        RelativeTimeTextView time;
        ImageView profilePic;

        ViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.single_comment);
            commentHolder = itemView.findViewById(R.id.question);
            username = itemView.findViewById(R.id.username);
            time = itemView.findViewById(R.id.time);
            profilePic = itemView.findViewById(R.id.profile_pic);
        }
    }
}