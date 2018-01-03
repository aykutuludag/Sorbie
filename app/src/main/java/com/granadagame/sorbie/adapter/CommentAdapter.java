package com.granadagame.sorbie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.granadagame.sorbie.R;
import com.granadagame.sorbie.model.CommentItem;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private int commentID;
    private CommentItem feedItem;
    private List<CommentItem> feedItemList;
    private Context mContext;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //bU NOKTADA KULLANICI KENDİ YORUMUNA TIKLARSA SİLME
            //SORUYU SORAN İSE DOĞRU OLARAK İŞARETLEME YAPABİLECEK
        }
    };

    public CommentAdapter(Context context, List<CommentItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
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

        viewHolder.username.setText(feedItem.getUsername());

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
            commentHolder = itemView.findViewById(R.id.textView4);
            username = itemView.findViewById(R.id.textView);
            time = itemView.findViewById(R.id.textView2);
            profilePic = itemView.findViewById(R.id.imageView);
        }
    }
}