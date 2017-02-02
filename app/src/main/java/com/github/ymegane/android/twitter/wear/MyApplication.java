package com.github.ymegane.android.twitter.wear;

import android.app.Application;

import com.github.ymegane.android.dlog.DLog;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DLog.init(this);

        FirebaseAnalytics.getInstance(this).setUserProperty("device", "wear");
    }
}
