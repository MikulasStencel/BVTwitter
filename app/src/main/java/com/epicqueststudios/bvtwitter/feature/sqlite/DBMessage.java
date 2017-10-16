package com.epicqueststudios.bvtwitter.feature.sqlite;

import com.epicqueststudios.bvtwitter.model.BVTweet;

public class DBMessage {
    public enum BVEvent {ADD, DELETE}

    public BVTweet tweet;
    public BVEvent eventType;
    public DBMessage(BVTweet tweet, BVEvent eventType){
        this.tweet = tweet;
        this.eventType = eventType;
    }

}
