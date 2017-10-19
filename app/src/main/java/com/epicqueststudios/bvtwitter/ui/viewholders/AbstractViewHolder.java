package com.epicqueststudios.bvtwitter.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;


public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {

    public AbstractViewHolder(View itemView) {
        super(itemView);
    }
    public abstract void bindView(BVTweetModel tweet);
}
