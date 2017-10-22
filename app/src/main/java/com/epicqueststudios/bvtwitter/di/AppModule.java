package com.epicqueststudios.bvtwitter.di;

import android.app.Application;
import android.content.Context;

import com.epicqueststudios.bvtwitter.BVTwitterApplication;
import com.epicqueststudios.bvtwitter.feature.twitter.BasicTwitterClient;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.AndroidInjectionModule;

@Module(includes = AndroidInjectionModule.class)
abstract class AppModule {

    @Binds
    abstract Application application(BVTwitterApplication application);


}