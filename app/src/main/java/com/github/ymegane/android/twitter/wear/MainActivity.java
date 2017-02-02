package com.github.ymegane.android.twitter.wear;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Toast;

import com.github.ymegane.android.dlog.DLog;
import com.github.ymegane.android.twitter.wear.databinding.ActivityMainBinding;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

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

        if (Twitter.getSessionManager().getActiveSession() == null) {
            startActivityForResult(new Intent(this, LoginActivity.class), 100);
        } else {
            updateUserInfo();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                // todo load twitter timeline
                updateUserInfo();
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

    private void updateUserInfo() {
        DLog.d("token=" + Twitter.getSessionManager().getActiveSession().getAuthToken().token);
        TwitterApiClient apiClient = TwitterCore.getInstance().getApiClient();
        apiClient.getAccountService().verifyCredentials(true, true).enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                DLog.d("name=" + result.data.name + "screenName=" + result.data.screenName + " id=" + result.data.id);
                binding.text.setText(result.data.screenName);
            }

            @Override
            public void failure(TwitterException exception) {
                DLog.w(exception);
                Twitter.logOut();
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 100);
            }
        });
    }
}
