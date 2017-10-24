package com.epicqueststudios.bvtwitter.utils;


import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ListUtils {

    public static <T extends Collection<U>, U> boolean isEmpty(T t) {
        return t == null || t.isEmpty();
    }
    public static boolean isEmpty(String t) {
        return t == null || t.isEmpty();
    }
}
