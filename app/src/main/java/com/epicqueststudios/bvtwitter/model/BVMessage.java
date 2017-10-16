package com.epicqueststudios.bvtwitter.model;

public class BVMessage extends BVTweet {

    public BVMessage(String message){
        super("{}");
        this.message = message;
        this.timeStamp = System.currentTimeMillis();
    }

    @Override
    public boolean useStorage() {
        return false;
    }

}
