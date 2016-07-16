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
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
public class CadencesActivity extends AppCompatActivity {
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
    /** The chords that the user wishes to be tested on. */
    private Set<String> selections;
    /** <code>true</code> if the user wants automatic replays. */
    private boolean prefRepeat;
    /** Used to play sound after a specified amount of time. */
    private Handler handler = new Handler();

    /**
     * Initializes the <code>Button</code> fields and begins the test.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadences);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tv = (TextView) findViewById(R.id.cadenceText);
        hs = (TextView) findViewById(R.id.cadenceScore);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> defaultSet = new HashSet(Arrays.asList(new String[] { "Imperfect", "Deceptive" }));
        selections = sharedPrefs.getStringSet("pref_cadences", defaultSet);
        prefRepeat = sharedPrefs.getBoolean("pref_repeat", true);

        initializeButtons();
        setButtonsEnabled(false);
        replay.setEnabled(false);
        replay.setBackgroundColor(Color.parseColor("#2400F2FF"));

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                testUser();
                setButtonsEnabled(true);
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

        perfect.setBackgroundColor(enabled ? Color.parseColor("#7B00F2FF") : Color.parseColor("#2400F2FF"));
        plagal.setBackgroundColor(enabled ? Color.parseColor("#7B00F2FF") : Color.parseColor("#2400F2FF"));
        imperfect.setBackgroundColor(enabled && selections.contains("Imperfect") ? Color.parseColor("#7B00F2FF") : Color.parseColor("#2400F2FF"));
        deceptive.setBackgroundColor(enabled && selections.contains ("Deceptive") ? Color.parseColor("#7B00F2FF") : Color.parseColor("#2400F2FF"));
    }

    /**
     * Displays the result of the user's input as "Correct!" or "Try Again!".
     * The score is either incremented (if correct) or reset to zero (if incorrect).
     */
    private void displayResult() {
        if (response.equals(answer)) {
            tv.setText("Correct! Next one playing...");
            answerCorrect = true;
            score++;
        } else {
            tv.setText("Incorrect...");
            answerCorrect = false;
            score = 0;
        }
        hs.setText("" + score);
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
            editor.commit();
        }
    }

    /**
     * After a two second delay, this method enables the answer buttons and tests the user again.
     */
    private void reset() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (answerCorrect) {
                    tv.setText("Identify the cadence...");
                } else {
                    tv.setText("Try again!");
                }
                setButtonsEnabled(true);
                testUser();
            }
        }, 2000);
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
     * Generates a random perfect cadence in C major or C minor.
     * The method generates a variety of Perfect Authentic Cadences and Inauthentic Cadences
     * involving V I/i and V7 I/i progressions.
     * Inversions are included, and different SATB possibilities are accounted for.
     * All cadences generated follow voice leading rules set by traditional Western music standards.
     * @return A random perfect cadence in C major (50%) or C minor (50%).
     */
    private int[] randPerfectCadence () {
        int minorValues[] = {5, 17, 29};
        // to switch any cadence to minor, subtract 1 from all values equal to 5, 17, or 29
        int[][] majorPerfectCadences =
                {
                        {8, 8, 15, 24, 1, 8, 17, 25}, // complete 2-3 tonic song, V I
                        {0, 8, 15, 24, 1, 8, 13, 17}, // incomplete 2-1 tonic song, V6 I
                        {8, 8, 15, 24, 5, 8, 13, 25}, // 5-3 inversion song, V I6
                        {-4, 8, 12, 15, 1, 5, 13, 13}, // incomplete 2-1 tonic song, V I
                        {0, 8, 20, 27, 1, 13, 20, 29}, // 2-3 inversion song, V6 I

                        {-4, 12, 15, 18, 1, 13, 13, 17}, // complete 2-1 V7 song, V7 I
                        {8, 8, 18, 24, 1, 8, 17, 25}, // incomplete 5-5 V7 song, V7 I
                        {-4, 6, 12, 15, 1, 5, 8, 13}, // V7 sacrifice song, V7 I

                        {0, 6, 8, 15, 1, 5, 8, 13}, // V(6-5) I
                        {3, 6, 8, 12, 1, 5, 8, 13}, // V(4-3) I
                        {6, 15, 20, 24, 5, 13, 20, 25} // V(4-2) I6
                };

        int randRow = (int)(Math.random() * majorPerfectCadences.length);
        if (Math.random() < 0.5) {
            for (int i = 0; i < 3; i++) {
                for (int j = 4; j < 8; j++) {
                    if (minorValues[i] == majorPerfectCadences[randRow][j]) {
                        majorPerfectCadences[randRow][j]--;
                    }
                }
            }
        }

        return majorPerfectCadences[randRow];
    }

    /**
     * Generates a random imperfect cadence in C major or C minor.
     * The method generates a variety of Imperfect Cadences
     * involving I V progressions.
     * Inversions are included, and different SATB possibilities are accounted for.
     * All cadences generated follow voice leading rules set by traditional Western music standards.
     * @return A random imperfect cadence in C major (50%) or C minor (50%).
     */
    private int[] randImperfectCadence() {
        int minorValues[] = {5, 17, 29};
        // to switch any cadence to minor, subtract 1 from all values equal to 5, 17, or 29
        int[][] majorImperfectCadences =
                {
                        {1, 8, 17, 25, -4, 8, 15, 24}, // I V version 1
                        {1, 17, 20, 25, -4, 12, 20, 27}, // I V version 2
                        {1, 8, 17, 25, -4, 12, 20, 27}, // I V version 3
                        {1, 13, 17, 20, -4, 15, 20, 24}, // I V version 4
                        {1, 13, 20, 29, -4, 12, 20, 27}, // I V version 5

                        {5, 13, 20, 25, 0, 8, 20, 27}, // I6 V6
                        {1, 8, 17, 25, 0, 8, 20, 27}, // I V6
                        {5, 13, 20, 25, 8, 12, 20, 27} // I6 V
                };

        int randRow = (int)(Math.random() * majorImperfectCadences.length);
        if (Math.random() < 0.5) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 4; j++) {
                    if (minorValues [i] == majorImperfectCadences[randRow][j]) {
                        majorImperfectCadences[randRow][j]--;
                    }
                }
            }
        }

        return majorImperfectCadences[randRow];
    }

    /**
     * Generates a random plagal cadence in C major or C minor.
     * The method generates a variety of Plagal Cadences
     * involving IV I progressions.
     * Inversions are included, and different SATB possibilities are accounted for.
     * All cadences generated follow voice leading rules set by traditional Western music standards.
     * @return A random plagal cadence in C major (50%) or C minor (50%).
     */
    private int[] randPlagalCadence() {
        int[] minorValues = {-2, 5, 10, 17, 22, 29};
        // to switch any cadence to minor, subtract 1 from all values equal to -2, 5, 10, 17, 22, 29
        int[][] majorPlagalCadences =
                {
                        {6, 18, 22, 25, 1, 17, 20, 25},//amen
                        {6, 10, 13, 18, 1, 8, 13, 17} //4-3 plagal
                };

        int randRow = (int)(Math.random() * majorPlagalCadences.length);
        if (Math.random() < 0.5) {
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 8; j++) {
                    if (minorValues[i] == majorPlagalCadences[randRow][j]) {
                        majorPlagalCadences[randRow][j]--;
                    }
                }
            }
        }

        return majorPlagalCadences[randRow];
    }

    /**
     * Generates a random deceptive cadence in C major or C minor.
     * The method generates a variety of Deceptive Cadences
     * involving V vi progressions.
     * Inversions are included, and different SATB possibilities are accounted for.
     * All cadences generated follow voice leading rules set by traditional Western music standards.
     * @return A random deceptive cadence in C major (50%) or C minor (50%).
     */
    private int[] randDeceptiveCadence() {
        int[] minorValues = {-2, 5, 10, 17, 22, 29};
        // to switch any cadence to minor, subtract 1 from all values equal to -2, 5, 10, 17, 22, 29
        int[][] majorDeceptiveCadences =
                {
                        {8, 12, 20, 27, 10, 13, 17, 25}, // weak version 1, V vi
                        {8, 12, 18, 27, 10, 13, 17, 25}, // strong version 1, V7 vi
                        {-4, 8, 12, 15, -2, 5, 13, 13}, // weak version 2, V vi
                        {-4, 6, 12, 15, -2, 5, 13, 13} // strong version 2, V7 vi
                };

        int randRow = (int)(Math.random() * majorDeceptiveCadences.length);
        if (Math.random() < 0.5) {
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 8; j++) {
                    if (minorValues [i] == majorDeceptiveCadences[randRow][j]) {
                        majorDeceptiveCadences[randRow][j]--;
                    }
                }
            }
        }

        return majorDeceptiveCadences[randRow];
    }

    /**
     * Plays a cadence specified by <code>answer</code>.
     * When playing a new cadence, the two chords are pseudo-randomly generated.
     */
    private void playAnswer() {
        replay.setEnabled(false);
        replay.setBackgroundColor(Color.parseColor("#2400F2FF"));
        if (answerCorrect) {
            if (answer.equals("Perfect")) {
                notes = randPerfectCadence();
            } else if (answer.equals("Imperfect")) {
                notes = randImperfectCadence();
            } else if (answer.equals("Plagal")) {
                notes = randPlagalCadence();
            } else {
                notes = randDeceptiveCadence();
            }

            int temp[] = new int[8];
            for (int i = 0; i < 8; i++) {
                temp[i] = notes[i];
            }
            Arrays.sort(temp);

            int minShift = -11 - temp[0];
            int maxShift = 28 - temp[7];
            int randomShift = minShift + (int) (Math.random() * (maxShift - minShift + 1));

            for (int i = 0; i < 8; i++) {
                notes[i] += randomShift;
            }
        }

        for (int i = 0; i < 8; i++) {
            mp[i] = MediaPlayer.create (this, Utilities.getResourceId(notes[i]));
        }
        for (int i = 0; i < 4; i++) {
            mp[i].start ();
        }
        while (true) {
            if (!mp[3].isPlaying()) {
                for (int i = 4; i < 8; i++) {
                    mp[i].start();
                }
                break;
            }
        }

        mp[7].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer med) {
                for (int i = 0; i < 8; i++) {
                    mp[i].release();
                    mp[i] = null;
                }
                replay.setEnabled(true);
                replay.setBackgroundColor(Color.parseColor("#7B00F2FF"));
            }
        });
    }

    /**
     * Replays the last interval for the user.
     * @param view The REPLAY button pressed.
     */
    public void replayCadence(View view) {
        answerCorrect = false;
        playAnswer();
    }

    /**
     * Sets the value of <code>response</code> after the user has selected a cadence.
     * The result is displayed, and the activity is reset.
     * @param view The button clicked by the user: perfect, plagal, imperfect, or deceptive.
     */
    public void answerClicked(View view) {
        setButtonsEnabled(false);
        response = ((Button) view).getText();

        displayResult();
        reset();
    }
}
