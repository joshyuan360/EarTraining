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
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.joshuayuan.eartraining.intelliyuan.Syllabus;
import com.joshuayuan.eartraining.R;

/**
 * The high scores activity displays the user's high scores for the
 * interval, chords, and cadences activity.
 *
 * @author Joshua Yuan
 */
public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }

        @Override
        public void onResume() {
            super.onResume();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            // Unregister the listener whenever a key changes
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        public static final String PREF_LEVEL = "pref_level";

        public static final String PREF_INTERVALS = "pref_intervals";
        public static final String PREF_INTERVALS_ADVANCED = "pref_intervals_advanced";
        public static final String PREF_CHORDS = "pref_chords";
        public static final String PREF_CADENCES = "pref_cadences";
        public static final String PREF_CHORD_PROGRESSIONS = "pref_chord_progressions";
        public static final String PREF_PROGRESSION_TONALITY = "pref_progression_tonality";
        public static final String PREF_SEQ_LENGTH = "pref_seq_length";

        public static final String PREF_REPEAT = "pref_repeat";

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {
            if (key.equals(PREF_LEVEL)) {
                Context context = getActivity().getApplicationContext();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

                SharedPreferences.Editor editor = prefs.edit();

                int level = Integer.parseInt(prefs.getString(PREF_LEVEL, "10"));

                editor.putStringSet(PREF_INTERVALS, Syllabus.getIntervalsFromLevel(level));
                editor.putString(PREF_INTERVALS_ADVANCED, Syllabus.getIntervalTypeFromLevel(level));
                editor.putStringSet(PREF_CHORDS, Syllabus.getChordsFromLevel(level));
                editor.putStringSet(PREF_CADENCES, Syllabus.getCadencesFromLevel(level));
                editor.putStringSet(PREF_CHORD_PROGRESSIONS, Syllabus.getProgressionsFromLevel(level));
                editor.putString(PREF_PROGRESSION_TONALITY, Syllabus.getProgressionTonalityFromLevel(level));
                editor.putString(PREF_SEQ_LENGTH, Syllabus.getProgressionLengthFromLevel(level));

                editor.apply();
            }
        }
    }
}
