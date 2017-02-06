package com.github.ymegane.android.twitter.wear.presentation.presenter;

import android.content.Context;
import android.widget.Toast;

import com.github.ymegane.android.dlog.DLog;
import com.github.ymegane.android.twitter.wear.databinding.ActivityProfileBinding;
import com.github.ymegane.android.twitter.wear.presentation.activity.ProfileActivity;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.User;

public class ProfilePresenter implements Presenter {
    private final Context context;
    private final ProfileActivity activity;
    private final ActivityProfileBinding binding;

    public ProfilePresenter(ProfileActivity activity, ActivityProfileBinding binding) {
        this.context = activity.getApplicationContext();
        this.activity = activity;
        this.binding = binding;

        initViews();
    }

    private void initViews() {
        getUser();
    }

    private void getUser() {
        TwitterApiClient apiClient = TwitterCore.getInstance().getApiClient();
        apiClient.getAccountService().verifyCredentials(true, true).enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                DLog.d("name=" + result.data.name + " screenName=" + result.data.screenName + " id=" + result.data.id);
                binding.setUser(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                DLog.w(exception);
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        });
    }
}
