package com.example.mycalender;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class ThemeView extends CardView{
    public ThemeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ThemeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.theme_view, this, true);
        TypedArray ta = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.theme});
        ContextThemeWrapper wrapper = new ContextThemeWrapper(context, ta.getResourceId(0, R.style.AppTheme));
        ta.recycle();
        int cardBackgroundColor = AppUtils.getThemedResId(wrapper, R.attr.colorCardBackground);
        int textColor = AppUtils.getThemedResId(wrapper, android.R.attr.textColorTertiary);
        setCardBackgroundColor(ContextCompat.getColor(wrapper, cardBackgroundColor));
        ((TextView) findViewById(R.id.content)).setTextColor(ContextCompat.getColor(wrapper, textColor));
    }

}
