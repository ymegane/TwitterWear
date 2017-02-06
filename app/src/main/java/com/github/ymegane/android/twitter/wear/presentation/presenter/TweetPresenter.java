package com.github.ymegane.android.twitter.wear.presentation.presenter;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ymegane.android.dlog.DLog;
import com.github.ymegane.android.twitter.wear.databinding.ActivityTweetBinding;
import com.github.ymegane.android.twitter.wear.presentation.activity.TweetActivity;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

public class TweetPresenter implements Presenter {

    private final Context context;
    private final TweetActivity activity;
    private final ActivityTweetBinding binding;

    public TweetPresenter(TweetActivity activity, ActivityTweetBinding binding) {
        this.context = activity.getApplicationContext();
        this.activity = activity;
        this.binding = binding;

        initViews();
    }

    private void initViews() {
        binding.editTweet.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String text = v.getText().toString();
                    postUpdate(text);
                    return true;
                }
                return false;
            }
        });
    }

    private void postUpdate(String message) {
        TwitterApiClient apiClient = TwitterCore.getInstance().getApiClient();
        apiClient.getStatusesService().update(message, null, true, null, null, null, false, false, null).enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                activity.setResult(Activity.RESULT_OK);
                activity.finish();
            }

            @Override
            public void failure(TwitterException exception) {
                DLog.w(exception);
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
