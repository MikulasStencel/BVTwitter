package com.epicqueststudios.bvtwitter.feature.twitter.viewmodel;


import android.content.Context;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.epicqueststudios.bvtwitter.base.adapter.BaseRecyclerViewAdapter;
import com.epicqueststudios.bvtwitter.base.viewmodel.RecyclerViewViewModel;
import com.epicqueststudios.bvtwitter.feature.twitter.adapter.TweetAdapter;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;

import java.util.ArrayList;

public class TweetsViewModel extends RecyclerViewViewModel<TweetsViewModel> {
    private final Context appContext;

    TweetAdapter adapter;

    public TweetsViewModel(Context context, @Nullable State savedInstanceState) {
        super(savedInstanceState);
        appContext = context.getApplicationContext();

        ArrayList<BVTweetModel> versions;
        if (savedInstanceState instanceof TweetsState) {
            versions = ((TweetsState) savedInstanceState).versions;
        } else {
            versions = new ArrayList<>();
        }
        adapter = new TweetAdapter();
        adapter.setItems(versions);
    }

    @Override
    protected BaseRecyclerViewAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(appContext);
    }

    @Override
    public RecyclerViewViewModelState getInstanceState() {
        return new TweetsState(this);
    }

    @Override
    public void setItem(TweetsViewModel item) {

    }


    private static class TweetsState extends RecyclerViewViewModelState {

        private final ArrayList<BVTweetModel> versions;

        public TweetsState(TweetsViewModel viewModel) {
            super(viewModel);
            versions = viewModel.adapter.getItems();
        }

        public TweetsState(Parcel in) {
            super(in);
            versions = in.createTypedArrayList(BVTweetModel.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeTypedList(versions);
        }

        public static Creator<TweetsState> CREATOR = new Creator<TweetsState>() {
            @Override
            public TweetsState createFromParcel(Parcel source) {
                return new TweetsState(source);
            }

            @Override
            public TweetsState[] newArray(int size) {
                return new TweetsState[size];
            }
        };
    }
}