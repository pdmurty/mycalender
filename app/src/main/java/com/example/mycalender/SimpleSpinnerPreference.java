package com.example.mycalender;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SimpleSpinnerPreference extends SpinnerPreference{
    private final LayoutInflater mLayoutInflater;

    @SuppressWarnings("unused")
    public SimpleSpinnerPreference(Context context, AttributeSet attrs) {
      this(context, attrs, R.attr.preferenceStyle);
    }

    public SimpleSpinnerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLayoutInflater = LayoutInflater.from(getContext());
    }


    protected View createDropDownView(int position, ViewGroup parent) {
        return mLayoutInflater.inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);

    }


    protected void bindDropDownView(int position, View view) {
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(mEntries[position]);
    }

}
