package com.epicqueststudios.bvtwitter.feature.lifespan;

import android.util.Log;

import com.epicqueststudios.bvtwitter.feature.sqlite.DatabaseHandler;
import com.epicqueststudios.bvtwitter.interfaces.ActivityInterface;
import com.epicqueststudios.bvtwitter.model.BVTweet;
import com.epicqueststudios.bvtwitter.utils.NetworkUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class CleaningRoutine {
    private static final String TAG = CleaningRoutine.class.getSimpleName();
    private final Object tweetsLock;
    private final ActivityInterface activityInterface;
    private final DatabaseHandler databaseHandler;
    private DisposableObserver<Long> cleanRoutine = null;
    private Observable<Long> cleanObserver;

    public CleaningRoutine(Object tweetsLock, ActivityInterface activityInterface, DatabaseHandler databaseHandler) {
        this.tweetsLock = tweetsLock;
        this.activityInterface = activityInterface;
        this.databaseHandler = databaseHandler;
    }

   /* public void startProcess() {
        cleanRoutine = Observable.interval(1000, 10000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .doOnEach(v-> deleteExpiredTweets(v))
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<Long>()  {

                    @Override
                    public void onNext(Long aLong) {
                        updateTweets(tweets);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage(), e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }*/

    public Observable<Long> startProcess() {
        cleanObserver = Observable.interval(1000, 10000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .doOnEach(v-> deleteExpiredTweets(v))
                .observeOn(AndroidSchedulers.mainThread());
        return cleanObserver;
    }

    private void deleteExpiredTweets(Notification<Long> notification) {
        if (!activityInterface.isOnline()){
            // do not delete tweets when uer is online
            return;
        }
        Log.d(TAG, "start cleaning routine "+notification.getValue());
        synchronized (tweetsLock) {
            List<BVTweet> tweets = activityInterface.getTweets();

            int deleted = 0;
            long now = System.currentTimeMillis();
            for (int i = 0; i < tweets.size(); i++) {
                if (tweets.get(i).isExpired(now)){
                    deleted++;
                    tweets.remove(i);
                    databaseHandler.deleteTweet(tweets.get(i));
                }
            }
            Log.d(TAG, "end of cleaning routine. "+deleted+" items removed.");
        }
    }

    public void onPause() {
        if (cleanRoutine != null && !cleanRoutine.isDisposed()) {
            cleanRoutine.dispose();
            cleanRoutine = null;
        }
    }

}
