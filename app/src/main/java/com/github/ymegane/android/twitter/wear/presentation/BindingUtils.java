package com.github.ymegane.android.twitter.wear.presentation;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.widget.ImageView;

import com.github.ymegane.android.twitter.wear.R;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.User;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.CropTransformation;

public class BindingUtils {

    @BindingAdapter("loadUserIcon")
    public static void loadUserIcon(ImageView imageView, User user) {
        if (user == null) {
            return;
        }
        Picasso.with(imageView.getContext())
                .load(Uri.parse(user.profileImageUrlHttps))
                .transform(new CropCircleTransformation())
                .into(imageView);
    }

    @BindingAdapter("loadUserBackground")
    public static void loadUserBackground(ImageView imageView, User user) {
        if (user == null) {
            return;
        }

        if (user.profileUseBackgroundImage) {
            Picasso.with(imageView.getContext())
                    .load(Uri.parse(user.profileBackgroundImageUrlHttps))
                    .transform(new CropTransformation(0, 0, imageView.getWidth(), imageView.getHeight()))
                    .into(imageView);
        }
    }
}
