package com.epicqueststudios.bvtwitter.feature.twitter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.epicqueststudios.bvtwitter.R;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVMessageModel;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;
import com.epicqueststudios.bvtwitter.ui.viewholders.AbstractViewHolder;
import com.epicqueststudios.bvtwitter.ui.viewholders.MessageViewHolder;
import com.epicqueststudios.bvtwitter.ui.viewholders.TweetViewHolder;

import java.util.List;

public class TweetAdapterObsolete extends RecyclerView.Adapter<AbstractViewHolder> {

    private static final int TYPE_TWEET = 1;
    private static final int TYPE_MESSAGE = 2;
    private List<BVTweetModel> tweets;

    public TweetAdapterObsolete(List<BVTweetModel> tweets) {
        super();
        setTweets(tweets);
    }

    public void setTweets(List<BVTweetModel> tweets) {
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
        BVTweetModel tweet = tweets.get(position);
        holder.bindView(tweet);
    }

    @Override
    public int getItemViewType(int position) {
        if (tweets.get(position) instanceof BVMessageModel)
            return TYPE_MESSAGE;
        return TYPE_TWEET;
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }


}
