package com.epicqueststudios.bvtwitter.ui.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.epicqueststudios.bvtwitter.R;
import com.epicqueststudios.bvtwitter.model.BVTweet;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TweetViewHolder extends AbstractViewHolder{
    @BindView(R.id.tv_tweet)
    TextView text;

    @BindView(R.id.tv_tweet_title)
    TextView title;

    @BindView(R.id.iv_profile)
    ImageView image;

    public TweetViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bindView(BVTweet tweet) {
        title.setText(tweet.getTweetTitle());
        text.setText(tweet.getMessage());
        String url = tweet.getImageUrl();
        if (url != null && !url.isEmpty()){
            Glide.with(image.getContext())
                    .load(url)
                    .into(image);
        } else {
            image.setImageResource(0);
        }
    }
}
