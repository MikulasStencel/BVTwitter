package com.epicqueststudios.bvtwitter.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.epicqueststudios.bvtwitter.base.viewmodel.AbstractViewModel;
import com.epicqueststudios.bvtwitter.base.viewmodel.BaseViewModel;

import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseActivity extends DaggerAppCompatActivity {
    protected CompositeDisposable compositeDisposable;
    private static final String EXTRA_VIEW_MODEL_STATE = "viewModelState";
    private AbstractViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
        BaseViewModel.State savedViewModelState = null;
        if (savedInstanceState != null) {
            savedViewModelState = savedInstanceState.getParcelable(EXTRA_VIEW_MODEL_STATE);
        }
        viewModel = createViewModel(savedViewModelState);
    }

    @Override
    protected void onDestroy() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
        super.onDestroy();
    }
    @Nullable
    protected abstract AbstractViewModel createViewModel(@Nullable AbstractViewModel.State savedViewModelState);


    @Override
    public void onStart() {
        super.onStart();
        if (viewModel != null) {
            viewModel.onStart();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (viewModel != null) {
            outState.putParcelable(EXTRA_VIEW_MODEL_STATE, viewModel.getInstanceState());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (viewModel != null) {
            viewModel.onStop();
        }
    }
}