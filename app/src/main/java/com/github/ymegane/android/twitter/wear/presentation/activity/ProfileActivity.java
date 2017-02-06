package com.github.ymegane.android.twitter.wear.presentation.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import com.github.ymegane.android.twitter.wear.R;
import com.github.ymegane.android.twitter.wear.databinding.ActivityProfileBinding;
import com.github.ymegane.android.twitter.wear.presentation.presenter.ProfilePresenter;

public class ProfileActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityProfileBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        new ProfilePresenter(this, binding);
    }
}
