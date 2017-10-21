package com.epicqueststudios.bvtwitter.feature.lifespan;

import com.epicqueststudios.bvtwitter.interfaces.LifeSpanTweetInterface;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;

import static com.epicqueststudios.bvtwitter.feature.lifespan.LifeSpanTweetFactory.TYPE.BASIC_TIME;

public class TimeLifeSpan implements LifeSpanTweetInterface {
    protected final long duration;

    public TimeLifeSpan(long duration){
        this.duration = duration;
    }

    public TimeLifeSpan() {
        this(LifeSpanTweetFactory.DEFAULT_EXPIRE);
    }

    @Override
    public boolean isExpired(BVTweetModel tweet, long now) {
        long ts = tweet.getTimeStamp();
        long span = now - ts;
        return (ts > 0L && span > duration);
    }

    @Override
    public int getID() {
        return BASIC_TIME.ordinal();
    }
}
