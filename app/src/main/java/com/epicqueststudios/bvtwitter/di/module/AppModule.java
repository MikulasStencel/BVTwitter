package com.epicqueststudios.bvtwitter.di.module;


import android.app.Application;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.epicqueststudios.bvtwitter.feature.sqlite.DatabaseHandler;
import com.epicqueststudios.bvtwitter.feature.twitter.BasicTwitterClient;
import com.epicqueststudios.bvtwitter.feature.twitter.adapter.TweetAdapter;
import com.epicqueststudios.bvtwitter.feature.twitter.model.TweetFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
public class AppModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }
    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    DatabaseHandler provideDatabaseHandler(Context context) {
        return new DatabaseHandler(context);
    }

    @Provides
    TweetAdapter provideTweetAdapter() {
        return new TweetAdapter();
    }

    @Provides
    RecyclerView.LayoutManager provideLinearLayoutManager(Context context){
        return new LinearLayoutManager(context);
    }

    @Provides
    TweetFactory provideTweetFactory(Context context){
        return new TweetFactory(context);
    }

}