package com.epicqueststudios.bvtwitter.feature.twitter.viewmodel;


import android.databinding.Bindable;
import android.databinding.ObservableBoolean;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.epicqueststudios.bvtwitter.R;
import com.epicqueststudios.bvtwitter.base.viewmodel.BaseViewModel;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;

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
        String url = item.getImageUrl();
       /* if (url != null && !url.isEmpty()){
            Glide.with(item.getContext())
                    .load(url)
                    .into(image);
        } else {
            image.setImageResource(0);
        }*/
        notifyChange();
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

    @Bindable
    public String getVersion() {
        return "todo";
    }

}