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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        presenter = new TimeLinePresenter(this, binding);

        setAmbientEnabled();

        if (Twitter.getSessionManager().getActiveSession() == null) {
            startActivityForResult(new Intent(this, LoginActivity.class), 100);
        } else {
            presenter.updateUserInfo();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                // todo load twitter timeline
                presenter.updateUserInfo();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
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
        super.onExitAmbient();
    }

    @Override
    public TimeLinePresenter.TimelinePresenterEventListener getTimelinePresenterEventListener() {
        return new TimeLinePresenter.TimelinePresenterEventListener() {
            @Override
            public void onGotUser(User user) {
                // do nothing
            }
        };
    }
}
