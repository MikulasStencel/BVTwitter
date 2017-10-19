package com.epicqueststudios.bvtwitter.base.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.database.sqlite.SQLiteOpenHelper;
import com.epicqueststudios.bvtwitter.base.feature.BaseClientApi;

public class BaseListViewModelFactory implements ViewModelProvider.Factory {

    private final BaseClientApi clientApi;
    private final SQLiteOpenHelper databaseHandler;

    public BaseListViewModelFactory(BaseClientApi clientApi, SQLiteOpenHelper databaseHandler) {
        this.clientApi = clientApi;
        this.databaseHandler = databaseHandler;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BaseListViewModel.class)) {
            return (T) new BaseListViewModel(clientApi, databaseHandler);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
