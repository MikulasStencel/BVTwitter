package com.epicqueststudios.bvtwitter.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.databinding.PropertyChangeRegistry;

public class BaseViewModel extends ViewModel implements Observable {
    private PropertyChangeRegistry registry = new PropertyChangeRegistry();
    private ObservableField<Boolean> isLoading = new ObservableField<>(false);
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
    public ObservableField<Boolean> getIsLoading() {
        return isLoading;
    }
}
