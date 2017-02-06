package com.github.ymegane.android.twitter.wear.presentation.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import com.github.ymegane.android.twitter.wear.BuildConfig;
import com.github.ymegane.android.twitter.wear.R;
import com.github.ymegane.android.twitter.wear.databinding.ActivityAboutBinding;

public class AboutActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAboutBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_about);
        binding.textVersion.setText(BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE+")");
    }
}
