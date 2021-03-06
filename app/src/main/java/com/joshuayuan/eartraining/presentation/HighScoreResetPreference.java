/*
 * Copyright (C) 2016 Joshua Yuan
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.joshuayuan.eartraining.presentation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * A <code>DialogPreference</code> that changes value of
 * <code>NoteMappings.resetHighScores</code> after a positive response.
 */
public class HighScoreResetPreference extends DialogPreference {
    public HighScoreResetPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            SharedPreferences prefs = getContext().getSharedPreferences("high scores", Context.MODE_PRIVATE);
            prefs.edit().putInt("ihs", 0).putInt("chhs", 0).putInt("cahs", 0).putInt("cphs", 0).apply();
        }
    }
}
