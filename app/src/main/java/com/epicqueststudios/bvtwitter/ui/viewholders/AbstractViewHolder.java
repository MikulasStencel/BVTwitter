package com.epicqueststudios.bvtwitter.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.epicqueststudios.bvtwitter.model.BVTweet;


public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {

    public AbstractViewHolder(View itemView) {
        super(itemView);
    }
    public abstract void bindView(BVTweet tweet);
}
