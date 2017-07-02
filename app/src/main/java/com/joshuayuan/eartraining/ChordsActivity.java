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
 * The chords activity plays a chord and asks the user to identify it.
 * The score is based on the number of consecutive correct answers.
 * @author Joshua Yuan
 */
public class ChordsActivity extends AppCompatActivity {
    /** A <code>Button</code> object in the first row of the chord activity window. */
    private Button major, minor, dominant, diminished;
    /** A <code>Button</code> object in the bottom two rows of the chord activity window. */
    private Button root, first, second, third;
    /** Allows user to replay the last chord. */
    private Button replay;
    /** User input for major, minor, dominant, or diminished. */
    private CharSequence part1;
    /** User input for root pos, 1st inv, 2nd inv, or 3rd inv. */
    private CharSequence part2;
    /** Stores the first part of the correct answer: major, minor, dominant, or diminished. */
    private CharSequence answer1;
    /**
     * Stores the second part of the correct answer: root pos, 1st inv, 2nd inv, or 3rd inv.
     * If diminished is the answer, this variable is set to "none".
     */
    private CharSequence answer2;
    /** <code>true</code> if the correct chord is identified. */
    private boolean answerCorrect = true;
    /** Displays info to the user on screen. */
    private TextView tv;
    /** Displays the current score. */
    private TextView hs;
    /** The lowest note of the chord being played. */
    private int note1;
    /** The current score of the user in this activity. */
    private int score = 0;
    /** The sound files required to play the chord. */
    private final MediaPlayer [] mp = new MediaPlayer [4];
    /** The chords that the user wishes to be tested on. */
    private Set<String> selections;
    /** <code>true</code> if the user wants automatic replays. */
    private boolean prefRepeat;
    /** <code>true</code> if the user wants to be tested on one or more dominant 7th chord(s). */
    private boolean allowDom;
    /** Used to play sound after a specified amount of time. */
    private Handler handler = new Handler();
    private boolean isReplaying;

    /**
     * Initializes the <code>Button</code> fields and begins the test.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Chords");
        setContentView(R.layout.activity_chords);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tv = (TextView) findViewById(R.id.chInstructions);
        hs = (TextView) findViewById(R.id.chordProgressionScore);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> defaultSet = new HashSet<String>(Arrays.asList(new String[] {
                "Major 1st Inv", "Major 2nd Inv", "Minor 1st Inv",
                "Minor 2nd Inv", "Dom 7 Root Pos", "Dom 7 1st Inv",
                "Dom 7 2nd Inv", "Dom 7 3rd Inv", "Dim 7 none", }));
        selections = sharedPrefs.getStringSet("pref_chords", defaultSet);
        prefRepeat = sharedPrefs.getBoolean("pref_repeat", true);

        initializeButtons();
        setFirstRowEnabled(false);
        setBottomRowsEnabled(false, false);
        replay.setEnabled(false);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                testUser();
            }
        }, 1500);

        for (String s : selections) {
            if (s.contains("Dom")) {
                allowDom = true;
                break;
            }
        }
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
        for (int i = 0; i < 4; i++) {
            if (mp[i] != null) {
                mp[i].release();
                mp[i] = null;
            }
        }
    }

    /**
     * Loads all the button fields.
     */
    private void initializeButtons() {
        major = (Button) findViewById(R.id.major);
        minor = (Button) findViewById(R.id.minor);
        dominant = (Button) findViewById(R.id.dom7);
        diminished = (Button) findViewById(R.id.dim7);

        root = (Button) findViewById(R.id.root);
        first = (Button) findViewById(R.id.first);
        second = (Button) findViewById(R.id.second);
        third = (Button) findViewById(R.id.third);

        replay = (Button) findViewById(R.id.replay);
    }

