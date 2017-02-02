package com.github.ymegane.android.twitter.wear;

import android.app.Application;

import com.github.ymegane.android.dlog.DLog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DLog.init(this);

        TwitterAuthConfig authConfig =  new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        FirebaseAnalytics.getInstance(this).setUserProperty("device", "wear");
    }
}
