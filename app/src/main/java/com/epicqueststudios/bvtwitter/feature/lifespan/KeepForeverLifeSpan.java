package com.epicqueststudios.bvtwitter.feature.lifespan;

import android.os.Parcel;
import android.os.Parcelable;

import com.epicqueststudios.bvtwitter.interfaces.LifeSpanTweetInterface;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;

import static com.epicqueststudios.bvtwitter.feature.lifespan.LifeSpanTweetFactory.TYPE.KEEP_FOREVER;

public class KeepForeverLifeSpan implements LifeSpanTweetInterface, Parcelable {
    @Override
    public boolean isExpired(BVTweetModel tweet, long now) {
        return false;
    }

    @Override
    public int getID() {
        return KEEP_FOREVER.ordinal();
    }

    protected KeepForeverLifeSpan() {

    }
    protected KeepForeverLifeSpan(Parcel in) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<KeepForeverLifeSpan> CREATOR = new Parcelable.Creator<KeepForeverLifeSpan>() {
        @Override
        public KeepForeverLifeSpan createFromParcel(Parcel in) {
            return new KeepForeverLifeSpan(in);
        }

        @Override
        public KeepForeverLifeSpan[] newArray(int size) {
            return new KeepForeverLifeSpan[size];
        }
    };
}