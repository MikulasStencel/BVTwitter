package com.epicqueststudios.bvtwitter.feature.lifespan;

import android.view.MotionEvent;
import android.view.View;
import com.epicqueststudios.bvtwitter.model.BVTweet;

import static com.epicqueststudios.bvtwitter.feature.lifespan.LifeSpanTweetFactory.TYPE.KEEP_FOREVER;


public class KeepOnTouchLifeSpan extends TimeLifeSpan implements View.OnTouchListener {

    boolean bTrackerDown = false;

    public KeepOnTouchLifeSpan(long duration){
        super(duration);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getActionMasked();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                bTrackerDown = true;
                break;
            case MotionEvent.ACTION_MOVE:
                bTrackerDown = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                bTrackerDown = false;
                break;
        }
        return false;
    }

    @Override
    public boolean isExpired(BVTweet tweet, long now) {
        long ts = tweet.getTimeStamp();
        return (ts > 0 && now - ts < duration) && !bTrackerDown;
    }

    @Override
    public int getID() {
        return KEEP_FOREVER.ordinal();
    }

}
