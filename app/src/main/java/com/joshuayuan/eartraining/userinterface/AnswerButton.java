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
package com.joshuayuan.eartraining.userinterface;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.joshuayuan.eartraining.R;

/**
 * A <code>Button</code> with font Gill Sans MT and text size 12.
 */
public class AnswerButton extends Button {
    public AnswerButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/GIL.TTF"));
        setTextSize(13);
        setTextColor(Color.parseColor("#FFFFFF"));
        setBackgroundResource(R.drawable.answer_button);
        setEnabled(false);
    }
}
