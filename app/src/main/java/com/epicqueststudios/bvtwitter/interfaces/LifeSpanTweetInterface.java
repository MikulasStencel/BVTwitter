package com.epicqueststudios.bvtwitter.interfaces;

import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;

public interface LifeSpanTweetInterface {
    boolean isExpired(BVTweetModel tweet, long now);

    int getID();
}
