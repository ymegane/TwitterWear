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
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TimeLinePresenter implements Presenter {

    private final Context context;
    private final MainActivity activity;
    private final ActivityMainBinding binding;

    private TimelinePresenterEventListener listener;
    private TimelineAdapter adapter;

    private Disposable timelineUpdater;

    private boolean isProcessing = false;

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
        setProcessing(true);

        TwitterApiClient apiClient = TwitterCore.getInstance().getApiClient();
        apiClient.getStatusesService().homeTimeline(30, null, null, false, false, false, false).enqueue(new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                DLog.printMethod();

                Tweets tweets = new Tweets();
                tweets.addTweets(result.data);

                adapter = new TimelineAdapter(context, tweets) {
                    @Override
                    public void onItemClick(TimelineAdapter.ViewHolder holder, int position) {
                        super.onItemClick(holder, position);
                    }
                };
                binding.recyclerTimeline.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                binding.recyclerTimeline.setAdapter(adapter);

                startUpdating();
                setProcessing(true);
            }

            @Override
            public void failure(TwitterException exception) {
                DLog.w(exception);
                setProcessing(false);
            }
        });
    }

    public void updateTimeline(long sinceId) {
        setProcessing(true);
        TwitterApiClient apiClient = TwitterCore.getInstance().getApiClient();
        apiClient.getStatusesService().homeTimeline(30, sinceId, null, false, false, false, false).enqueue(new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                DLog.printMethod();

                adapter.addTweets(result.data);
                setProcessing(false);
            }

            @Override
            public void failure(TwitterException exception) {
                DLog.w(exception);
                setProcessing(false);
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
        setProcessing(true);
        DLog.d("token=" + Twitter.getSessionManager().getActiveSession().getAuthToken().token);
        TwitterApiClient apiClient = TwitterCore.getInstance().getApiClient();
        apiClient.getAccountService().verifyCredentials(true, true).enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                DLog.d("name=" + result.data.name + " screenName=" + result.data.screenName + " id=" + result.data.id);
                listener.onGotUser(result.data);
                setProcessing(false);
            }

            @Override
            public void failure(TwitterException exception) {
                DLog.w(exception);
                Twitter.logOut();
                activity.startActivityForResult(new Intent(context, LoginActivity.class), 100);
                setProcessing(false);
            }
        });
    }

    public void startUpdating() {
        cancelUpdating();

        timelineUpdater = Observable.interval(30, 10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        DLog.printMethod();

                        if (adapter.getItemCount() == 0) {
                            showTimeLine();
                        } else {
                            updateTimeline(adapter.getTweet(0).id);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        DLog.w(throwable);
                    }
                });
    }

    public void cancelUpdating() {
        if (timelineUpdater != null) {
            timelineUpdater.dispose();
        }
        timelineUpdater = null;
    }

    private void setProcessing(boolean processing) {
        isProcessing = processing;
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    public interface TimelinePresenterObserver {
        TimelinePresenterEventListener getTimelinePresenterEventListener();
    }

    public interface TimelinePresenterEventListener {
        void onGotUser(User user);
        void onGotInitialTimeline();
    }
}
