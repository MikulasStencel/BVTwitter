package com.epicqueststudios.bvtwitter.di;

import com.epicqueststudios.bvtwitter.ui.activities.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class AndroidBindingModule {

    @ContributesAndroidInjector
    abstract MainActivity mainActivity();

}