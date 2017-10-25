package com.epicqueststudios.bvtwitter.feature.twitter.viewmodel;


import android.app.Activity;
import android.content.Context;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.epicqueststudios.bvtwitter.R;
import com.epicqueststudios.bvtwitter.base.adapter.BaseRecyclerViewAdapter;
import com.epicqueststudios.bvtwitter.base.viewmodel.RecyclerViewViewModel;
import com.epicqueststudios.bvtwitter.feature.lifespan.CleaningRoutine;
import com.epicqueststudios.bvtwitter.feature.sqlite.DatabaseHandler;
import com.epicqueststudios.bvtwitter.feature.twitter.BasicTwitterClient;
import com.epicqueststudios.bvtwitter.feature.twitter.adapter.TweetAdapter;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVMessageModel;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;
import com.epicqueststudios.bvtwitter.interfaces.ActivityInterface;
import com.epicqueststudios.bvtwitter.interfaces.StorageInterface;
import com.epicqueststudios.bvtwitter.ui.activities.MainActivity;
import com.epicqueststudios.bvtwitter.utils.ListUtils;
import com.epicqueststudios.bvtwitter.utils.RxUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Component;
import dagger.android.AndroidInjection;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

@BindingMethods({
        @BindingMethod(type = EditText.class, attribute = "android:onEditorAction", method = "setOnEditorActionListener")
})
public class TweetsViewModel extends RecyclerViewViewModel<TweetsViewModel> implements StorageInterface {
    private static final String TAG = TweetsViewModel.class.getSimpleName();
    private static final int MIN_TEXT_LENGTH = 2;
    private static final int MAX_TEXT_LENGTH = 50;
    public static final String KEY_LAST_SEARCH = "prefs_last_search";
    private final Context appContext;

    private final BasicTwitterClient twitterClient;

    ArrayList<BVTweetModel> tweets;
    TweetAdapter adapter;
    private CleaningRoutine cleaningRoutine = null;
    private DatabaseHandler databaseHandler;

    private PublishSubject eventSubject = PublishSubject.create();

    private DisposableObserver<List<BVTweetModel>> cleanObserver;
    private final ObservableBoolean isRetryVisible = new ObservableBoolean(false);
    public ObservableField<String> searchText = new ObservableField<>();
    private Disposable disposable;
    private Object tweetsLock = new Object();
    private CompositeDisposable compositeDisposable;

    @Inject
    public TweetsViewModel(Context context, @Nullable State savedInstanceState, BasicTwitterClient twitterClient) {
        super(savedInstanceState);
        appContext = context.getApplicationContext();

        compositeDisposable = new CompositeDisposable();

        if (savedInstanceState instanceof TweetsState) {
            tweets = ((TweetsState) savedInstanceState).tweets;
        } else {
            tweets = new ArrayList<>();
        }
        adapter = new TweetAdapter();
        adapter.setItems(tweets);
        this.twitterClient = new BasicTwitterClient(appContext);
      //  this.twitterClient = twitterClient;
        databaseHandler = new DatabaseHandler(context);
        cleaningRoutine = new CleaningRoutine(this, (ActivityInterface)context, databaseHandler);
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
        super.setItem(item);
    }

    public void setItems(List<BVTweetModel> tweets) {
        this.tweets.clear();
        this.tweets.addAll(tweets);
        adapter.setItems(this.tweets);
    }

    public void addTweet(BVTweetModel bvTweet) {
        this.tweets.add(0, bvTweet);
        adapter.addItem(bvTweet);
    }

    public PublishSubject<UIEvent> getEvents(){
        return eventSubject;
    }

    @Override
    public List<BVTweetModel> getList() {
        return this.tweets;
    }

