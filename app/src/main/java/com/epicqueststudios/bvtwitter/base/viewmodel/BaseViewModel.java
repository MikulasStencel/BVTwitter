package com.epicqueststudios.bvtwitter.base.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.PropertyChangeRegistry;

public abstract class BaseViewModel<ITEM_T>  extends ViewModel implements Observable {
    private PropertyChangeRegistry registry = new PropertyChangeRegistry();
    private ObservableBoolean isLoading = new ObservableBoolean(false);

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        registry.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        registry.remove(callback);
    }
    public void setIsLoading(boolean isLoading) {
        this.isLoading.set(isLoading);
    }
    public ObservableBoolean getIsLoading() {
        return isLoading;
    }


    public abstract void setItem(ITEM_T item);

}
