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
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.joshuayuan.eartraining.syllabus.PianoSyllabus;
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
        setTitle("Settings");

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            setPresetRequested();
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
        public static final String PREF_PRESET = "pref_preset";

        public static final String PREF_INTERVALS = "pref_intervals";
        public static final String PREF_INTERVALS_ADVANCED = "pref_interval_type";
        public static final String PREF_CHORDS = "pref_chords";
        public static final String PREF_CHORDS_ADVANCED = "pref_chord_type";
        public static final String PREF_CADENCES = "pref_cadences";
        public static final String PREF_CHORD_PROGRESSIONS = "pref_chord_progressions";
        public static final String PREF_PROGRESSION_TONALITY = "pref_progression_tonality";
        public static final String PREF_SEQ_LENGTH = "pref_seq_length";

        public static final String PREF_REPEAT = "pref_repeat";

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {
            if (key.equals(PREF_LEVEL)) {
                customizeUserSettings();
            } else if (key.equals(PREF_PRESET)) {
                boolean isRequested = getPreferenceManager().getSharedPreferences().getBoolean(PREF_PRESET, false);
                if (isRequested) {
                    customizeUserSettings();
                } else {
                    setPreferenceScreen(null);
                    addPreferencesFromResource(R.xml.settings);
                }
                setPresetRequested();
            }
        }

        private void customizeUserSettings() {
            Context context = getActivity().getApplicationContext();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

            SharedPreferences.Editor editor = prefs.edit();

            int level = Integer.parseInt(prefs.getString(PREF_LEVEL, "10"));

            editor.putStringSet(PREF_INTERVALS, PianoSyllabus.getIntervalsFromLevel(level));
            editor.putString(PREF_INTERVALS_ADVANCED, PianoSyllabus.getIntervalTypeFromLevel(level));
            editor.putStringSet(PREF_CHORDS, PianoSyllabus.getChordsFromLevel(level));
            editor.putString(PREF_CHORDS_ADVANCED, PianoSyllabus.getChordTypeFromLevel(level));
            editor.putStringSet(PREF_CADENCES, PianoSyllabus.getCadencesFromLevel(level));
            editor.putStringSet(PREF_CHORD_PROGRESSIONS, PianoSyllabus.getProgressionsFromLevel(level));
            editor.putString(PREF_PROGRESSION_TONALITY, PianoSyllabus.getProgressionTonalityFromLevel(level));
            editor.putString(PREF_SEQ_LENGTH, PianoSyllabus.getProgressionLengthFromLevel(level));

            editor.apply();
        }

        private void setPresetRequested() {
            boolean isRequested = getPreferenceManager().getSharedPreferences().getBoolean(PREF_PRESET, false);
            PreferenceScreen preferenceScreen = getPreferenceScreen();

            preferenceScreen.findPreference(PREF_LEVEL).setEnabled(isRequested);

            preferenceScreen.findPreference(PREF_INTERVALS).setEnabled(!isRequested);
            preferenceScreen.findPreference(PREF_INTERVALS_ADVANCED).setEnabled(!isRequested);
            preferenceScreen.findPreference(PREF_CHORDS).setEnabled(!isRequested);
            preferenceScreen.findPreference(PREF_CHORDS_ADVANCED).setEnabled(!isRequested);
            preferenceScreen.findPreference(PREF_CADENCES).setEnabled(!isRequested);
            preferenceScreen.findPreference(PREF_CHORD_PROGRESSIONS).setEnabled(!isRequested);
            preferenceScreen.findPreference(PREF_PROGRESSION_TONALITY).setEnabled(!isRequested);
            preferenceScreen.findPreference(PREF_SEQ_LENGTH).setEnabled(!isRequested);
        }
    }
}