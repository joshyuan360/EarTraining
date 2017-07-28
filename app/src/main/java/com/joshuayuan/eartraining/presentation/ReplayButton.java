package com.joshuayuan.eartraining.presentation;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.joshuayuan.eartraining.R;

/**
 * Created by toli on 7/28/2017.
 */
public class ReplayButton extends Button {
    public ReplayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.replay_button);
        setEnabled(true);
    }
}