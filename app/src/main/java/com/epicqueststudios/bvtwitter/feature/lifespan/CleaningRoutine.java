package com.epicqueststudios.bvtwitter.feature.lifespan;

import android.util.Log;

import com.epicqueststudios.bvtwitter.feature.sqlite.DatabaseHandler;
import com.epicqueststudios.bvtwitter.interfaces.ActivityInterface;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class CleaningRoutine {
    private static final String TAG = CleaningRoutine.class.getSimpleName();
    private static final long CHECK_EXPIRED_TWEETS_TIME_INTERVAL = 10000;
    private final Object tweetsLock;
    private final ActivityInterface activityInterface;
    private final DatabaseHandler databaseHandler;
    private DisposableObserver<Long> cleanRoutine = null;
    private Observable<List<BVTweetModel>> cleanObserver;

    public CleaningRoutine(Object tweetsLock, ActivityInterface activityInterface, DatabaseHandler databaseHandler) {
        this.tweetsLock = tweetsLock;
        this.activityInterface = activityInterface;
        this.databaseHandler = databaseHandler;
    }

    public Observable<List<BVTweetModel>> startProcess() {
        cleanObserver = Observable.interval(1000, CHECK_EXPIRED_TWEETS_TIME_INTERVAL, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .map(a -> checkAndDeleteExpiredTweets(activityInterface.getTweets()))
                .observeOn(AndroidSchedulers.mainThread());
        return cleanObserver;
    }

    private List<BVTweetModel> checkAndDeleteExpiredTweets(List<BVTweetModel> oldTweets) {
        if (!activityInterface.isOnline()){
            return oldTweets;
        }
        Log.d(TAG, "-- start cleaning routine");
        synchronized (tweetsLock) {
            int deleted = 0;
            long now = System.currentTimeMillis();
            for (int i = 0; i < oldTweets.size(); i++) {
                if (oldTweets.get(i).isExpired(now)){
                    deleted++;
                    oldTweets.remove(i);
                    databaseHandler.deleteTweet(oldTweets.get(i));
                }
            }
            Log.d(TAG, "-- end of cleaning routine. "+deleted+" items removed.");
        }
        return oldTweets;
    }

    public void onPause() {
        if (cleanRoutine != null && !cleanRoutine.isDisposed()) {
            cleanRoutine.dispose();
            cleanRoutine = null;
        }
    }

}
