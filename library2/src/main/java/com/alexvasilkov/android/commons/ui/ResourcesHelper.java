package com.alexvasilkov.android.commons.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;

@SuppressWarnings("unused") // Public API
public class ResourcesHelper {

    private static final int[] tmpAttrs = new int[1];

    private ResourcesHelper() {}

    public static Drawable getAttrDrawable(@NonNull Context context, @AttrRes int attr) {
        tmpAttrs[0] = attr;
        TypedArray arr = context.obtainStyledAttributes(null, tmpAttrs);
        Drawable drawable = arr.getDrawable(0);
        arr.recycle();
        return drawable;
    }

    public static int getAttrColor(@NonNull Context context, @AttrRes int attr) {
        tmpAttrs[0] = attr;
        TypedArray arr = context.obtainStyledAttributes(null, tmpAttrs);
        int color = arr.getColor(0, 0);
        arr.recycle();
        return color;
    }

    @SuppressWarnings("deprecation")
    public static int getColor(@NonNull Context context, @ColorRes int colorResId) {
        if (Build.VERSION.SDK_INT < 23) {
            return context.getResources().getColor(colorResId);
        } else {
            return context.getColor(colorResId);
        }
    }

}
