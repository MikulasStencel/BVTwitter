package com.epicqueststudios.bvtwitter.feature.lifespan;

import android.app.Activity;

import com.epicqueststudios.bvtwitter.interfaces.LifeSpanTweetInterface;
import com.epicqueststudios.bvtwitter.model.BVTweet;


public class LifeSpanTweetFactory {

    public enum TYPE {KEEP_FOREVER, BASIC_TIME, KEEP_ON_NO_NETWORK, KEEP_ON_TOUCH}
    public static final long DEFAULT_EXPIRE = 60000;

    public static LifeSpanTweetInterface createLifeSpanByType(int index, Activity activity, Object...params){
        return createLifeSpanByType(TYPE.values()[index], activity, params);
    }

    public static LifeSpanTweetInterface createLifeSpanByType(TYPE type, Activity activity, Object...params){
        switch (type){
            case KEEP_FOREVER: return new KeepForeverLifeSpan();
            case BASIC_TIME: return new TimeLifeSpan((Long) params[0]);
            case KEEP_ON_NO_NETWORK: return KeepOnNoNetwork.getInstance(activity);
            case KEEP_ON_TOUCH: return new KeepOnTouchLifeSpan((Long) params[0]);
        }
        return null;
    }

    public static LifeSpanTweetInterface createLifeSpanByTweet(BVTweet tweet){
        if (tweet.getName() != null)
            new TimeLifeSpan(DEFAULT_EXPIRE);

        return new KeepForeverLifeSpan();
    }
}
