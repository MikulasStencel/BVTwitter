package com.epicqueststudios.bvtwitter.feature.twitter;

import android.content.Context;

import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Flowable;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static org.mockito.Mockito.*;


public class BasicTwitterClientTest {
    @Mock
    Context context;
    @Mock
    OkHttpClient client;
    @InjectMocks
    BasicTwitterClient basicTwitterClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testStopStream() throws Exception {
        basicTwitterClient.stopStream();
    }

    @Test
    public void testPrepareClient() throws Exception {
        OkHttpClient result = basicTwitterClient.prepareClient();
        Assert.assertTrue("Prepared client isn't instance of OkHttpClient", result instanceof OkHttpClient);

    }

    @Test
    public void testGetStream() throws Exception {
        Flowable<BVTweetModel> result = basicTwitterClient.getStream("text");
        Assert.assertNotEquals(null, result);
    }

    @Test
    public void testBuildUrlRequest() throws Exception {
        Request result = basicTwitterClient.buildUrlRequest("https://stream.twitter.com/1.1/statuses/filter.json?track=anything");
        OkHttpClient client = basicTwitterClient.getClient();
        Assert.assertNotEquals(null, client);
        Assert.assertNotEquals(null, result);
    }
}
