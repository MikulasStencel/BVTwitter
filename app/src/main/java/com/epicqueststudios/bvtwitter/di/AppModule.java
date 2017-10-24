package com.epicqueststudios.bvtwitter.di;

import android.app.Application;
import android.content.Context;

import com.epicqueststudios.bvtwitter.BVTwitterApplication;
import com.epicqueststudios.bvtwitter.feature.twitter.BasicTwitterClient;
import com.epicqueststudios.bvtwitter.ui.activities.MainActivity;
import com.epicqueststudios.bvtwitter.ui.activities.MainActivityModule;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.AndroidInjectionModule;
import dagger.android.ContributesAndroidInjector;
/*
@Module(includes = AndroidInjectionModule.class)
abstract class AppModule {

    @Binds
    abstract Application application(BVTwitterApplication application);


}*/

@Module(includes = AndroidInjectionModule.class)
abstract class AppModule {
    @PerActivity
    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity mainActivity();
}