    /**
     * Enables or disables the first row of buttons.
     * @param enabled Controls the major, minor, dominant, and diminished buttons.
     */
    private void setFirstRowEnabled(boolean enabled) {
        major.setEnabled(enabled);
        minor.setEnabled(enabled);
        dominant.setEnabled(allowDom && enabled);
        diminished.setEnabled(selections.contains("Dim 7 none") && enabled);
    }

    /**
     * Determines if the inversion button should be enabled
     * based on user settings.
     * @param inversion The inversion button.
     * @return <code>true</code> if the button specified by <code>inversion</code> should be enabled.
     */
    private boolean allowInvButton(String inversion) {
        for (String s : selections) {
            if (part1 != null && s.contains(part1) && s.contains(inversion)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Enables or disables the bottom two rows of buttons.
     * @param enabled Controls the root pos, 1st inv, and 2nd inv buttons.
     * @param enableThird Controls the 3rd inv button.
     */
    private void setBottomRowsEnabled(boolean enabled, boolean enableThird) {
        boolean allowRoot = part1 != null && !(part1.equals("Dom 7") && !selections.contains("Dom 7 Root Pos"));
        root.setEnabled(allowRoot && enabled);
        first.setEnabled(allowInvButton("1st Inv") && enabled);
        second.setEnabled(allowInvButton("2nd Inv") && enabled);
        third.setEnabled(allowInvButton("3rd Inv") && enableThird);
    }

    /**
     * Generates a new random chord and stores it in <code>answer1</code> and <code>answer2</code>.
     * Chances: 25% major, 25% minor, 25% dominant, 25% diminished.
     * Method is invoked only when the last answer provided is correct.
     */
    private void setAnswer() {
        double randNum = Math.random() * 4;
        if (randNum < 2) {
            if (randNum < 1) {
                answer1 = "Major";
            } else {
                answer1 = "Minor";
            }

            double randNum2 = Math.random() * 3;
            if (randNum2 < 1) {
                answer2 = "Root Pos";
            } else if (randNum2 < 2) {
                answer2 = "1st Inv";
            } else {
                answer2 = "2nd Inv";
            }
        } else if (randNum < 3) {
            answer1 = "Dom 7";

            double randNum2 = Math.random() * 4;
            if (randNum2 < 1) {
                answer2 = "Root Pos";
            } else if (randNum2 < 2) {
                answer2 = "1st Inv";
            } else if (randNum2 < 3) {
                answer2 = "2nd Inv";
            } else {
                answer2 = "3rd Inv";
            }
        } else {
            answer1 = "Dim 7";
            answer2 = "none";
        }
        //Log.i ("answer", answer1 +  " " + answer2);
        String answer = answer1 + " " + answer2;
        if (!answer.equals("Major Root Pos") && !answer.equals("Minor Root Pos") && !selections.contains(answer)) {
            setAnswer();
        }
    }

    /**
     * Plays the chord specified by <code>answer1</code> and <code>answer2</code>.
     * When playing a new chord, the starting note is pseudo-randomly picked.
     */
    private void playAnswer() {
        // set up UI
        setFirstRowEnabled(false);
        setBottomRowsEnabled(false, false);
        replay.setEnabled(false);
        if (answerCorrect) {
            tv.setText(getResources().getString(R.string.playing_chord));
        } else {
            tv.setText(getResources().getString(R.string.replaying));
        }

        if (answerCorrect) {
            note1 = (int) (Math.random() * 16) + 1; //1 to 15
        }
        int note2, note3, note4 = 1;

        CharSequence chord = answer1 + " " + answer2;
        if (chord.equals("Major Root Pos")) {
            note2 = note1 + 4;
            note3 = note2 + 3;
        } else if (chord.equals("Major 1st Inv")) {
            note2 = note1 + 3;
            note3 = note2 + 5;
        } else if (chord.equals("Major 2nd Inv")) {
            note2 = note1 + 5;
            note3 = note2 + 4;
        } else if (chord.equals("Minor Root Pos")) {
            note2 = note1 + 3;
            note3 = note2 + 4;
        } else if (chord.equals("Minor 1st Inv")) {
            note2 = note1 + 4;
            note3 = note2 + 5;
        } else if (chord.equals("Minor 2nd Inv")) {
            note2 = note1 + 5;
            note3 = note2 + 3;
        } else if (chord.equals("Dom 7 Root Pos")) {
            note2 = note1 + 4;
            note3 = note2 + 3;
            note4 = note3 + 3;
        } else if (chord.equals("Dom 7 1st Inv")) {
            note2 = note1 + 3;
            note3 = note2 + 3;
            note4 = note3 + 2;
        } else if (chord.equals("Dom 7 2nd Inv")) {
            note2 = note1 + 3;
            note3 = note2 + 2;
            note4 = note3 + 4;
        } else if (chord.equals("Dom 7 3rd Inv")) {
            note2 = note1 + 2;
            note3 = note2 + 4;
            note4 = note3 + 3;
        } else { //diminished
            note2 = note1 + 3;
            note3 = note2 + 3;
            note4 = note3 + 3;
        }

        mp[0] = MediaPlayer.create(this, Utilities.getResourceId(note1));
        mp[1] = MediaPlayer.create(this, Utilities.getResourceId(note2));
        mp[2] = MediaPlayer.create(this, Utilities.getResourceId(note3));
        mp[3] = MediaPlayer.create(this, Utilities.getResourceId(note4));

        for (int i = 0; i < 3; i++) {
            mp[i].start();
        }

        if (answer1.subSequence(0, 1).equals ("D")) {
            mp[3].start();
        }

        mp[2].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer m) {
                for (int i = 0; i < 4; i++) {
                    mp[i].stop();
                    mp[i].release();
                    mp[i] = null;
                }
                // set up UI
                replay.setEnabled(true);
                setFirstRowEnabled(true);
                tv.setText(getResources().getString(R.string.identify_chord));
                isReplaying = false;
            }
        });
    }

