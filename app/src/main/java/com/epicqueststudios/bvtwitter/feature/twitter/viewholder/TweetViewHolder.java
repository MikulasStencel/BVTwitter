package com.epicqueststudios.bvtwitter.feature.twitter.viewholder;


import android.databinding.ViewDataBinding;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.epicqueststudios.bvtwitter.R;
import com.epicqueststudios.bvtwitter.base.adapter.BaseRecyclerViewAdapter;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;
import com.epicqueststudios.bvtwitter.feature.twitter.viewmodel.TweetItemViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder<BVTweetModel, TweetItemViewModel> {

    @BindView(R.id.tv_tweet)
    TextView text;

    @BindView(R.id.tv_tweet_title)
    TextView title;

    @BindView(R.id.iv_profile)
    ImageView image;

    public TweetViewHolder(View itemView, ViewDataBinding binding, TweetItemViewModel viewModel) {
        super(itemView, binding, viewModel);
        ButterKnife.bind(this, itemView);
    }
}
