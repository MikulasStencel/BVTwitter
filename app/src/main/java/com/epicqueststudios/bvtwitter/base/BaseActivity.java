package com.epicqueststudios.bvtwitter.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import io.reactivex.disposables.CompositeDisposable;

public class BaseActivity extends AppCompatActivity {
    protected CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onDestroy() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
        super.onDestroy();
    }
}