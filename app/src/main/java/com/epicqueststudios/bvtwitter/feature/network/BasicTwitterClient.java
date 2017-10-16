package com.epicqueststudios.bvtwitter.feature.network;


import android.content.Context;
import android.util.Log;

import com.epicqueststudios.bvtwitter.R;
import com.epicqueststudios.bvtwitter.feature.lifespan.LifeSpanTweetFactory;
import com.epicqueststudios.bvtwitter.feature.lifespan.TimeLifeSpan;
import com.epicqueststudios.bvtwitter.model.BVMessage;
import com.epicqueststudios.bvtwitter.model.BVTweet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

public class BasicTwitterClient {
    private static final String TAG = BasicTwitterClient.class.getSimpleName();
    private static final String STREAM_URL = "https://stream.twitter.com/1.1/statuses/filter.json?track=";
    private final Context context;
    //  private static BasicTwitterClient instance;

    private OkHttpClient client = null;
    private boolean bKeepRunning = true;

    public BasicTwitterClient(Context context){
        this.context = context;
    }

    /*public synchronized static BasicTwitterClient getInstance(){
        if (instance == null){
            instance = new BasicTwitterClient();
        }
        return instance;
    }*/

    public void stopStream(){
        bKeepRunning = false;
    }

    private OkHttpClient prepareClient(){
        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer( "deZ0o9TRsbHFMYG27K4GJs5JY",
                "HZGfh9NyvekEDCOIkZDAsgCd0QyeywwCATCMkln8C5aZuPKZzc");
        consumer.setTokenWithSecret(	"918198327459696641-jVkuuOyqlvs7DiwEc6ZfHTBBdl3VOxp",
                "fSfXrlqGiAygnOmwbXjGNzy33nlld8q6QLFR9jG2AvTJ6");
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        client = new OkHttpClient.Builder()
                .addInterceptor(new SigningInterceptor(consumer))
                .addInterceptor(logging)
                .readTimeout(0, TimeUnit.SECONDS)
                .build();

        return client;
    }

    private Request buildUrlRequest(String streamUrl) {
        if (client == null){
            prepareClient();
        }
        return new Request.Builder().url(streamUrl).build();
    }

    public Observable<BVTweet> getStream(String text){
        return Observable.create((ObservableOnSubscribe<BVTweet>) tweetEmitter -> {
            Request request = BasicTwitterClient.this.buildUrlRequest(STREAM_URL.concat(text));
            BufferedReader reader = null;
            try {
                tweetEmitter.onNext(new BVMessage(context.getString(R.string.connecting_to_stream_message) + " " +text));
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    tweetEmitter.onNext(new BVMessage(context.getString(R.string.stream_opened_message)));
                    Log.d(TAG, "Stream opened.");
                    InputStream in = response.body().byteStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    BasicTwitterClient.this.parseTweets(reader, tweetEmitter);
                } else {
                    Log.e(TAG, "Unsuccessful attempt to open stream.");
                  //  tweetEmitter.onNext(new BVMessage(context.getString(R.string.stream_closed_message)));
                    tweetEmitter.onError(new IOException(response.code() + ": " + response.body().string()));
                    return;
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
                if (!tweetEmitter.isDisposed()) {
                 //   tweetEmitter.onNext(new BVMessage(context.getString(R.string.stream_closed_message)));
                    tweetEmitter.onError(e);
                }
                return;
            } finally {
                if (reader != null) {
                    reader.close();
                    Log.d(TAG, "Stream closed.");
                }
            }
        }).doOnSubscribe(v -> bKeepRunning = true);
    }

    private void parseTweets(BufferedReader reader, ObservableEmitter<BVTweet> tweetEmitter) {
        try {
            String line = "";
            do {
                line = reader.readLine();
                //Log.d(TAG, "Stream active: " + bKeepRunning + "  Line: [" + line+"]");
                if (line == null || line.isEmpty()) {
                    continue;
                }
                BVTweet tweet = new BVTweet(line);
                if (tweet.hasMessage()) {
                    tweet.setLifeSpan(new TimeLifeSpan(LifeSpanTweetFactory.DEFAULT_EXPIRE));
                    tweetEmitter.onNext(tweet);
                }
            } while (bKeepRunning);

            tweetEmitter.onNext(new BVMessage(context.getString(R.string.stream_closed_message)));
            tweetEmitter.onComplete();
        } catch (Exception e) {
            if (!tweetEmitter.isDisposed()) {
                tweetEmitter.onError(e);
            }
        }
    }
}
