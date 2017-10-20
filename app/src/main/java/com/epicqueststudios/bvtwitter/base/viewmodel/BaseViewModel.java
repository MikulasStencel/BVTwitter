package com.epicqueststudios.bvtwitter.base.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.PropertyChangeRegistry;
import android.support.annotation.Nullable;

public abstract class BaseViewModel<ITEM_T>  extends AbstractViewModel{
    private ObservableBoolean isLoading = new ObservableBoolean(false);
    protected ITEM_T item;

    public BaseViewModel(State savedInstanceState) {
        super(savedInstanceState);
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading.set(isLoading);
    }
    public ObservableBoolean getIsLoading() {
        return isLoading;
    }


    public void setItem(ITEM_T item){
        this.item = item;
    }


}
