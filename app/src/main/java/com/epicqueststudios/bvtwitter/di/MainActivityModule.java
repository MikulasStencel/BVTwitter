package com.epicqueststudios.bvtwitter.di;


import android.app.Activity;

import com.epicqueststudios.bvtwitter.ui.activities.MainActivity;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
abstract class MainActivityModule {

    @Binds
    abstract Activity bindActivity(MainActivity activity);

    @Provides
    static OkHttpClient provideOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
        return client;
    }

   /* @Singleton
    @Provides
    static BasicTwitterClient provideBasicTwitterClient(Context context) {
        BasicTwitterClient client = new BasicTwitterClient(context);
        return client;
    }*/
}