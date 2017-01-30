package com.github.ymegane.android.twitter.wear;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import com.github.ymegane.android.dlog.DLog;
import com.github.ymegane.android.twitter.wear.databinding.ActivityLoginBinding;

public class LoginActivity extends WearableActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        setAmbientEnabled();
        setResult(RESULT_CANCELED);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DLog.printMethod();
    }
}
