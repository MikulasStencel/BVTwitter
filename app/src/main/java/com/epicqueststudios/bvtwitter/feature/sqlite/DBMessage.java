package com.epicqueststudios.bvtwitter.feature.sqlite;

import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;

public class DBMessage {
    public enum BVEvent {ADD, DELETE}

    public BVTweetModel tweet;
    public BVEvent eventType;
    public DBMessage(BVTweetModel tweet, BVEvent eventType){
        this.tweet = tweet;
        this.eventType = eventType;
    }

}
