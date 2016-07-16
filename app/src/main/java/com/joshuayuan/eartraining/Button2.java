/*
 * Copyright (C) 2016 Joshua Yuan
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.joshuayuan.eartraining;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * A <code>Button</code> with font Gill Sans MT and text size 14.
 */
public class Button2 extends Button {
    public Button2(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/GIL.TTF"));
        setTextSize(14);
        setTextColor(Color.parseColor("#FFFFFF"));
        setBackgroundResource(R.drawable.custom_button);
        setEnabled(true);
    }
}