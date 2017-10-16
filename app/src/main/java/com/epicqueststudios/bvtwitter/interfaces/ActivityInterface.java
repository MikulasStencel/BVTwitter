package com.epicqueststudios.bvtwitter.interfaces;

import com.epicqueststudios.bvtwitter.model.BVTweet;

import java.util.List;

public interface ActivityInterface {
    List<BVTweet> getTweets();
    boolean isOnline();
}
