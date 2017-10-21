package com.epicqueststudios.bvtwitter.utils;


import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;

import java.util.List;

public class ListUtils {
    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }
}
