package com.epicqueststudios.bvtwitter.ui.activities;

import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.Toast;

import com.epicqueststudios.bvtwitter.BVTwitterApplication;
import com.epicqueststudios.bvtwitter.R;
import com.epicqueststudios.bvtwitter.base.BaseActivity;
import com.epicqueststudios.bvtwitter.base.viewmodel.BaseViewModel;
import com.epicqueststudios.bvtwitter.databinding.ActivityMainBinding;
import com.epicqueststudios.bvtwitter.di.ActivityDependency;
import com.epicqueststudios.bvtwitter.di.AppDependency;
import com.epicqueststudios.bvtwitter.feature.lifespan.CleaningRoutine;
import com.epicqueststudios.bvtwitter.feature.sqlite.DatabaseHandler;
import com.epicqueststudios.bvtwitter.feature.twitter.BasicTwitterClient;
import com.epicqueststudios.bvtwitter.feature.twitter.adapter.TweetAdapter;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVMessageModel;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;
import com.epicqueststudios.bvtwitter.feature.twitter.viewmodel.TweetsViewModel;
import com.epicqueststudios.bvtwitter.feature.twitter.viewmodel.UIEvent;
import com.epicqueststudios.bvtwitter.interfaces.ActivityInterface;
import com.epicqueststudios.bvtwitter.utils.ListUtils;
import com.epicqueststudios.bvtwitter.utils.NetworkUtils;
import com.epicqueststudios.bvtwitter.utils.RxUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.OkHttpClient;

import static com.epicqueststudios.bvtwitter.feature.twitter.viewmodel.TweetsViewModel.KEY_LAST_SEARCH;

public class MainActivity extends BaseActivity implements ActivityInterface, HasFragmentInjector {
    @Inject
    AppDependency appDependency; // same object from App

    @Inject
    ActivityDependency activityDependency;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;


    BVTwitterApplication app;
    //@Inject
    BasicTwitterClient twitterClient;
    @Inject
    OkHttpClient okHttpClient;


    private static final String TAG = MainActivity.class.getSimpleName();
    private TweetsViewModel tweetsViewModel;
    private DisposableObserver<BVTweetModel> observer;
    private Object tweetsLock = new Object();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.edit_text_search)
    EditText searchEditText;

    @Override
    public final AndroidInjector<android.app.Fragment> fragmentInjector() {
        return fragmentInjector;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setViewModel(tweetsViewModel);
        tweetsViewModel.getEvents().subscribe(event -> onUIEvent(event));
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

      /*  searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                tweetsViewModel.onRetryClick(null);
                searchEditText.clearFocus();
                hideKeyboard();
                return true;
            }
            return false;
        });*/
    }

    private void onUIEvent(UIEvent event) {
        Log.d(TAG, "event: "+event.toString());
        switch (event.getType()){
            case ADD_TWEET:
                synchronized (tweetsLock) {
                    tweetsViewModel.addTweet(new BVMessageModel(getString((int) event.getData())));
                }
                break;
            case ON_ERROR:
                Toast.makeText(MainActivity.this, R.string.stream_forced_to_close_message, Toast.LENGTH_SHORT).show();
                synchronized (tweetsLock) {
                    tweetsViewModel.addTweet(new BVMessageModel(((Exception) event.getData()).getMessage()));
                }
                break;
            case SAVE_LAST_SEARCHED_QUERY:
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString(KEY_LAST_SEARCH, (String) event.getData()).apply();
        }
    }

    @Nullable
    @Override
    protected BaseViewModel createViewModel(@Nullable BaseViewModel.State savedViewModelState) {
        twitterClient = new BasicTwitterClient(this.getApplicationContext());
        DatabaseHandler databaseHandler = new DatabaseHandler(this.getApplicationContext());
        TweetAdapter adapter = new TweetAdapter();
        tweetsViewModel = new TweetsViewModel(savedViewModelState, twitterClient, databaseHandler, new LinearLayoutManager(this.getApplicationContext()), adapter);
        CleaningRoutine cleaningRoutine = new CleaningRoutine(tweetsViewModel, (ActivityInterface) this, databaseHandler);
        tweetsViewModel.setCleaningRoutine(cleaningRoutine);
        return tweetsViewModel;
    }

    @Override
    protected void onPause() {
        super.onPause();
        tweetsViewModel.onPause();
    }

    @Override
    protected void onDestroy() {
        if (tweetsViewModel != null) {
            tweetsViewModel.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        final String last = PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_LAST_SEARCH, null);
        if (!ListUtils.isEmpty(last)) {
            doNotOpenKeyboardOnStart();
        }
        tweetsViewModel.onResume(last);
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
