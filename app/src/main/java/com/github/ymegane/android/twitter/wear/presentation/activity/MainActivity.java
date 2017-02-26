package com.github.ymegane.android.twitter.wear.presentation.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import com.github.ymegane.android.twitter.wear.R;
import com.github.ymegane.android.twitter.wear.databinding.ActivityMainBinding;
import com.github.ymegane.android.twitter.wear.presentation.presenter.TimeLinePresenter;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.models.User;

public class MainActivity extends WearableActivity implements TimeLinePresenter.TimelinePresenterObserver {

    private TimeLinePresenter presenter;
    private static final int REQUEST_CODE_LOGIN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        presenter = new TimeLinePresenter(this, binding);

        setAmbientEnabled();

        if (Twitter.getSessionManager().getActiveSession() == null) {
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_CODE_LOGIN);
        } else {
            presenter.updateUserInfo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!presenter.isProcessing()) {
            presenter.startUpdating();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.cancelUpdating();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.cancelUpdating();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOGIN) {
            if (resultCode == RESULT_OK) {
                presenter.updateUserInfo();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        presenter.cancelUpdating();
        presenter.updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        presenter.updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        presenter.updateDisplay();
        presenter.startUpdating();
        super.onExitAmbient();
    }

    @Override
    public TimeLinePresenter.TimelinePresenterEventListener getTimelinePresenterEventListener() {
        return new TimeLinePresenter.TimelinePresenterEventListener() {
            @Override
            public void onGotUser(User user) {
                presenter.showTimeLine();
            }

            @Override
            public void onGotInitialTimeline() {
                // do nothing
            }

            @Override
            public void onRequestLogin() {
                startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), REQUEST_CODE_LOGIN);
            }
        };
    }
}
