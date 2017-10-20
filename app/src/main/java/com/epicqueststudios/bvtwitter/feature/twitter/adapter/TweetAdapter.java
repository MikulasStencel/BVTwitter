package com.epicqueststudios.bvtwitter.feature.twitter.adapter;

import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epicqueststudios.bvtwitter.R;
import com.epicqueststudios.bvtwitter.base.adapter.BaseRecyclerViewAdapter;
import com.epicqueststudios.bvtwitter.databinding.TweetItemWithImageBinding;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;
import com.epicqueststudios.bvtwitter.feature.twitter.viewmodel.TweetsItemViewModel;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class TweetAdapter extends BaseRecyclerViewAdapter<BVTweetModel, TweetsItemViewModel> {

    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tweet_list_item_with_image, parent, false);

        TweetsItemViewModel viewModel = new TweetsItemViewModel();

        TweetItemWithImageBinding binding = TweetItemWithImageBinding.bind(itemView);
        binding.setViewModel(viewModel);

        return new TweetViewHolder(itemView, binding, viewModel);
    }

    public void setItems(ArrayList<BVTweetModel> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public ArrayList<BVTweetModel> getItems() {
        return items;
    }

    static class TweetViewHolder extends BaseViewHolder<BVTweetModel, TweetsItemViewModel> {

        public TweetViewHolder(View itemView, ViewDataBinding binding, TweetsItemViewModel viewModel) {
            super(itemView, binding, viewModel);
            ButterKnife.bind(this, itemView);
        }

    }



}
