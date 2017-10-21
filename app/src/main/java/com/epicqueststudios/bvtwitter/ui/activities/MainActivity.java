package com.epicqueststudios.bvtwitter.ui.activities;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.epicqueststudios.bvtwitter.R;
import com.epicqueststudios.bvtwitter.base.BaseActivity;
import com.epicqueststudios.bvtwitter.base.viewmodel.BaseViewModel;
import com.epicqueststudios.bvtwitter.databinding.ActivityMainBinding;
import com.epicqueststudios.bvtwitter.feature.sqlite.DatabaseHandler;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVMessageModel;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;
import com.epicqueststudios.bvtwitter.feature.twitter.viewmodel.TweetsViewModel;
import com.epicqueststudios.bvtwitter.interfaces.ActivityInterface;
import com.epicqueststudios.bvtwitter.utils.ListUtils;
import com.epicqueststudios.bvtwitter.utils.NetworkUtils;
import com.epicqueststudios.bvtwitter.utils.RxUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements ActivityInterface {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MIN_TEXT_LENGTH = 2;
    private static final int MAX_TEXT_LENGTH = 50;
    private static final String KEY_LAST_SEARCH = "prefs_last_search";
    private TweetsViewModel tweetsViewModel;
    private DisposableObserver<BVTweetModel> observer;
    private Object tweetsLock = new Object();

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.edit_text_search)
    EditText searchEditText;

    @BindView(R.id.fab)
    FloatingActionButton actionButton;

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setViewModel(tweetsViewModel);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                startStream();
                searchEditText.clearFocus();
                hideKeyboard();
                return true;
            }
            return false;
        });

        actionButton.setOnClickListener(view -> Snackbar.make(view, "Clear all tweets", Snackbar.LENGTH_LONG)
                .setAction("Clear", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clearTweets();
                    }
                }).show());
    }

    @Nullable
    @Override
    protected BaseViewModel createViewModel(@Nullable BaseViewModel.State savedViewModelState) {
        tweetsViewModel = new TweetsViewModel(this, savedViewModelState);
        return tweetsViewModel;
    }


    @Override
    protected void onPause() {
        super.onPause();
        tweetsViewModel.onPause();
        stopStream();
    }

    @Override
    public void onResume() {
        super.onResume();
        final String last = PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_LAST_SEARCH, null);
        if (last != null && !last.isEmpty()){
            recyclerView.post(() -> {
                searchEditText.setText(last);
                doNotOpenKeyboardOnStart();

                if (ListUtils.isEmpty(tweetsViewModel.getList())){
                    Single<List<BVTweetModel>> single = tweetsViewModel.loadFromDatabase();
                    compositeDisposable.add(single.compose(RxUtils.applySingleOnMainThread())
                            .subscribe( result -> {
                                startStream(last);
                            } ));
                } else {
                    startStream(last);
                }

            });
        }
        tweetsViewModel.startCleaningRoutine(tweetsLock);
    }

    private void clearTweets() {
        synchronized (tweetsLock) {
            tweetsViewModel.clearTweets();
        }
    }

    @OnClick(R.id.start)
    protected void startStream() {
        String text = searchEditText.getText().toString().trim();
        if (text.length() < MIN_TEXT_LENGTH){
            Toast.makeText(this, getString(R.string.error_text_too_short), Toast.LENGTH_LONG).show();
            return;
        }
        if (text.length() > MAX_TEXT_LENGTH){
            Toast.makeText(this, getString(R.string.error_text_too_long), Toast.LENGTH_LONG).show();
            return;
        }

        startStream(text);
    }

    protected void startStream(String text) {
        tweetsViewModel.stopStream();
        stopStream();
        saveLastSearchedKeyword(text);
        Log.d(TAG, "Starting stream. text: "+text);
        tweetsViewModel.showRetry(false);
        Flowable<BVTweetModel> stream = tweetsViewModel.startStream(text);
        disposable = stream.compose(RxUtils.applyFlowableOnMainThread()).subscribe(v -> {
            tweetsViewModel.addTweet(v);
        }, e -> onTweetStreamError(e), () -> onTweetStreamComplete());

    }

    private void onTweetStreamComplete() {
        Log.d(TAG, "Observer onComplete: ");
        synchronized (tweetsLock) {
            tweetsViewModel.addTweet(new BVMessageModel(getString(R.string.stream_closed_message)));
        }
        tweetsViewModel.showRetry(true);
    }

    private void onTweetStreamError(Throwable e) {
        Log.e(TAG, "Observer Error: " + e.getMessage(), e);
        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        synchronized (tweetsLock) {
            tweetsViewModel.addTweet(new BVMessageModel(getString(R.string.stream_forced_to_close_message)));
        }
        tweetsViewModel.showRetry(true);
    }

    private void stopStream() {
        if (observer!= null && !observer.isDisposed()){
            Log.d(TAG, "Stopping stream.");
            observer.onNext(new BVMessageModel(getString(R.string.stream_closed_message)));
            observer.dispose();
        }
        observer = null;

        if (disposable!= null && !disposable.isDisposed()){
            Log.d(TAG, "Stopping stream.");
            disposable.dispose();
        }
        disposable = null;

    }

    private void saveLastSearchedKeyword(String text) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(KEY_LAST_SEARCH, text).apply();
    }

    private void hideKeyboard() {
        InputMethodManager in = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    public boolean isOnline() {
        return NetworkUtils.isOnline(this);
    }

    private void doNotOpenKeyboardOnStart() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}
