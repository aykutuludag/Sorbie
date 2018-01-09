package com.granadagame.sorbie.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.granadagame.sorbie.model.FeedItem;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private int questionID;
    private int commentCount;
    private String userName;
    private String questionImage;
    private String question;
    private List<FeedItem> feedItemList;
    private Context mContext;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            int position = holder.getAdapterPosition();
            questionID = feedItemList.get(position).getID();
            questionImage = feedItemList.get(position).getImageURI();
            question = feedItemList.get(position).getQuestion();

            //Creating the instance of PopupMenu
            PopupMenu popup;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                popup = new PopupMenu(mContext, view, Gravity.RIGHT);
            } else {
                popup = new PopupMenu(mContext, view);
            }
            popup.getMenuInflater().inflate(R.menu.post, popup.getMenu());

            String localUser = MainActivity.username;
            if (localUser.equals(userName)) {
                popup.getMenu().findItem(R.id.post_delete).setVisible(true);
            } else {
                popup.getMenu().findItem(R.id.post_delete).setVisible(false);
            }

            popup.show();

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.post_share:
                            sharePost(question, questionImage);
                            break;
                        case R.id.post_delete:
                            deletePost(questionID);
                            Toast.makeText(mContext, "Soru silindi...", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return true;
                }
            });
        }
    };

    private View.OnClickListener clickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            int position = holder.getAdapterPosition();
            questionID = feedItemList.get(position).getID();
            userName = feedItemList.get(position).getUsername();
            commentCount = feedItemList.get(position).getComment_number();

            Intent intent = new Intent(mContext, CommentsActivity.class);
            intent.putExtra("question_ID", questionID);
            intent.putExtra("commentCount", commentCount);
            intent.putExtra("questionOwner", userName);
            mContext.startActivity(intent);
        }
    };

    public FeedAdapter(Context context, List<FeedItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    private void sharePost(String text, String uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text + " " + uri);
        intent.setType("text/plain");
        mContext.startActivity(Intent.createChooser(intent, "Paylaş..."));
    }

    private void deletePost(final int ID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://granadagame.com/Sorbie/delete_question.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //UPDATE FEED
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
                params.put("id", String.valueOf(ID));

                //returning parameters
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_feed, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        FeedItem feedItem = feedItemList.get(i);

        questionID = feedItem.getID();

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

        questionImage = feedItem.getImageURI();
        Picasso.with(mContext).load(questionImage).error(R.drawable.empty).placeholder(R.drawable.empty)
                .into(viewHolder.questionImage);

        question = feedItem.getQuestion();
        viewHolder.question.setText(question);

        Picasso.with(mContext).load(feedItem.getProfile_pic()).error(R.drawable.empty).placeholder(R.drawable.empty)
                .into(viewHolder.profilePic);

        if (feedItem.getIsAnswered() == 1) {
            viewHolder.verified.setImageResource(R.drawable.answered);
        } else {
            viewHolder.verified.setImageResource(R.drawable.waitinganswer);
        }

        commentCount = feedItem.getComment_number();
        viewHolder.comCount.setText(feedItem.getComment_number() + " cevap");

        // Handle click event on image click
        viewHolder.header.setOnClickListener(clickListener);
        viewHolder.header.setTag(viewHolder);

        viewHolder.bottom.setOnClickListener(clickListener2);
        viewHolder.bottom.setTag(viewHolder);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout header, bottom;
        TextView question;
        ImageView questionImage;
        TextView username;
        RelativeTimeTextView time;
        ImageView profilePic;
        ImageView verified;
        TextView comCount;

        ViewHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.card_question_header);
            bottom = itemView.findViewById(R.id.card_question_bottom);
            questionImage = itemView.findViewById(R.id.question_image);
            question = itemView.findViewById(R.id.question);
            username = itemView.findViewById(R.id.username);
            time = itemView.findViewById(R.id.time);
            profilePic = itemView.findViewById(R.id.profile_pic);
            verified = itemView.findViewById(R.id.question_answered);
            comCount = itemView.findViewById(R.id.question_comment);
        }
    }
}