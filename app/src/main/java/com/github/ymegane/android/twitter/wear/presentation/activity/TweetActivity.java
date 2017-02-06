package com.github.ymegane.android.twitter.wear.presentation.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import com.github.ymegane.android.twitter.wear.R;
import com.github.ymegane.android.twitter.wear.databinding.ActivityTweetBinding;
import com.github.ymegane.android.twitter.wear.presentation.presenter.TweetPresenter;

public class TweetActivity extends WearableActivity {

    private TweetPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);

        ActivityTweetBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_tweet);
        presenter = new TweetPresenter(this, binding);
    }

}
