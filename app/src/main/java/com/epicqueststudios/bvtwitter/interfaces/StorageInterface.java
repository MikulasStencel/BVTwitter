package com.epicqueststudios.bvtwitter.interfaces;


import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;

import java.util.List;

public interface StorageInterface {
    List<BVTweetModel> getList();
}
