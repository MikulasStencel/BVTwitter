package com.epicqueststudios.bvtwitter.base;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import io.reactivex.disposables.CompositeDisposable;

public class SubscriptionHandler implements LifecycleObserver {
    protected CompositeDisposable disposable;

    public SubscriptionHandler(Lifecycle lifecycle) {
        lifecycle.addObserver(this);
        disposable = new CompositeDisposable();
    }

    public CompositeDisposable getDisposable() {
        return disposable;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.clear();
        }
    }
}