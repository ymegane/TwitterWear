package com.github.ymegane.android.twitter.wear.presentation.presenter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.wearable.view.DefaultOffsettingHelper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ymegane.android.dlog.DLog;
import com.github.ymegane.android.twitter.wear.presentation.activity.MainActivity;
import com.github.ymegane.android.twitter.wear.databinding.ActivityMainBinding;
import com.github.ymegane.android.twitter.wear.domain.entity.Tweets;
import com.github.ymegane.android.twitter.wear.presentation.widget.MainNavigationDrawerAdapter;
import com.github.ymegane.android.twitter.wear.presentation.widget.TimelineAdapter;
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
        binding.topNavigationDrawer.setAdapter(new MainNavigationDrawerAdapter(activity));

        binding.recyclerTimeline.setOffsettingHelper(new DefaultOffsettingHelper());
        binding.bottomDrawer.setShouldPeekOnScrollDown(true);
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
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.editTweet.setText("");
                binding.bottomDrawer.closeDrawer();
            }
        });
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

                    @Override
                    public void onItemAdded(int oldPosition) {
                    }
                };
                binding.recyclerTimeline.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                binding.recyclerTimeline.setAdapter(adapter);

                startUpdating();
                setProcessing(false);

                listener.onGotInitialTimeline();
            }

            @Override
            public void failure(TwitterException exception) {
                DLog.w(exception);
                setProcessing(false);
                binding.progress.setVisibility(View.GONE);
            }
        });
    }

    public void updateTimeline() {
        if (adapter.getItemCount() == 0) {
            showTimeLine();
        } else {
            updateTimeline(adapter.getTweet(0).id);
        }
    }

    private void updateTimeline(long sinceId) {
        setProcessing(true);
        TwitterApiClient apiClient = TwitterCore.getInstance().getApiClient();
        apiClient.getStatusesService().homeTimeline(50, sinceId, null, false, false, false, false).enqueue(new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                DLog.d("result count=" + result.data.size());

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

    private void postUpdate(String message) {
        TwitterApiClient apiClient = TwitterCore.getInstance().getApiClient();
        apiClient.getStatusesService().update(message, null, true, null, null, null, false, false, null).enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                binding.editTweet.setText("");
                binding.editTweet.clearFocus();
                binding.bottomDrawer.closeDrawer();
                updateTimeline();
            }

            @Override
            public void failure(TwitterException exception) {
                DLog.w(exception);
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateDisplay() {
        if (activity.isAmbient()) {
            binding.drawerLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.black));
        } else {
            binding.drawerLayout.setBackground(null);
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

                listener.onRequestLogin();
                setProcessing(false);
            }
        });
    }

    public void startUpdating() {
        DLog.printMethod();
        cancelUpdating();

        timelineUpdater = Observable.interval(3, 3, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        DLog.printMethod();
                        updateTimeline();

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

        if (processing) {
            binding.progress.setVisibility(View.VISIBLE);
        } else {
            binding.progress.setVisibility(View.GONE);
        }
    }

    public void setSelectedDrawerItem(int position) {
        binding.topNavigationDrawer.setCurrentItem(position, false);
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

        void onRequestLogin();
    }
}
