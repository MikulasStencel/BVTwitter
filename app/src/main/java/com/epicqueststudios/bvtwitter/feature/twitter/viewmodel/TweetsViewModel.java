package com.epicqueststudios.bvtwitter.feature.twitter.viewmodel;


import android.app.Activity;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.epicqueststudios.bvtwitter.base.adapter.BaseRecyclerViewAdapter;
import com.epicqueststudios.bvtwitter.base.viewmodel.RecyclerViewViewModel;
import com.epicqueststudios.bvtwitter.feature.lifespan.CleaningRoutine;
import com.epicqueststudios.bvtwitter.feature.sqlite.DatabaseHandler;
import com.epicqueststudios.bvtwitter.feature.twitter.BasicTwitterClient;
import com.epicqueststudios.bvtwitter.feature.twitter.adapter.TweetAdapter;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;
import com.epicqueststudios.bvtwitter.interfaces.ActivityInterface;
import com.epicqueststudios.bvtwitter.interfaces.StorageInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Component;
import dagger.android.AndroidInjection;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class TweetsViewModel extends RecyclerViewViewModel<TweetsViewModel> implements StorageInterface {
    private static final String TAG = TweetsViewModel.class.getSimpleName();
    private final Context appContext;

    private final BasicTwitterClient twitterClient;

    ArrayList<BVTweetModel> tweets;
    TweetAdapter adapter;
    private CleaningRoutine cleaningRoutine = null;
    private DatabaseHandler databaseHandler;


    private DisposableObserver<List<BVTweetModel>> cleanObserver;
    private final ObservableBoolean isRetryVisible = new ObservableBoolean(false);

    @Inject
    public TweetsViewModel(Context context, @Nullable State savedInstanceState, BasicTwitterClient twitterClient) {
        super(savedInstanceState);
        appContext = context.getApplicationContext();

        if (savedInstanceState instanceof TweetsState) {
            tweets = ((TweetsState) savedInstanceState).tweets;
        } else {
            tweets = new ArrayList<>();
        }
        adapter = new TweetAdapter();
        adapter.setItems(tweets);
        this.twitterClient = new BasicTwitterClient(appContext);
        ////this.twitterClient = twitterClient;
        databaseHandler = new DatabaseHandler(context);
        cleaningRoutine = new CleaningRoutine(this, (ActivityInterface)context, databaseHandler);
    }

    @Override
    protected BaseRecyclerViewAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(appContext);
    }

    @Override
    public RecyclerViewViewModelState getInstanceState() {
        return new TweetsState(this);
    }

    @Override
    public void setItem(TweetsViewModel item) {
        super.setItem(item);
    }

    public void setItems(List<BVTweetModel> tweets) {
        this.tweets.clear();
        this.tweets.addAll(tweets);
        adapter.setItems(this.tweets);
    }

    public void addTweet(BVTweetModel bvTweet) {
        this.tweets.add(0, bvTweet);
        adapter.addItem(bvTweet);
    }

    @Override
    public List<BVTweetModel> getList() {
        return this.tweets;
    }

    public void startCleaningRoutine(final Object lock) {
        cleanObserver = cleaningRoutine.startProcess()
                .subscribeWith(new DisposableObserver<List<BVTweetModel>>() {
            @Override
            public void onNext(List<BVTweetModel> expiredTweets) {
                deleteTweets(expiredTweets, lock);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage(), e);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void deleteTweets(List<BVTweetModel> expired, Object lock) {
        synchronized (lock){
            for (BVTweetModel model: expired){
                removeIfExists(model);
            }
           // this.tweets.removeAll(expired);
        }
    }

    private void removeIfExists(BVTweetModel model) {
       /* if (this.tweets.contains(model)){
            this.tweets.remove(model);
        }*/
        for (int i = 0; i < this.tweets.size(); i++) {
            if (this.tweets.get(i).getId() == model.getId()){
                this.tweets.remove(tweets.get(i));
                break;
            }
        }
    }

    public Flowable<BVTweetModel> startStream(String text) {
        Flowable<BVTweetModel> stream = twitterClient.getStream(text);
        stream = stream.doOnNext(bvTweetModel -> {
            if (bvTweetModel.useStorage())
                storeTweet(bvTweetModel);
        });
        return stream;
    }

    public void stopStream() {
        twitterClient.stopStream();
    }

    public void clearTweets() {
        databaseHandler.clearDB();
        setItems(new ArrayList<>());
    }


    public Single<List<BVTweetModel>> loadFromDatabase() {
        return Single.fromCallable(() -> databaseHandler.getAllTweets())
                .doOnSubscribe(v -> setIsLoading(true))
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(result -> {
                    setIsLoading(false);
                    setItems(result);
                }
        );
    }

    public void onPause() {
        stopStream();
        if (cleanObserver != null && !cleanObserver.isDisposed()) {
            Log.d(TAG, "cleaning routine has been disposed");
            cleanObserver.dispose();
            cleanObserver = null;
        }
    }

    public void showRetry(boolean bShow) {
        this.isRetryVisible.set(bShow);
        this.isRetryVisible.notifyChange();
    }

    public ObservableBoolean getIsRetryVisible() {
        return isRetryVisible;
    }


    public void storeTweet(BVTweetModel tweet) {
        databaseHandler.storeTweet(tweet);
    }

    private static class TweetsState extends RecyclerViewViewModelState {

        private final ArrayList<BVTweetModel> tweets;

        public TweetsState(TweetsViewModel viewModel) {
            super(viewModel);
            tweets = viewModel.adapter.getItems();
        }

        public TweetsState(Parcel in) {
            super(in);
            tweets = in.createTypedArrayList(BVTweetModel.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeTypedList(tweets);
        }

        public static Creator<TweetsState> CREATOR = new Creator<TweetsState>() {
            @Override
            public TweetsState createFromParcel(Parcel source) {
                return new TweetsState(source);
            }

            @Override
            public TweetsState[] newArray(int size) {
                return new TweetsState[size];
            }
        };
    }


}