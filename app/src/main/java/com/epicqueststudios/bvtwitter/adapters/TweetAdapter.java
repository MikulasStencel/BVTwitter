package com.epicqueststudios.bvtwitter.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.epicqueststudios.bvtwitter.R;
import com.epicqueststudios.bvtwitter.model.BVMessage;
import com.epicqueststudios.bvtwitter.model.BVTweet;
import com.epicqueststudios.bvtwitter.ui.viewholders.AbstractViewHolder;
import com.epicqueststudios.bvtwitter.ui.viewholders.MessageViewHolder;
import com.epicqueststudios.bvtwitter.ui.viewholders.TweetViewHolder;

import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<AbstractViewHolder> {

    private static final int TYPE_TWEET = 1;
    private static final int TYPE_MESSAGE = 2;
    private List<BVTweet> tweets;

    public TweetAdapter(List<BVTweet> tweets) {
        super();
        setTweets(tweets);
    }

    public void setTweets(List<BVTweet> tweets) {
        this.tweets = tweets;
    }

    @Override
    public AbstractViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_MESSAGE: {
                return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item, parent, false));
            }
            case TYPE_TWEET: {
                return new TweetViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet_list_item_with_image, parent, false));
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(AbstractViewHolder holder, int position) {
        BVTweet tweet = tweets.get(position);
        holder.bindView(tweet);
    }

    @Override
    public int getItemViewType(int position) {
        if (tweets.get(position) instanceof BVMessage)
            return TYPE_MESSAGE;
        return TYPE_TWEET;
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }


}
