package com.granadagame.sorbie.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.granadagame.sorbie.CommentsActivity;
import com.granadagame.sorbie.R;
import com.granadagame.sorbie.model.FeedItem;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private int questionID;
    private int commentCount;
    private FeedItem feedItem;
    private List<FeedItem> feedItemList;
    private Context mContext;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            int position = holder.getAdapterPosition();
            questionID = feedItemList.get(position).getID();
            Intent intent = new Intent(mContext, CommentsActivity.class);
            intent.putExtra("question_ID", questionID);
            intent.putExtra("commentCount", commentCount);
            mContext.startActivity(intent);
        }
    };

    public FeedAdapter(Context context, List<FeedItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_feed, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        feedItem = feedItemList.get(i);

        questionID = feedItem.getID();

        viewHolder.username.setText(feedItem.getUsername());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = format.parse(feedItem.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.time.setReferenceTime(date.getTime());

        Picasso.with(mContext).load(feedItem.getImageURI()).error(R.drawable.empty).placeholder(R.drawable.empty)
                .into(viewHolder.questionImage);

        viewHolder.question.setText(feedItem.getQuestion());

        Picasso.with(mContext).load(feedItem.getProfile_pic()).error(R.drawable.empty).placeholder(R.drawable.empty)
                .into(viewHolder.profilePic);

        if (feedItem.getIsAnswered() == 1) {
            viewHolder.verified.setImageResource(R.drawable.answered);
            viewHolder.card.setBackgroundColor(Color.parseColor("#9EC465"));
        } else {
            viewHolder.verified.setImageResource(R.drawable.waitinganswer);
            viewHolder.card.setBackgroundColor(Color.parseColor("#EFEFEF"));
        }

        commentCount = feedItem.getComment_number();
        viewHolder.comCount.setText(feedItem.getComment_number() + " yorum");

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
        TextView question;
        ImageView questionImage;
        TextView username;
        RelativeTimeTextView time;
        ImageView profilePic;
        ImageView verified;
        TextView comCount;

        ViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.single_feed);
            questionImage = itemView.findViewById(R.id.imageView2);
            question = itemView.findViewById(R.id.textView4);
            username = itemView.findViewById(R.id.textView);
            time = itemView.findViewById(R.id.textView2);
            profilePic = itemView.findViewById(R.id.imageView);
            verified = itemView.findViewById(R.id.imageView3);
            comCount = itemView.findViewById(R.id.textView3);
        }
    }
}