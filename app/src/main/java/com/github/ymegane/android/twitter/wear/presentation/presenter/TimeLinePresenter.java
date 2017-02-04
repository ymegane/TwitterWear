package com.github.ymegane.android.twitter.wear.presentation.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.wearable.view.DefaultOffsettingHelper;

import com.github.ymegane.android.dlog.DLog;
import com.github.ymegane.android.twitter.wear.presentation.activity.LoginActivity;
import com.github.ymegane.android.twitter.wear.presentation.activity.MainActivity;
import com.github.ymegane.android.twitter.wear.databinding.ActivityMainBinding;
import com.github.ymegane.android.twitter.wear.domain.entity.Tweets;
import com.github.ymegane.android.twitter.wear.widget.TimelineAdapter;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

import java.util.List;

public class TimeLinePresenter implements Presenter {

    private final Context context;
    private final MainActivity activity;
    private final ActivityMainBinding binding;

    private TimelinePresenterEventListener listener;

    public TimeLinePresenter(MainActivity activity, ActivityMainBinding binding) {
        this.activity = activity;
        this.binding = binding;
        this.context = activity.getApplicationContext();

        this.listener = activity.getTimelinePresenterEventListener();

        initViews();
    }

    private void initViews() {
        binding.recyclerTimeline.setOffsettingHelper(new DefaultOffsettingHelper());
    }

    public void showTimeLine() {
        TwitterApiClient apiClient = TwitterCore.getInstance().getApiClient();
        apiClient.getStatusesService().homeTimeline(30, null, null, false, false, false, false).enqueue(new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                DLog.printMethod();

                Tweets tweets = new Tweets();
                tweets.addTweets(result.data);
                binding.setTweets(tweets);

                TimelineAdapter adapter = new TimelineAdapter(context, tweets) {
                    @Override
                    public void onItemClick(TimelineAdapter.ViewHolder holder, int position) {
                        super.onItemClick(holder, position);
                    }
                };
                binding.recyclerTimeline.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                binding.recyclerTimeline.setAdapter(adapter);
            }

            @Override
            public void failure(TwitterException exception) {
                DLog.w(exception);
            }
        });
    }

    public void updateDisplay() {
        Resources resources = context.getResources();
        if (activity.isAmbient()) {
            binding.container.setBackgroundColor(resources.getColor(android.R.color.black));
        } else {
            binding.container.setBackground(null);
        }
    }

    public void updateUserInfo() {
        DLog.d("token=" + Twitter.getSessionManager().getActiveSession().getAuthToken().token);
        TwitterApiClient apiClient = TwitterCore.getInstance().getApiClient();
        apiClient.getAccountService().verifyCredentials(true, true).enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                DLog.d("name=" + result.data.name + " screenName=" + result.data.screenName + " id=" + result.data.id);
                listener.onGotUser(result.data);
                showTimeLine();
            }

            @Override
            public void failure(TwitterException exception) {
                DLog.w(exception);
                Twitter.logOut();
                activity.startActivityForResult(new Intent(context, LoginActivity.class), 100);
            }
        });
    }

    public interface TimelinePresenterObserver {
        TimelinePresenterEventListener getTimelinePresenterEventListener();
    }

    public interface TimelinePresenterEventListener {
        void onGotUser(User user);
    }
}
