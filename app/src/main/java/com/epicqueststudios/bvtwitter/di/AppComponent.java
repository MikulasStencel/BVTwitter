package com.epicqueststudios.bvtwitter.di;


import android.app.Application;
import android.content.Context;

import com.epicqueststudios.bvtwitter.BVTwitterApplication;
import com.epicqueststudios.bvtwitter.ui.activities.MainActivity;
import com.epicqueststudios.bvtwitter.ui.activities.MainActivityModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
/*
@Singleton
@Component(modules = {
                AppModule.class,
                AndroidSupportInjectionModule.class,
                AndroidBindingModule.class})

public interface AppComponent extends AndroidInjector<BVTwitterApplication> {
    @Override
    void inject(BVTwitterApplication instance);



  @Component.Builder
  interface Builder {
      @BindsInstance
      AppComponent.Builder application(BVTwitterApplication application);
      AppComponent build();
  }
}
*/

@Singleton
@Component(modules = {AppModule.class, AndroidSupportInjectionModule.class, AndroidBindingModule.class, MainActivityModule.class})
public interface AppComponent {
     void inject(BVTwitterApplication app);
     //BVTwitterApplication getBVTwitterApplication();

     //Context getApplicationContext();
}