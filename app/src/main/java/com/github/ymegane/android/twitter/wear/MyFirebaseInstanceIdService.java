package com.github.ymegane.android.twitter.wear;


import com.github.ymegane.android.dlog.DLog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        DLog.d("Refreshed token: " + refreshedToken);
    }
}
