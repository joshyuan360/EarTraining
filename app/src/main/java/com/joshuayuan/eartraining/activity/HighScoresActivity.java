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
package com.joshuayuan.eartraining.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.joshuayuan.eartraining.R;

/**
 * The high scores activity displays the user's high scores for the
 * interval, chords, and cadences activity.
 *
 * @author Joshua Yuan
 */
public class HighScoresActivity extends AppCompatActivity {
    // do not change these; they will affect existing app users
    public static final String HIGH_SCORES_KEY = "high scores";

    public static final String INTERVALS_SCORE_KEY = "ihs";
    public static final String CHORDS_SCORE_KEY = "chhs";
    public static final String CADENCES_SCORE_KEY = "cahs";
    public static final String PROGRESSIONS_SCORE_KEY = "cphs";

    /**
     * Displays the user's high scores for the interval, chords, and cadences activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView intervals = (TextView) findViewById(R.id.inhs);
        TextView chords = (TextView) findViewById(R.id.chhs);
        TextView cadences = (TextView) findViewById(R.id.cahs);
        TextView chordProgressions = (TextView) findViewById(R.id.cphs);

        SharedPreferences highScoresPref = getSharedPreferences(HIGH_SCORES_KEY, Context.MODE_PRIVATE);

        Resources res = getResources();

        intervals.setText(String.format(res.getString(R.string.intervals_hs), highScoresPref.getInt(INTERVALS_SCORE_KEY, 0)));
        chords.setText(String.format(res.getString(R.string.chords_hs), highScoresPref.getInt(CHORDS_SCORE_KEY, 0)));
        cadences.setText(String.format(res.getString(R.string.cadences_hs), highScoresPref.getInt(CADENCES_SCORE_KEY, 0)));
        chordProgressions.setText(String.format(res.getString(R.string.chord_progressions_hs), highScoresPref.getInt(PROGRESSIONS_SCORE_KEY, 0)));
    }
}