    public void startCleaningRoutine(final Object lock) {
        cleanObserver = cleaningRoutine.startProcess()
                .subscribeWith(new DisposableObserver<List<BVTweetModel>>() {
            @Override
            public void onNext(List<BVTweetModel> expiredTweets) {
                deleteTweets(expiredTweets, lock);
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

    private void deleteTweets(List<BVTweetModel> expired, Object lock) {
        synchronized (lock){
            /*for (BVTweetModel model: expired){
                removeIfExists(model);
            }*/
            this.tweets.removeAll(expired);
        }
    }

    private void removeIfExists(BVTweetModel model) {
        if (this.tweets.contains(model)){
            this.tweets.remove(model);
        }
       /* for (int i = 0; i < this.tweets.size(); i++) {
            if (this.tweets.get(i).getId() == model.getId()){
                this.tweets.remove(tweets.get(i));
                break;
            }
        }*/
    }

    public Flowable<BVTweetModel> startStream(String text) {
        Flowable<BVTweetModel> stream = twitterClient.getStream(text);
        stream = stream.doOnNext(bvTweetModel -> {
            if (bvTweetModel.useStorage())
                storeTweet(bvTweetModel);
        });
        return stream;
    }


    public void clearTweets() {
        databaseHandler.clearDB();
        setItems(new ArrayList<>());
    }


    public Single<List<BVTweetModel>> loadFromDatabase() {
        return Single.fromCallable(() -> databaseHandler.getAllTweets())
                .doOnSubscribe(v -> setIsLoading(true))
                .doOnSuccess(result -> {
                    setIsLoading(false);
                }
        );
    }

    public void onPause() {
        stopTwitterStream();
        if (cleanObserver != null && !cleanObserver.isDisposed()) {
            Log.d(TAG, "cleaning routine has been disposed");
            cleanObserver.dispose();
            cleanObserver = null;
        }
        if (disposable!= null && !disposable.isDisposed()){
            Log.d(TAG, "Stopping stream.");
            disposable.dispose();
        }
        disposable = null;
    }

    public void showRetry(boolean bShow) {
        this.isRetryVisible.set(bShow);
        this.isRetryVisible.notifyChange();
    }

    public void onRetryClick(View view){
        String text = searchText.get().trim();

        if (text.length() < MIN_TEXT_LENGTH){
            Toast.makeText(view.getContext(), view.getResources().getString(R.string.error_text_too_short), Toast.LENGTH_LONG).show();
            return;
        }
        if (text.length() > MAX_TEXT_LENGTH){
            Toast.makeText(view.getContext(), view.getResources().getString(R.string.error_text_too_long), Toast.LENGTH_LONG).show();
            return;
        }
        startTwitterStream(text);
    }

    public void onDeleteTweets(View view) {
        Snackbar.make(view, "Clear all tweets", Snackbar.LENGTH_LONG)
                .setAction("Clear", view1 -> clearTweets()).show();
    }

    public void startTwitterStream(String text) {
        stopTwitterStream();
        saveLastSearchedKeyword(text);
        Log.d(TAG, "Starting stream. text: "+text);
        showRetry(false);
        Flowable<BVTweetModel> stream = startStream(text);
        disposable = stream.compose(RxUtils.applyFlowableOnMainThread()).subscribe(v -> {
            addTweet(v);
        }, e -> onTweetStreamError(e), () -> onTweetStreamComplete());

    }

    private void onTweetStreamComplete() {
        Log.d(TAG, "Observer onComplete: ");
        eventSubject.onNext(new UIEvent(UIEvent.TYPE.ADD_TWEET, R.string.stream_closed_message));
        showRetry(true);
    }

    private void onTweetStreamError(Throwable e) {
        Log.e(TAG, "Observer Error: " + e.getMessage(), e);
        eventSubject.onNext(new UIEvent(UIEvent.TYPE.ON_ERROR, e));
        showRetry(true);
    }

    private void saveLastSearchedKeyword(String text) {
        PreferenceManager.getDefaultSharedPreferences(appContext).edit().putString(KEY_LAST_SEARCH, text).apply();
    }

    private void stopTwitterStream() {
        twitterClient.stopStream();

        if (disposable!= null && !disposable.isDisposed()){
            Log.d(TAG, "Stopping stream.");
            //observer.onNext(new BVMessageModel(getString(R.string.stream_closed_message)));
            disposable.dispose();
        }
        disposable = null;

    }

    public ObservableBoolean getIsRetryVisible() {
        return isRetryVisible;
    }


    public void storeTweet(BVTweetModel tweet) {
        databaseHandler.storeTweet(tweet);
    }


    public void onDestroy(){
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
    }

    public void onResume(String last) {
        if (last != null && !last.isEmpty()){
            searchText.set(last);
            if (ListUtils.isEmpty(getList())){
                Single<List<BVTweetModel>> single = loadFromDatabase();
                compositeDisposable.add(single.compose(RxUtils.applySingleOnMainThread())
                        .subscribe( result -> {
                            Log.d(TAG, "Data was loaded from db: "+result.size()+" items");
                            setItems(result);
                            startTwitterStream(last);
                        } ));
            } else {
                startTwitterStream(last);
            }
        }
        startCleaningRoutine(tweetsLock);
    }

    public boolean onEditorAction(@NonNull final TextView textView, final int actionId, @Nullable final KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            onRetryClick(null); //TODO
            textView.clearFocus();
            hideKeyboard(textView);
            return true;
        }
        return false;
    }

    private void hideKeyboard(View view) {
        InputMethodManager in = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private static class TweetsState extends RecyclerViewViewModelState {

        private final ArrayList<BVTweetModel> tweets;

        public TweetsState(TweetsViewModel viewModel) {
            super(viewModel);
            tweets = viewModel.adapter.getItems();
        }

        public TweetsState(Parcel in) {
            super(in);
            tweets = in.createTypedArrayList(BVTweetModel.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeTypedList(tweets);
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