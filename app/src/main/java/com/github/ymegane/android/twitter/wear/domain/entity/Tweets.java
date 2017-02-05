package com.github.ymegane.android.twitter.wear.domain.entity;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

public class Tweets extends BaseObservable {

    public Tweets() {
        this.tweets = new ObservableArrayList<>();
    }

    public ObservableArrayList<Tweet> tweets;

    public void addTweets(List<Tweet> tweets) {
        this.tweets.addAll(0, tweets);
    }
}
