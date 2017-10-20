package com.epicqueststudios.bvtwitter.feature.twitter.viewmodel;


import android.databinding.Bindable;
import com.epicqueststudios.bvtwitter.base.viewmodel.BaseViewModel;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;


public class TweetsItemViewModel extends BaseViewModel<BVTweetModel> {
    private BVTweetModel tweet;

    public TweetsItemViewModel() {
        super(null);
    }
    @Override
    public void setItem(BVTweetModel item) {
        tweet = item;
        notifyChange();
    }


    @Bindable
    public String getVersion() {
        return "todo";
    }

}