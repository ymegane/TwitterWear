package com.github.ymegane.android.twitter.wear.presentation;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.User;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class BindingUtils {

    @BindingAdapter("loadUserIcon")
    public static void loadUserIcon(ImageView imageView, User user) {
        Picasso.with(imageView.getContext())
                .load(Uri.parse(user.profileImageUrlHttps))
                .transform(new CropCircleTransformation())
                .into(imageView);
    }
}
