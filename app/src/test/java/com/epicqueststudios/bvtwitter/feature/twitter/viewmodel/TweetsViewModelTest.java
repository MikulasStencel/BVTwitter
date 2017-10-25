package com.epicqueststudios.bvtwitter.feature.twitter.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.PropertyChangeRegistry;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.epicqueststudios.bvtwitter.feature.lifespan.CleaningRoutine;
import com.epicqueststudios.bvtwitter.feature.sqlite.DatabaseHandler;
import com.epicqueststudios.bvtwitter.feature.twitter.BasicTwitterClient;
import com.epicqueststudios.bvtwitter.feature.twitter.adapter.TweetAdapter;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;
import com.epicqueststudios.bvtwitter.interfaces.ActivityInterface;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;

import static org.mockito.Mockito.*;


public class TweetsViewModelTest {
    @Mock
    Context appContext;
    @Mock
    BasicTwitterClient twitterClient;
    @Mock
    TweetAdapter adapter;
    TweetsViewModel tweetsViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        twitterClient = new BasicTwitterClient(appContext);
        DatabaseHandler databaseHandler = new DatabaseHandler(appContext);
        tweetsViewModel = new TweetsViewModel(null, twitterClient, databaseHandler, new LinearLayoutManager(appContext), adapter);
        ActivityInterface activityInterface = () -> false;
        CleaningRoutine cleaningRoutine = new CleaningRoutine(tweetsViewModel, activityInterface, databaseHandler);
        tweetsViewModel.setCleaningRoutine(cleaningRoutine);
    }


    @Test
    public void testAddTweet() throws Exception {
        List<BVTweetModel> list = tweetsViewModel.getList();

        Assert.assertEquals(0, list.size());
        tweetsViewModel.addTweet(new BVTweetModel("line"));
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testOnTweetStreamComplete() throws Exception {
        CountDownLatch lock = new CountDownLatch(1);

        PublishSubject<UIEvent> events = tweetsViewModel.getEvents();
        final UIEvent[] event = new UIEvent[1];
        events.subscribe(new DisposableObserver<UIEvent>() {
            @Override
            public void onNext(UIEvent uiEvent) {
                Assert.assertEquals(UIEvent.TYPE.ADD_TWEET, uiEvent.getType());
                Assert.assertNotEquals(null, uiEvent.getData());
                event[0] = uiEvent;
            }

            @Override
            public void onError(Throwable e) {
                Assert.assertTrue(e.getMessage(), true);
            }

            @Override
            public void onComplete() {

            }
        });

        tweetsViewModel.onTweetStreamComplete();

        lock.await(1000, TimeUnit.MILLISECONDS);
        Assert.assertNotEquals(null, event[0]);
        Assert.assertNotEquals(null, event[0].getData());
    }
}
