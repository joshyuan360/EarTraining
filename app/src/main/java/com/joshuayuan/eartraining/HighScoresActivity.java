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
package com.joshuayuan.eartraining;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * The high scores activity displays the user's high scores for the
 * interval, chords, and cadences activity.
 * @author Joshua Yuan
 */
public class HighScoresActivity extends AppCompatActivity {
    /**
     * Displays the user's high scores for the interval, chords, and cadences activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView intervals = (TextView)findViewById(R.id.inhs);
        TextView chords = (TextView)findViewById(R.id.chhs);
        TextView cadences = (TextView)findViewById(R.id.cahs);
        TextView chordProgressions = (TextView)findViewById(R.id.cphs);

        String intervalsScore = getSharedPreferences("high scores", Context.MODE_PRIVATE).getInt("ihs", 0) + "";
        String chordsScore = getSharedPreferences("high scores", Context.MODE_PRIVATE).getInt("chhs", 0) + "";
        String cadencesScore = getSharedPreferences("high scores", Context.MODE_PRIVATE).getInt("cahs", 0) + "";
        String chordProgressionsScore = getSharedPreferences("high scores", Context.MODE_PRIVATE).getInt("cphs", 0) + "";

        intervals.setText ("Intervals: " + intervalsScore);
        chords.setText ("Chords: " + chordsScore);
        cadences.setText ("Cadences: " + cadencesScore);
        chordProgressions.setText("Progressions: " + chordProgressionsScore);
    }
}
