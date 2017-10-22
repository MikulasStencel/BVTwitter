package com.epicqueststudios.bvtwitter;

import com.epicqueststudios.bvtwitter.di.AppComponent;
import com.epicqueststudios.bvtwitter.di.DaggerAppComponent;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;


public class BVTwitterApplication extends DaggerApplication {

   /* @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().create(this);
    }*/
    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        AppComponent appComponent = DaggerAppComponent.builder().application(this).build();
        appComponent.inject(this);
        return appComponent;
    }
}
