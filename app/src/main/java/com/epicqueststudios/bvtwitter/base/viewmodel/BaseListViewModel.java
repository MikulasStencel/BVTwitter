package com.epicqueststudios.bvtwitter.base.viewmodel;


import android.database.sqlite.SQLiteOpenHelper;
import android.databinding.ObservableBoolean;

import com.epicqueststudios.bvtwitter.base.feature.BaseClientApi;

public class BaseListViewModel extends BaseObservableViewModel {

    private final String TAG = this.getClass().getSimpleName();
    private final BaseClientApi clientApi;
    private final SQLiteOpenHelper databaseHandler;

    private final ObservableBoolean isListEmpty = new ObservableBoolean(true);
    private final ObservableBoolean showingMovies = new ObservableBoolean();
    private final ObservableBoolean showingTvShows = new ObservableBoolean();
    private final ObservableBoolean showingPlaylist = new ObservableBoolean();
    private final ObservableBoolean showingDownloads = new ObservableBoolean();
    private final ObservableBoolean isConnectivity = new ObservableBoolean(true);

    public ObservableBoolean getIsConnectivity() {
        return isConnectivity;
    }

    public BaseListViewModel(BaseClientApi clientApi, SQLiteOpenHelper databaseHandler) {
        this.clientApi = clientApi;
        this.databaseHandler = databaseHandler;
    }

    public ObservableBoolean getIsListEmpty() {
        return isListEmpty;
    }

 }