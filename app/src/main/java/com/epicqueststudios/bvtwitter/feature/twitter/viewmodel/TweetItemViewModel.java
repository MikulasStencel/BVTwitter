package com.epicqueststudios.bvtwitter.feature.twitter.viewmodel;


import android.content.Context;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.epicqueststudios.bvtwitter.R;
import com.epicqueststudios.bvtwitter.base.viewmodel.BaseViewModel;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;
import com.epicqueststudios.bvtwitter.utils.ListUtils;

import butterknife.BindView;


public class TweetItemViewModel extends BaseViewModel<BVTweetModel> {

    @BindView(R.id.iv_profile)
    ImageView image;

    private ObservableBoolean isLoading = new ObservableBoolean(false);

    public TweetItemViewModel() {
        super(null);
    }

    @Override
    public void setItem(BVTweetModel item) {
        super.setItem(item);
    }

    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String url) {

        if (ListUtils.isEmpty(url)) {
            imageView.setImageResource(0);
            imageView.invalidate();
            return;
        }
        Context context = imageView.getContext();
        Glide.with(context).load(url).into(imageView);
        imageView.invalidate();
    }

    public String getImageUrl(){
        return item.getImageUrl();
    }
    public void setIsLoading(boolean isLoading) {
        this.isLoading.set(isLoading);
    }
    public ObservableBoolean getIsLoading() {
        return isLoading;
    }

    public String getTitle(){
        return item.getTweetTitle();
    }
    public String getMessage(){
        return item.getMessage();
    }

}