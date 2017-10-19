package com.epicqueststudios.bvtwitter.feature.lifespan;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;

import static com.epicqueststudios.bvtwitter.feature.lifespan.LifeSpanTweetFactory.TYPE.KEEP_FOREVER;


public class KeepOnTouchLifeSpan extends TimeLifeSpan implements View.OnTouchListener, Parcelable {

    boolean bTrackerDown = false;

    public KeepOnTouchLifeSpan(){
        super();
    }

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
    public boolean isExpired(BVTweetModel tweet, long now) {
        long ts = tweet.getTimeStamp();
        return (ts > 0 && now - ts < duration) && !bTrackerDown;
    }

    @Override
    public int getID() {
        return KEEP_FOREVER.ordinal();
    }


    protected KeepOnTouchLifeSpan(Parcel in) {
        bTrackerDown = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (bTrackerDown ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<KeepOnTouchLifeSpan> CREATOR = new Parcelable.Creator<KeepOnTouchLifeSpan>() {
        @Override
        public KeepOnTouchLifeSpan createFromParcel(Parcel in) {
            return new KeepOnTouchLifeSpan(in);
        }

        @Override
        public KeepOnTouchLifeSpan[] newArray(int size) {
            return new KeepOnTouchLifeSpan[size];
        }
    };
}