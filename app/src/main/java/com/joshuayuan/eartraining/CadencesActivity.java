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
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The cadences activity plays a cadence and asks the user to identify it.
 * The score is based on the number of consecutive correct answers.
 * @author Joshua Yuan
 */
public class CadencesActivity extends AppCompatActivity { //todo: does it switch between minor and major? check this
    /** A <code>Button</code> object in the cadences activity. */
    private Button perfect, plagal, imperfect, deceptive;
    /** Allows the user to replay the last cadence. */
    private Button replay;
    /** The current answer to the last cadence played. */
    private CharSequence answer;
    /** User input for perfect, plagal, imperfect, or deceptive. */
    private CharSequence response;
    /** <code>true</code> if the correct interval is identified. */
    private boolean answerCorrect = true;
    /** Displays info to the user on screen. */
    private TextView tv;
    /** Contains all of the notes in both chords that are to be played. */
    private int notes [];
    /** The current score of the user. */
    private int score;
    /** Displays the user's high score. */
    private TextView hs;
    /** Contains the sound files required to play the cadence. */
    private final MediaPlayer[] mp = new MediaPlayer[8];
    private MediaPlayer tonic = new MediaPlayer();
    /** The chords that the user wishes to be tested on. */
    private Set<String> selections;
    /** <code>true</code> if the user wants automatic replays. */
    private boolean prefRepeat;
    /** Used to play sound after a specified amount of time. */
    private Handler handler = new Handler();
    private boolean isReplaying;
    private int randomShift;

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
        hs = (TextView) findViewById(R.id.cadenceScore);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> defaultSet = new HashSet(Arrays.asList(new String[] { "Imperfect", "Deceptive" })); //TODO change this
        selections = sharedPrefs.getStringSet("pref_cadences", defaultSet);
        prefRepeat = sharedPrefs.getBoolean("pref_repeat", true);

        initializeButtons();
        setButtonsEnabled(false);
        replay.setEnabled(false);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                testUser();
            }
        }, 1500);
    }

    /**
     * Stops any currently playing sounds when the user exits the activity.
     * @throws IllegalStateException if the internal player engine has not been
     * initialized or has been released.
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
    }

    /**
     * Generates a new random cadence and stores it in <code>answer</code>.
     * Chances: 25% perfect, 25% plagal, 25% imperfect, 25% deceptive.
     * Method is invoked only when the last answer provided is correct.
     */
    private void setAnswer() {
        double randNum = Math.random() * 4;
        if (randNum < 1) {
            answer = "Perfect";
        } else if (randNum < 2) {
            answer = "Plagal";
        } else if (randNum < 3) {
            answer = "Imperfect";
        } else {
            answer = "Deceptive";
        }
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
        hs.setText(String.valueOf(score));
        setHighScores(score);
    }

    /**
     * If the current score is higher than the high score, the new high score is updated
     * in shared preferences.
     * @param score The current score.
     */
    private void setHighScores(int score) {
        SharedPreferences pref = getSharedPreferences("high scores", Context.MODE_PRIVATE);
        if (pref.getInt("cahs", 0) < score) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("cahs", score);
            editor.apply();
        }
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

            int temp[] = new int[8];
            for (int i = 0; i < 8; i++) {
                temp[i] = notes[i];
            }
            Arrays.sort(temp);

            int minShift = -11 - temp[0];
            int maxShift = 28 - temp[7];
            randomShift = minShift + (int) (Math.random() * (maxShift - minShift + 1));
            //randomShift = minShift + 2; // bad: -5
            for (int i = 0; i < 8; i++) {
                notes[i] += randomShift;
            }
        }
        //make sure tonic note is between 5 and 16 (inclusive)
        int tonicId = 1 + randomShift;
        while (tonicId < 4) {
            tonicId += 12;
        }
        tonic = MediaPlayer.create(this, Utilities.getResourceId(tonicId));
        for (int i = 0; i < 8; i++) {
            mp[i] = MediaPlayer.create (this, Utilities.getResourceId(notes[i]));
        }

        tonic.start();
        tonic.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            public void onCompletion(MediaPlayer med) {
                tonic.stop();
                tonic.release();
                tonic = null;
                for (int i = 0; i < 4; i++) {
                    if (mp[i] != null) mp[i].start();
                }
            }
        });
        mp[3].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer med) {
                for (int i = 4; i < 8; i++) {
                    mp[i].start();
                }
            }
        });
        mp[7].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer med) {
                for (int i = 0; i < 8; i++) {
                    mp[i].stop();
                    mp[i].release();
                    mp[i] = null;
                }
                // set up UI
                replay.setEnabled(true);
                setButtonsEnabled(true);
                tv.setText(getResources().getString(R.string.identify_cadence));
                isReplaying = false;
            }
        });
    }

    /**
     * Replays the last interval for the user.
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
