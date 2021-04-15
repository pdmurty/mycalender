package com.pdmurty.mycalender;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Parcelable;

import androidx.annotation.AttrRes;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AppUtils {
    private static final String ABBR_YEAR = "y";
    private static final String ABBR_WEEK = "w";
    private static final String ABBR_DAY = "d";
    private static final String ABBR_HOUR = "h";
    private static final String ABBR_MINUTE = "m";
    private static final String PLAY_STORE_URL = "market://details?id=" + BuildConfig.APPLICATION_ID;
    private static final String FORMAT_HTML_COLOR = "%06X";
    public static final int HOT_THRESHOLD_HIGH = 300;
    public static final int HOT_THRESHOLD_NORMAL = 100;
    static final int HOT_THRESHOLD_LOW = 10;
    public static final int HOT_FACTOR = 3;
    private static final String HOST_ITEM = "item";
    private static final String HOST_USER = "user";

    public static int getThemedResId(Context context, @AttrRes int attr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        final int resId = a.getResourceId(0, 0);
        a.recycle();
        return resId;
    }

}
