package com.github.ymegane.android.twitter.wear;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;

import com.github.ymegane.android.twitter.wear.databinding.ActivityMainBinding;
import com.twitter.sdk.android.core.TwitterSession;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setAmbientEnabled();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                // todo load twitter timeline
            } else {
                finish();
            }
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            binding.container.setBackgroundColor(getResources().getColor(android.R.color.black));
            binding.text.setTextColor(getResources().getColor(android.R.color.white));
            binding.clock.setVisibility(View.VISIBLE);

            binding.clock.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            binding.container.setBackground(null);
            binding.text.setTextColor(getResources().getColor(android.R.color.black));
            binding.clock.setVisibility(View.GONE);
        }
    }
}