    /**
     * If the last answer was correct, this method plays a newly-generated chord.
     * If the last answer was incorrect, this method replays the last chord.
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
     * Displays the result of the user's input as "Correct!" or "Incorrect...".
     * The score is either incremented (if correct) or reset to zero (if incorrect).
     */
    private void displayResult() {
        if (answer1.equals("Dim 7") && part2.equals("Dim 7")) {
            tv.setText(getResources().getString(R.string.correct));
            answerCorrect = true;
            score++;
        } else if (part1.equals(answer1) && part2.equals(answer2)) {
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
        if (pref.getInt("chhs", 0) < score) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("chhs", score);
            editor.commit();
        }
    }

    /**
     * Replays the last interval for the user.
     * @param view The REPLAY button pressed.
     */
    public void replayChord(View view) {
        answerCorrect = false;
        isReplaying = true;
        playAnswer();
    }

    /**
     * Sets the value of <code>part1</code> after the user has selected
     * major, minor, dominant, or diminished.
     * @param view The button clicked by the user: major, minor, dominant, or diminished.
     */
    public void cpart1Clicked(View view) {
        setFirstRowEnabled(false);
        part1 = ((Button) view).getText();
        setBottomRowsEnabled(true, part1.equals("Dom 7"));
    }

    /**
     * Sets the value of <code>part2</code> after the user has selected a chord.
     * The result is displayed, and the activity is reset.
     * @param view The button clicked by the user:
     *             root pos, 1st inv, 2nd inv, 3rd inv, or diminished.
     */
    public void cpart2Clicked(View view) {
        setBottomRowsEnabled(false, false);
        part2 = ((Button) view).getText();
        if (part2.equals("Dim 7")) {
            part1 = "Dim 7";
            setFirstRowEnabled(false);
        }
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
                    setFirstRowEnabled(true);
                    tv.setText(getResources().getString(R.string.try_again));
                }
            }
        }, 1500);
    }
}
