package com.epicqueststudios.bvtwitter.feature.twitter.model;

public class BVMessageModel extends BVTweetModel {

    public BVMessageModel(String message){
        super("{}");
        this.message = message;
        this.timeStamp = System.currentTimeMillis();
    }

    @Override
    public boolean useStorage() {
        return false;
    }

}
