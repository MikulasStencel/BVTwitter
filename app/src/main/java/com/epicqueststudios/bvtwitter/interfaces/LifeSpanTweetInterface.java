package com.epicqueststudios.bvtwitter.interfaces;

import com.epicqueststudios.bvtwitter.model.BVTweet;

public interface LifeSpanTweetInterface {
    boolean isExpired(BVTweet tweet, long now);

    int getID();
}
