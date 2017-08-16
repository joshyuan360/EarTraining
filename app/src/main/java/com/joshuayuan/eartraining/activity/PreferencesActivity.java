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

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {
            if (key.equals(getString(R.string.PREF_LEVEL))) {
                customizeUserSettings();
            } else if (key.equals(getString(R.string.PREF_PRESET))) {
                boolean isRequested = getPreferenceManager().getSharedPreferences().getBoolean(getString(R.string.PREF_PRESET), false);
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

            int level = Integer.parseInt(prefs.getString(getString(R.string.PREF_LEVEL), "10"));

            editor.putStringSet(getString(R.string.PREF_INTERVALS), PianoSyllabus.getIntervalsFromLevel(level));
            editor.putString(getString(R.string.PREF_INTERVALS_ADVANCED), PianoSyllabus.getIntervalTypeFromLevel(level));
            editor.putStringSet(getString(R.string.PREF_CHORDS), PianoSyllabus.getChordsFromLevel(level));
            editor.putString(getString(R.string.PREF_CHORDS_ADVANCED), PianoSyllabus.getChordTypeFromLevel(level));
            editor.putStringSet(getString(R.string.PREF_CADENCES), PianoSyllabus.getCadencesFromLevel(level));
            editor.putStringSet(getString(R.string.PREF_CHORD_PROGRESSIONS), PianoSyllabus.getProgressionsFromLevel(level));
            editor.putString(getString(R.string.PREF_PROGRESSION_TONALITY), PianoSyllabus.getProgressionTonalityFromLevel(level));
            editor.putString(getString(R.string.PREF_SEQ_LENGTH), PianoSyllabus.getProgressionLengthFromLevel(level));

            editor.apply();
        }

        private void setPresetRequested() {
            boolean isRequested = getPreferenceManager().getSharedPreferences().getBoolean(getString(R.string.PREF_PRESET), false);
            PreferenceScreen preferenceScreen = getPreferenceScreen();

            preferenceScreen.findPreference(getString(R.string.PREF_LEVEL)).setEnabled(isRequested);

            preferenceScreen.findPreference(getString(R.string.PREF_INTERVALS)).setEnabled(!isRequested);
            preferenceScreen.findPreference(getString(R.string.PREF_INTERVALS_ADVANCED)).setEnabled(!isRequested);
            preferenceScreen.findPreference(getString(R.string.PREF_CHORDS)).setEnabled(!isRequested);
            preferenceScreen.findPreference(getString(R.string.PREF_CHORDS_ADVANCED)).setEnabled(!isRequested);
            preferenceScreen.findPreference(getString(R.string.PREF_CADENCES)).setEnabled(!isRequested);
            preferenceScreen.findPreference(getString(R.string.PREF_CHORD_PROGRESSIONS)).setEnabled(!isRequested);
            preferenceScreen.findPreference(getString(R.string.PREF_PROGRESSION_TONALITY)).setEnabled(!isRequested);
            preferenceScreen.findPreference(getString(R.string.PREF_SEQ_LENGTH)).setEnabled(!isRequested);
        }
    }
}
