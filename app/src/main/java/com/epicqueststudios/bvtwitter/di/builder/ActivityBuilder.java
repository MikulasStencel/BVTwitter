package com.epicqueststudios.bvtwitter.di.builder;

import com.epicqueststudios.bvtwitter.ui.activities.MainActivity;
import com.epicqueststudios.bvtwitter.ui.activities.MainActivityModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity bindMainActivity();

}