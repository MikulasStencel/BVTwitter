package com.epicqueststudios.bvtwitter.feature.lifespan;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.util.Log;

import com.epicqueststudios.bvtwitter.interfaces.LifeSpanTweetInterface;
import com.epicqueststudios.bvtwitter.model.BVTweet;

import static com.epicqueststudios.bvtwitter.feature.lifespan.LifeSpanTweetFactory.TYPE.BASIC_TIME;
import static com.epicqueststudios.bvtwitter.feature.lifespan.LifeSpanTweetFactory.TYPE.KEEP_ON_NO_NETWORK;


public class KeepOnNoNetwork extends BroadcastReceiver implements LifeSpanTweetInterface, Application.ActivityLifecycleCallbacks {
    private static final String TAG = KeepOnNoNetwork.class.getSimpleName();
    private static KeepOnNoNetwork instance;
    private final Activity context;
    boolean bConnected = false;

    private KeepOnNoNetwork(Activity context){
        this.context = context;
        refreshNetworkStatus();
    }

    public synchronized static KeepOnNoNetwork getInstance(Activity context){
        if (instance == null){
            instance = new KeepOnNoNetwork(context);
            instance.registerNetworkBroadcast();
        }
        return instance;
    }

    private void registerNetworkBroadcast() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.context.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.context.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        context.getApplication().registerActivityLifecycleCallbacks(this);
    }

    private void refreshNetworkStatus() {
        bConnected = getConnectivityStatus(context) != TYPE.NOT_CONNECTED;
    }

    @Override
    public boolean isExpired(BVTweet tweet, long now) {
        return !bConnected;
    }

    public enum TYPE {WIFI, MOBILE, NOT_CONNECTED}
    @Override
    public void onReceive(final Context context, final Intent intent) {
        refreshNetworkStatus();
    }

    public static TYPE getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE.WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE.MOBILE;
        }
        return TYPE.NOT_CONNECTED;
    }

    protected void unregisterNetwork() {
        try {
            context.unregisterReceiver(this);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public int getID() {
        return KEEP_ON_NO_NETWORK.ordinal();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        registerNetworkBroadcast();
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        unregisterNetwork();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }


}
