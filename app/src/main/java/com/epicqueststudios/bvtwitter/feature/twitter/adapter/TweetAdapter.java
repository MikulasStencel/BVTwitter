package com.epicqueststudios.bvtwitter.feature.twitter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epicqueststudios.bvtwitter.R;
import com.epicqueststudios.bvtwitter.base.adapter.BaseRecyclerViewAdapter;
import com.epicqueststudios.bvtwitter.databinding.TweetItemWithImageBinding;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;
import com.epicqueststudios.bvtwitter.feature.twitter.viewholder.TweetViewHolder;
import com.epicqueststudios.bvtwitter.feature.twitter.viewmodel.TweetItemViewModel;

import java.util.ArrayList;
import java.util.List;

public class TweetAdapter extends BaseRecyclerViewAdapter<BVTweetModel, TweetItemViewModel> {
    private static final int TYPE_TWEET = 1;
    private static final int TYPE_MESSAGE = 2;
    private static final int MIN_AUTO_SCROLL_OFFSET = 60;

    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TweetItemViewModel viewModel = new TweetItemViewModel();
        View itemView =itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet_item_with_image, parent, false);
        TweetItemWithImageBinding binding = TweetItemWithImageBinding.bind(itemView);
        binding.setViewModel(viewModel);

        return new TweetViewHolder(itemView, binding, viewModel);
    }

    public void setItems(List<BVTweetModel> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void addItem(BVTweetModel newItem) {
        items.add(0,newItem);
        // Do not scroll position if list is on the top
        if (recyclerView!=null && recyclerView.computeVerticalScrollOffset() > MIN_AUTO_SCROLL_OFFSET) {
            notifyItemInserted(0);
        } else {
            notifyDataSetChanged();
        }
    }

    public ArrayList<BVTweetModel> getItems() {
        return items;
    }

}
