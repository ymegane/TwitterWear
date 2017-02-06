package com.github.ymegane.android.twitter.wear.presentation.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.wearable.view.drawer.WearableNavigationDrawer;
import android.util.SparseArray;

import com.github.ymegane.android.twitter.wear.R;
import com.github.ymegane.android.twitter.wear.presentation.activity.MainActivity;
import com.github.ymegane.android.twitter.wear.presentation.activity.ProfileActivity;

public class MainNavigationDrawerAdapter extends WearableNavigationDrawer.WearableNavigationDrawerAdapter {

    private Context context;
    private MainActivity activity;
    private SparseArray<Item> items;

    public MainNavigationDrawerAdapter(MainActivity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        items = new SparseArray<>(2);
        items.append(0, new Item("Timeline", R.drawable.ic_android_24dp));
        items.append(1, new Item("Profile", R.drawable.ic_person_black_24dp));
        items.append(2, new Item("About", R.drawable.ic_mood_24dp));
    }

    @Override
    public String getItemText(int i) {
        return items.get(i).text;
    }

    @Override
    public Drawable getItemDrawable(int i) {
        return ContextCompat.getDrawable(context, items.get(i).iconRes);
    }

    @Override
    public void onItemSelected(int i) {
        switch (i) {
            case 0:
                activity.startActivity(new Intent(activity, MainActivity.class));
                break;
            case 1:
                activity.startActivity(new Intent(activity, ProfileActivity.class));
                break;
            default:
                break;
        }
    }


    @Override
    public int getCount() {
        return items.size();
    }


    private static class Item {
        private String text;
        @DrawableRes
        private int iconRes;

        public Item(String text, int iconRes) {
            this.text = text;
            this.iconRes = iconRes;
        }
    }
}
