package com.epicqueststudios.bvtwitter.ui.activities;

import android.support.v7.app.AppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;


public class RxActivity extends AppCompatActivity {
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
