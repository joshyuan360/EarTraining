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
package com.joshuayuan.eartraining.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.joshuayuan.eartraining.intelliyuan.CadenceGenerator;
import com.joshuayuan.eartraining.intelliyuan.ChordExtensions;
import com.joshuayuan.eartraining.intelliyuan.NoteMappings;
import com.joshuayuan.eartraining.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static com.joshuayuan.eartraining.activity.PreferenceKeys.CADENCES_SCORE_KEY;
import static com.joshuayuan.eartraining.activity.PreferenceKeys.HIGH_SCORES_KEY;
import static com.joshuayuan.eartraining.activity.PreferencesActivity.SettingsFragment.PREF_CADENCES;
import static com.joshuayuan.eartraining.activity.PreferencesActivity.SettingsFragment.PREF_REPEAT;

/**
 * The cadences activity plays a cadence and asks the user to identify it.
 * The score is based on the number of consecutive correct answers.
 *
 * @author Joshua Yuan
 */
public class CadencesActivity extends AppCompatActivity { //todo: does it switch between minor and major? check this
    private final MediaPlayer[] mp = new MediaPlayer[8];

    private Button perfect, plagal, imperfect, deceptive;
    private Button replay;

    private CharSequence answer;
    private CharSequence response;

    private boolean answerCorrect = true;
    private TextView tv;
    private int notes[];
    private int score;

    private TextView currentScore, highScore;
    private MediaPlayer tonic = new MediaPlayer();
    private Set<String> selections;
    private boolean prefRepeat;

    private Handler handler = new Handler();
    private boolean isReplaying;
    private int randomShift;
    private SharedPreferences pref;

    private AudioManager audioManager;
    private SeekBar volumeSeekbar, speedSeekbar;

    /**
     * Initializes the <code>Button</code> fields and begins the test.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Cadences");
        setContentView(R.layout.activity_cadences);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tv = (TextView) findViewById(R.id.cadenceText);
        currentScore = (TextView) findViewById(R.id.cadenceScore);
        highScore = (TextView) findViewById(R.id.cadenceHighestScore);

        loadPreference();

        initializeButtons();
        setButtonsEnabled(false);
        replay.setEnabled(false);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                testUser();
            }
        }, 1000);

    }

    private void loadPreference() {
        pref = getSharedPreferences(HIGH_SCORES_KEY, Context.MODE_PRIVATE);
        highScore.setText(String.valueOf(pref.getInt(CADENCES_SCORE_KEY, 0)));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> defaultSet = new HashSet(Arrays.asList(new String[]{"Imperfect", "Deceptive"})); //TODO change this (6.3)
        selections = sharedPrefs.getStringSet(PREF_CADENCES, defaultSet);
        prefRepeat = sharedPrefs.getBoolean(PREF_REPEAT, true);
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

    /**
     * Stops any currently playing sounds when the user exits the activity.
     *
     * @throws IllegalStateException if the internal player engine has not been
     *                               initialized or has been released.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        for (int i = 0; i < 8; i++) {
            if (mp[i] != null) {
                mp[i].release();
                mp[i] = null;
            }
        }

        if (tonic != null) {
            tonic.release();
            tonic = null;
        }
    }

    /**
     * Generates a new random cadence and stores it in <code>answer</code>.
     * Chances: 25% perfect, 25% plagal, 25% imperfect, 25% deceptive.
     * Method is invoked only when the last answer provided is correct.
     */
    private void setAnswer() {
        String[] primaryKey = new String[] { "Perfect", "Plagal", "Imperfect", "Deceptive" };

        Random random = new Random();
        answer = primaryKey[random.nextInt(primaryKey.length)];

        if (!answer.equals("Perfect") && !answer.equals("Plagal") && !selections.contains(answer)) {
            setAnswer();
        }
    }

    /**
     * Loads all the button fields.
     */
    private void initializeButtons() {
        perfect = (Button) findViewById(R.id.perfect);
        plagal = (Button) findViewById(R.id.plagal);
        imperfect = (Button) findViewById(R.id.imperfect);
        deceptive = (Button) findViewById(R.id.deceptive);
        replay = (Button) findViewById(R.id.replay);
    }

    /**
     * Enables or disables the answer buttons.
     *
     * @param enabled Controls the perfect, plagal, imperfect, and deceptive buttons.
     */
    private void setButtonsEnabled(boolean enabled) {
        perfect.setEnabled(enabled);
        plagal.setEnabled(enabled);

        imperfect.setEnabled(enabled && selections.contains("Imperfect"));
        deceptive.setEnabled(enabled && selections.contains("Deceptive"));
    }

