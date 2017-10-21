package com.epicqueststudios.bvtwitter.utils;

import io.reactivex.FlowableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public final class RxUtils {

    public static <T> ObservableTransformer<T, T> applyObservableOnMainThread() {
        return observable -> observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> FlowableTransformer<T, T> applyFlowableOnMainThread() {
        return flowable -> flowable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> applySingleOnMainThread() {
        return single -> single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
