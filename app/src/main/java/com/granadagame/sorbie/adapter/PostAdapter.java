package com.granadagame.sorbie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.granadagame.sorbie.R;
import com.granadagame.sorbie.model.FeedItem;
import com.granadagame.sorbie.model.PostItem;

import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<PostItem> feedItemList;
    private Context mContext;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*ViewHolder holder = (ViewHolder) view.getTag();
            int position = holder.getAdapterPosition();
            int eventID = feedItemList.get(position).getID();

            Intent intent = new Intent(mContext, SingleEvent.class);
            intent.putExtra("EVENT_ID", eventID);
            mContext.startActivity(intent);*/
        }
    };

    public PostAdapter(Context context, List<PostItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_post, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        PostItem feedItem = feedItemList.get(i);

        int ID = feedItem.getID();

        viewHolder.question.setText(feedItem.getQuestion());

        // Handle click event on image click
        viewHolder.question.setOnClickListener(clickListener);
        viewHolder.question.setTag(viewHolder);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        Button question;

        ViewHolder(View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.button2);
        }
    }
}
