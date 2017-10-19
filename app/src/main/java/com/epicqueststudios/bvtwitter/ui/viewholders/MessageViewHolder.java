package com.epicqueststudios.bvtwitter.ui.viewholders;

import android.view.View;
import android.widget.TextView;

import com.epicqueststudios.bvtwitter.R;
import com.epicqueststudios.bvtwitter.feature.twitter.model.BVTweetModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageViewHolder extends AbstractViewHolder{
    @BindView(R.id.tv_tweet)
    TextView text;

    public MessageViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bindView(BVTweetModel tweet) {
        text.setText(tweet.getMessage());
    }
}
