package com.epicqueststudios.bvtwitter.feature.sqlite;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.epicqueststudios.bvtwitter.feature.lifespan.LifeSpanTweetFactory;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHandler.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bv_database";
    private static final String TABLE_TWEETS = "tweets";
    private static final String KEY_ID = "id";
    private static final String KEY_RAW_TEXT = "raw_text";
    private static final String KEY_LIFESPAN_TYPE = "lifespan_type";
    private PublishSubject<DBMessage> dbEventBus = PublishSubject.create();

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DatabaseManager.initializeInstance(this);
        dbEventBus.observeOn(Schedulers.computation()).subscribeOn(Schedulers.computation()).subscribe(event -> {
            onEvent(event);
        });
    }

    private void onEvent(DBMessage event) {
        switch (event.eventType){
            case ADD:
                addTweet(event.tweet);
                break;
            case DELETE:
                deleteTweet(event.tweet);
                break;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TWEETS_TABLE = "CREATE TABLE " + TABLE_TWEETS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_RAW_TEXT + " TEXT," + KEY_LIFESPAN_TYPE + " INT" + ")";
        db.execSQL(CREATE_TWEETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TWEETS);
        onCreate(db);
    }

    public void addTweet(BVTweetModel tweet) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase(true);
        ContentValues values = new ContentValues();
        values.put(KEY_ID, tweet.getId());
        values.put(KEY_RAW_TEXT, tweet.getRaw());
        values.put(KEY_LIFESPAN_TYPE, tweet.getLifeSpanType());
        db.insert(TABLE_TWEETS, null, values);
        DatabaseManager.getInstance().closeDatabase();
    }

    public List<BVTweetModel> getAllTweets(Activity context) {
        List<BVTweetModel> tweetList = new ArrayList<BVTweetModel>();
        String selectQuery = "SELECT  * FROM " + TABLE_TWEETS;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase(false);
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {

            if (cursor.moveToFirst()) {
                do {
                    BVTweetModel tweet = new BVTweetModel(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RAW_TEXT)));
                    // example of usage of KeepOnNoNetwork class
                    // tweet.setId(Integer.parseInt(cursor.getString(0)));
                    //tweet.setLifeSpan(KeepOnNoNetwork.getInstance(context));

                    tweet.setLifeSpan(LifeSpanTweetFactory.createLifeSpanByType(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_LIFESPAN_TYPE)), context, LifeSpanTweetFactory.DEFAULT_EXPIRE));
                    tweetList.add(tweet);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            DatabaseManager.getInstance().closeDatabase();
        }
        return tweetList;
    }

    public void deleteTweet(BVTweetModel tweet) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase(true);
        db.delete(TABLE_TWEETS, KEY_ID + " = ?",
                new String[] { String.valueOf(tweet.getId()) });
        DatabaseManager.getInstance().closeDatabase();
    }

    public void clearDB() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase(true);
        db.delete(TABLE_TWEETS, null, null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public void storeTweet(BVTweetModel tweet) {
        dbEventBus.onNext(new DBMessage(tweet, DBMessage.BVEvent.ADD));
    }
    public void removeTweet(BVTweetModel tweet) {
        dbEventBus.onNext(new DBMessage(tweet, DBMessage.BVEvent.DELETE));
    }

}
