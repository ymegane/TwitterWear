package com.github.ymegane.android.twitter.wear.presentation.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.wearable.view.drawer.WearableNavigationDrawer;
import android.util.SparseArray;

import com.github.ymegane.android.twitter.wear.R;

public class MainNavigationDrawerAdapter extends WearableNavigationDrawer.WearableNavigationDrawerAdapter {

    private Context context;
    private SparseArray<Item> items;

    public MainNavigationDrawerAdapter(Context context) {
        this.context = context;
        items = new SparseArray<>(2);
        items.append(0, new Item("Profile", R.drawable.ic_person_black_24dp));
        items.append(1, new Item("About", R.drawable.ic_mood_24dp));
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
                break;
            case 1:
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
