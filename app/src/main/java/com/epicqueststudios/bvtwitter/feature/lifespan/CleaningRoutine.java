package com.epicqueststudios.bvtwitter.feature.lifespan;

import android.util.Log;

import com.epicqueststudios.bvtwitter.feature.sqlite.DatabaseHandler;
import com.epicqueststudios.bvtwitter.interfaces.ActivityInterface;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;
import com.epicqueststudios.bvtwitter.interfaces.StorageInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class CleaningRoutine {
    private static final String TAG = CleaningRoutine.class.getSimpleName();
    private static final long CHECK_EXPIRED_TWEETS_TIME_INTERVAL = 10000;
    private final Object cleaningLock = new Object();
    private final StorageInterface storageInterface;
    private final DatabaseHandler databaseHandler;
    private final ActivityInterface activityInterface;
    private Observable<List<BVTweetModel>> cleanObserver;

    public CleaningRoutine(StorageInterface storageInterface, ActivityInterface activityInterface, DatabaseHandler databaseHandler) {
        this.storageInterface = storageInterface;
        this.activityInterface = activityInterface;
        this.databaseHandler = databaseHandler;
    }

    public Observable<List<BVTweetModel>> startProcess() {
        cleanObserver = Observable.interval(5000, CHECK_EXPIRED_TWEETS_TIME_INTERVAL, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .map(a -> collectExpiredTweets(storageInterface.getList()))
                .observeOn(AndroidSchedulers.mainThread());

        return cleanObserver;
    }

    private List<BVTweetModel> collectExpiredTweets(List<BVTweetModel> oldTweets) {
        List<BVTweetModel> expiredTweets = new ArrayList<>();
        if (!activityInterface.isOnline()){
            return expiredTweets;
        }
        Log.d(TAG, "-- start cleaning routine");
        synchronized (cleaningLock) {
            int deleted = 0;
            long now = System.currentTimeMillis();
            for (int i = 0; i < oldTweets.size(); i++) {
                if (oldTweets.get(i).isExpired(now)){
                    deleted++;
                    expiredTweets.add(oldTweets.get(i));
                }
            }
            databaseHandler.deleteTweets(expiredTweets);
            Log.d(TAG, "-- end of cleaning routine. "+deleted+" items removed.");
        }
        return oldTweets;
    }

}
