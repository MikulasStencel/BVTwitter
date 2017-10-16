package com.epicqueststudios.bvtwitter.feature.lifespan;

import com.epicqueststudios.bvtwitter.interfaces.LifeSpanTweetInterface;
import com.epicqueststudios.bvtwitter.model.BVTweet;

import static com.epicqueststudios.bvtwitter.feature.lifespan.LifeSpanTweetFactory.TYPE.BASIC_TIME;

public class TimeLifeSpan implements LifeSpanTweetInterface {
    protected final long duration;

    /**
     * @duration lifetime of tweet in miliseconds
     * */
    public TimeLifeSpan(long duration){
        this.duration = duration;
    }

    @Override
    public boolean isExpired(BVTweet tweet, long now) {
        long ts = tweet.getTimeStamp();
        long span = now - ts;
        return (ts > 0L && span > duration);
    }

    @Override
    public int getID() {
        return BASIC_TIME.ordinal();
    }
}
