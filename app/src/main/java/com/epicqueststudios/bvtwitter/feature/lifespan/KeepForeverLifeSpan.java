package com.epicqueststudios.bvtwitter.feature.lifespan;

import com.epicqueststudios.bvtwitter.interfaces.LifeSpanTweetInterface;
import com.epicqueststudios.bvtwitter.model.BVTweet;

import static com.epicqueststudios.bvtwitter.feature.lifespan.LifeSpanTweetFactory.TYPE.BASIC_TIME;
import static com.epicqueststudios.bvtwitter.feature.lifespan.LifeSpanTweetFactory.TYPE.KEEP_FOREVER;

public class KeepForeverLifeSpan implements LifeSpanTweetInterface {
    @Override
    public boolean isExpired(BVTweet tweet, long now) {
        return false;
    }

    @Override
    public int getID() {
        return KEEP_FOREVER.ordinal();
    }
}
