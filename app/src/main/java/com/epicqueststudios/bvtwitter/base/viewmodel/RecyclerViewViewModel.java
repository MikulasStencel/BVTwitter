package com.epicqueststudios.bvtwitter.base.viewmodel;

import android.databinding.ObservableBoolean;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.epicqueststudios.bvtwitter.base.adapter.BaseRecyclerViewAdapter;

public abstract class RecyclerViewViewModel<ITEM_T> extends BaseViewModel<ITEM_T> {
    private final ObservableBoolean isLoading = new ObservableBoolean(false);

    RecyclerView.LayoutManager layoutManager;
    private Parcelable savedLayoutManagerState;

    protected abstract BaseRecyclerViewAdapter getAdapter();
    protected abstract RecyclerView.LayoutManager createLayoutManager();

    public RecyclerViewViewModel(@Nullable State savedInstanceState) {
        super(savedInstanceState);
        if (savedInstanceState instanceof RecyclerViewViewModelState) {
            savedLayoutManagerState = ((RecyclerViewViewModelState) savedInstanceState).layoutManagerState;
        }
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading.set(isLoading);
        this.isLoading.notifyChange();
    }
    public ObservableBoolean getIsLoading() {
        return isLoading;
    }

    @Override
    public RecyclerViewViewModelState getInstanceState() {
        return new RecyclerViewViewModelState(this);
    }

    public final void setupRecyclerView(RecyclerView recyclerView) {
        layoutManager = createLayoutManager();
        if (savedLayoutManagerState != null) {
            layoutManager.onRestoreInstanceState(savedLayoutManagerState);
            savedLayoutManagerState = null;
        }
        recyclerView.setAdapter(getAdapter());
        recyclerView.setLayoutManager(layoutManager);
    }

    protected static class RecyclerViewViewModelState extends State {

        private final Parcelable layoutManagerState;

        public RecyclerViewViewModelState(RecyclerViewViewModel viewModel) {
            super(viewModel);
            layoutManagerState = viewModel.layoutManager.onSaveInstanceState();
        }

        public RecyclerViewViewModelState(Parcel in) {
            super(in);
            layoutManagerState = in.readParcelable(
                    RecyclerView.LayoutManager.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeParcelable(layoutManagerState, flags);
        }

        public static Parcelable.Creator<RecyclerViewViewModelState> CREATOR =
                new Parcelable.Creator<RecyclerViewViewModelState>() {
                    @Override
                    public RecyclerViewViewModelState createFromParcel(Parcel source) {
                        return new RecyclerViewViewModelState(source);
                    }

                    @Override
                    public RecyclerViewViewModelState[] newArray(int size) {
                        return new RecyclerViewViewModelState[size];
                    }
                };
    }
}