    /**
     * Displays the result of the user's input as "Correct!" or "Try Again!".
     * The score is either incremented (if correct) or reset to zero (if incorrect).
     */
    private void displayResult() {
        if (response.equals(answer)) {
            tv.setText(getResources().getString(R.string.correct));
            answerCorrect = true;
            score++;
        } else {
            tv.setText(getResources().getString(R.string.incorrect));
            answerCorrect = false;
            score = 0;
        }
        currentScore.setText(String.valueOf(score));
        setHighScores(score);
    }

    /**
     * If the current score is higher than the high score, the new high score is updated
     * in shared preferences.
     *
     * @param score The current score.
     */
    private void setHighScores(int score) {
        int hs = pref.getInt(CADENCES_SCORE_KEY, 0);

        if (hs < score) {
            hs = score;

            SharedPreferences.Editor editor = pref.edit();

            editor.putInt(CADENCES_SCORE_KEY, hs);
            editor.apply();
        }

        highScore.setText(String.valueOf(hs));
    }

    /**
     * If the last answer was correct, this method plays a newly-generated cadence.
     * If the last answer was incorrect, this method replays the last cadence.
     */
    private void testUser() {
        if (answerCorrect) {
            setAnswer();
            playAnswer();
        } else if (prefRepeat) {
            playAnswer();
        }
    }

    /**
     * Plays a cadence specified by <code>answer</code>.
     * When playing a new cadence, the two chords are pseudo-randomly generated.
     */
    private void playAnswer() {
        // set up UI
        setButtonsEnabled(false);
        replay.setEnabled(false);
        if (answerCorrect) {
            tv.setText(getResources().getString(R.string.playing_cadence));
        } else {
            tv.setText(getResources().getString(R.string.replaying));
        }

        if (answerCorrect) {
            if (answer.equals("Perfect")) {
                notes = CadenceGenerator.getCadence(CadenceGenerator.Cadence.PERFECT);
            } else if (answer.equals("Imperfect")) {
                notes = CadenceGenerator.getCadence(CadenceGenerator.Cadence.IMPERFECT);
            } else if (answer.equals("Plagal")) {
                notes = CadenceGenerator.getCadence(CadenceGenerator.Cadence.PLAGAL);
            } else {
                notes = CadenceGenerator.getCadence(CadenceGenerator.Cadence.DECEPTIVE);
            }

            randomShift = ChordExtensions.modulateNotesRand(notes, 5);
        }
        //make sure tonic note is between 5 and 16 (inclusive)
        int tonicId = 1 + randomShift;
        while (tonicId < 4) {
            tonicId += 12;
        }
        tonic = MediaPlayer.create(this, NoteMappings.getResourceId(tonicId));
        for (int i = 0; i < 8; i++) {
            mp[i] = MediaPlayer.create(this, NoteMappings.getResourceId(notes[i]));
        }

        tonic.start();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tonic.stop();
                tonic.release();
                tonic = null;
            }
        }, 1500);

        for (int i = 0; i < mp.length + 4; i += 4) {
            final int start = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (start - 4 >= 0) {
                        for (int j = start - 4; j < start; j++) {
                            mp[j].stop();
                            mp[j].release();
                            mp[j] = null;
                        }
                    }

                    if (start >= mp.length) {
                        replay.setEnabled(true);
                        setButtonsEnabled(true);
                        tv.setText(getResources().getString(R.string.identify_cadence));
                        isReplaying = false;
                        return;
                    }

                    for (int j = start; j < start + 4; j++) {
                        mp[j].start();
                    }
                }
            }, 1500 + i / 4 * 1500);
        }
    }

    /**
     * Replays the last interval for the user.
     *
     * @param view The REPLAY button pressed.
     */
    public void replayCadence(View view) {
        answerCorrect = false;
        isReplaying = true;
        playAnswer();
    }

    /**
     * Sets the value of <code>response</code> after the user has selected a cadence.
     * The result is displayed, and the activity is reset.
     *
     * @param view The button clicked by the user: perfect, plagal, imperfect, or deceptive.
     */
    public void answerClicked(View view) {
        response = ((Button) view).getText();
        setButtonsEnabled(false);
        displayResult();
        if (answerCorrect || prefRepeat) {
            replay.setEnabled(false);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (answerCorrect || prefRepeat) {
                    testUser();
                } else if (!isReplaying) {
                    setButtonsEnabled(true);
                    tv.setText(getResources().getString(R.string.try_again));
                }
            }
        }, 1500);
    }
}
