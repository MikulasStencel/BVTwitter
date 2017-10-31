package com.epicqueststudios.bvtwitter.feature.twitter.model;

import android.content.Context;

import javax.inject.Inject;

public class TweetFactory {

    Context context;
    @Inject
    public TweetFactory(Context context) {
        this.context = context;
    }

    public BVTweetModel createTweet(String line)
    {
        return new BVTweetModel(line);
    }
}
