package com.github.ymegane.android.twitter.wear.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ymegane.android.twitter.wear.R;
import com.github.ymegane.android.twitter.wear.databinding.ItemTimelineBinding;
import com.github.ymegane.android.twitter.wear.domain.entity.Tweets;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private Tweets tweets;

    public TimelineAdapter(Context context, Tweets tweets) {
        layoutInflater = LayoutInflater.from(context);
        this.tweets = tweets;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemTimelineBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_timeline, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Tweet tweet = tweets.tweets.get(position);
        holder.binding.setTweet(tweet);
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(holder, position);
            }
        });
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return tweets.tweets.size();
    }

    public void addTweets(List<Tweet> tweets) {
        int recentCount = getItemCount();
        this.tweets.addTweets(tweets);
        notifyItemRangeInserted(recentCount-1, tweets.size());
    }

    public Tweet getTweet(int position) {
        return this.tweets.tweets.get(position);
    }

    public void onItemClick(ViewHolder holder, int position) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemTimelineBinding binding;
        public ViewHolder(ItemTimelineBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
