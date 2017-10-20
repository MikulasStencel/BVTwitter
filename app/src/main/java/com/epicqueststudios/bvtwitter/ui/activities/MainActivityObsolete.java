package com.epicqueststudios.bvtwitter.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
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
import com.epicqueststudios.bvtwitter.feature.twitter.adapter.TweetAdapterObsolete;
import com.epicqueststudios.bvtwitter.databinding.ActivityMainBinding;
import com.epicqueststudios.bvtwitter.feature.lifespan.CleaningRoutine;
import com.epicqueststudios.bvtwitter.feature.twitter.BasicTwitterClient;
import com.epicqueststudios.bvtwitter.feature.sqlite.DatabaseHandler;
import com.epicqueststudios.bvtwitter.feature.twitter.viewmodel.TweetsItemViewModel;
import com.epicqueststudios.bvtwitter.feature.twitter.viewmodel.TweetsViewModel;
import com.epicqueststudios.bvtwitter.interfaces.ActivityInterface;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVMessageModel;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;
import com.epicqueststudios.bvtwitter.ui.widgets.WrapContentLinearLayoutManager;
import com.epicqueststudios.bvtwitter.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivityObsolete extends RxActivity implements ActivityInterface {
    private static final String TAG = MainActivityObsolete.class.getSimpleName();
    private static final int MIN_TEXT_LENGTH = 2;
    private static final int MAX_TEXT_LENGTH = 50;
    private static final int MIN_AUTO_SCROLL_OFFSET = 60;
    private static final String KEY_LAST_SEARCH = "prefs_last_search";
    private BasicTwitterClient twitterClient;
    private List<BVTweetModel> tweets = new ArrayList<>();

    private TweetsItemViewModel viewModel;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.start)
    Button buttonStart;

    @BindView(R.id.edit_text_search)
    EditText searchEditText;

    @BindView(R.id.fab)
    FloatingActionButton actionButton;

    private TweetAdapterObsolete adapter;
    private DisposableObserver<BVTweetModel> observer;
    private DatabaseHandler databaseHandler;
    private CleaningRoutine cleaningRoutine = null;
    private Object tweetsLock = new Object();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*viewModel = ViewModelProviders.of(this).get(TweetsViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setVm(viewModel);*/

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        twitterClient = new BasicTwitterClient(getApplicationContext());

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new TweetAdapterObsolete(tweets);
        recyclerView.setAdapter(adapter);

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                startStream();
                searchEditText.clearFocus();
                InputMethodManager in = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
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

        databaseHandler = new DatabaseHandler(this);
        cleaningRoutine = new CleaningRoutine(tweetsLock, this, databaseHandler);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopStream();
        cleaningRoutine.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Observable<List<BVTweetModel>> observer = cleaningRoutine.startProcess();
        observer.subscribeWith(new DisposableObserver<List<BVTweetModel>>() {
            @Override
            public void onNext(List<BVTweetModel> otweets) {
                updateTweets(otweets);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage(), e);
            }

            @Override
            public void onComplete() {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        final String last = PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_LAST_SEARCH, null);
        if (last != null && !last.isEmpty()){
            recyclerView.post(() -> {
                viewModel.setIsLoading(true);
                searchEditText.setText(last);
                doNotOpenKeyboardOnStart();

                Observable.fromCallable(() -> databaseHandler.getAllTweets(this))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( result -> {
                            viewModel.setIsLoading(false);
                            updateTweets(result);
                            startStream(last);
                        } );

            });
        }
    }

    private void doNotOpenKeyboardOnStart() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void updateTweets(List<BVTweetModel> result) {
        synchronized (tweetsLock) {
            tweets = result;
            adapter.setTweets(tweets);
        }
        adapter.notifyDataSetChanged();
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

    private void clearTweets() {
        databaseHandler.clearDB();
        updateTweets(new ArrayList<>());
    }

    protected void startStream(String text) {
        stopStream();
        saveLastSearchedKeyword(text);
        Log.d(TAG, "Starting stream. text: "+text);
        buttonStart.setVisibility(View.GONE);
        Observable<BVTweetModel> stream = twitterClient.getStream(text);

        observer = stream.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribeWith(new DisposableObserver<BVTweetModel>() {
            @Override
            public void onNext(BVTweetModel bvTweet) {
                Log.d(TAG, "Observer onSuccess: " + bvTweet.getMessage());
                synchronized (tweetsLock) {
                    tweets.add(0, bvTweet);
                    adapter.setTweets(tweets);
                }
                notifyAdapter();
                if (bvTweet.useStorage())
                    databaseHandler.storeTweet(bvTweet);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Observer Error: " + e.getMessage(), e);
                Toast.makeText(MainActivityObsolete.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                synchronized (tweetsLock) {
                    tweets.add(0, new BVMessageModel(getString(R.string.stream_forced_to_close_message)));
                    adapter.setTweets(tweets);
                }
                notifyAdapter();
                buttonStart.setVisibility(View.VISIBLE);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Observer onComplete: ");
                synchronized (tweetsLock) {
                    tweets.add(0, new BVMessageModel(getString(R.string.stream_closed_message)));
                    adapter.setTweets(tweets);
                }
                notifyAdapter();
                buttonStart.setVisibility(View.VISIBLE);
            }
        });
    }

    private void saveLastSearchedKeyword(String text) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(KEY_LAST_SEARCH, text).apply();
    }

    private void notifyAdapter() {
        // Do not scroll position if list is on the top
        if (recyclerView.computeVerticalScrollOffset() > MIN_AUTO_SCROLL_OFFSET) {
            adapter.notifyItemInserted(0);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void stopStream() {
        twitterClient.stopStream();
        if (observer!= null && !observer.isDisposed()){
            Log.d(TAG, "Stopping stream.");
            observer.onNext(new BVMessageModel(getString(R.string.stream_closed_message)));
            observer.dispose();
        }
        observer = null;
    }

    @Override
    public List<BVTweetModel> getTweets() {
        return tweets;
    }

    @Override
    public boolean isOnline() {
        return (!NetworkUtils.isOnline(this));
    }
}
