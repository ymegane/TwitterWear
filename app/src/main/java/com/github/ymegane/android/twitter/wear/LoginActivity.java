package com.github.ymegane.android.twitter.wear;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.authentication.OAuthClient;
import android.view.View;
import android.widget.Toast;

import com.github.ymegane.android.dlog.DLog;
import com.github.ymegane.android.twitter.wear.databinding.ActivityLoginBinding;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

public class LoginActivity extends WearableActivity {

    private ActivityLoginBinding binding;

    private OAuthClient oauthClient;
    private static final Uri LOGIN_URL = Uri.parse(BuildConfig.OAUTH_ENDPOINT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        setAmbientEnabled();
        setResult(RESULT_CANCELED);

        oauthClient = OAuthClient.create(this);

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oauthClient.sendAuthorizationRequest(LOGIN_URL, new MyOAuthCallback());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (oauthClient != null) {
            oauthClient.destroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DLog.printMethod();
    }

    private class MyOAuthCallback extends OAuthClient.Callback {

        @Override
        public void onAuthorizationResponse(Uri requestUrl, Uri responseUrl) {
            String token = responseUrl.getQueryParameter("token");
            String secret = responseUrl.getQueryParameter("secret");

            TwitterAuthToken authToken = new TwitterAuthToken(token, secret);
            TwitterSession session = new TwitterSession(authToken, 0, null);
            Twitter.getSessionManager().setActiveSession(session);

            TwitterApiClient apiClient = TwitterCore.getInstance().getApiClient();
            apiClient.getAccountService().verifyCredentials(true, true).enqueue(new Callback<User>() {
                @Override
                public void success(Result<User> result) {
                    DLog.d("name=" + result.data.name + "screenName=" + result.data.screenName + " id=" + result.data.id);
                    Toast.makeText(getApplicationContext(), result.data.screenName, Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void failure(TwitterException exception) {
                    DLog.w(exception);
                    Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        @Override
        public void onAuthorizationError(int i) {
            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
