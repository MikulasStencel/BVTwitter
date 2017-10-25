package com.epicqueststudios.bvtwitter.di;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.epicqueststudios.bvtwitter.feature.twitter.BasicTwitterClient;
import com.epicqueststudios.bvtwitter.ui.activities.MainActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import okhttp3.OkHttpClient;

@Module
abstract class AndroidBindingModule {

   // @ContributesAndroidInjector
  //  abstract MainActivity mainActivity();
    @Provides
    static OkHttpClient provideOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
        return client;
    }


   /* @Singleton
    @Provides
    @Inject
    BasicTwitterClient provideBasicTwitterClient(Context context) {
        BasicTwitterClient client = new BasicTwitterClient(context);
        return client;
    }*/
}