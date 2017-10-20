package com.epicqueststudios.bvtwitter.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epicqueststudios.bvtwitter.R;
import com.epicqueststudios.bvtwitter.base.BaseActivity;
import com.epicqueststudios.bvtwitter.base.viewmodel.AbstractViewModel;
import com.epicqueststudios.bvtwitter.base.viewmodel.BaseViewModel;
import com.epicqueststudios.bvtwitter.databinding.ActivityMainBinding;
import com.epicqueststudios.bvtwitter.feature.twitter.viewmodel.TweetsViewModel;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity{
    private TweetsViewModel tweetsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View root = LayoutInflater.from(this).inflate(R.layout.activity_main, null, false);
        ButterKnife.bind(this, root);
        ActivityMainBinding binding = ActivityMainBinding.bind(root);
        binding.setVm(tweetsViewModel);
    }

    @Nullable
    @Override
    protected BaseViewModel createViewModel(@Nullable BaseViewModel.State savedViewModelState) {
        tweetsViewModel = new TweetsViewModel(this, savedViewModelState);
        return tweetsViewModel;
    }


}
