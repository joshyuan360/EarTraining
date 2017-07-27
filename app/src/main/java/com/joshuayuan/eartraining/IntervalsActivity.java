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

import com.joshuayuan.eartraining.IntelliYuan.NoteMappings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * The interval activity plays an interval and asks the user to identify it.
 * The score is based on the number of consecutive correct answers.
 *
 * @author Joshua Yuan
 */
public class IntervalsActivity extends AppCompatActivity {
    /**
     * The interval sound files to be played.
     */
    private final MediaPlayer[] mp = new MediaPlayer[2];
    /**
     * User input for perfect, major, or minor.
     */
    private CharSequence part1;
    /**
     * User input for unison, second, third, fourth, fifth, sixth, seventh, or octave.
     */
    private CharSequence part2;
    /**
     * Stores the first part of the correct answer: perfect, major, or minor.
     */
    private CharSequence answer1;
    /**
     * Stores the second part of the correct answer: unison, second, third, fourth, fifth, sixth seventh, or octave.
     */
    private CharSequence answer2;
    /**
     * A <code>Button</code> object in the first row of the interval activity window.
     */
    private Button perfect, major, minor, tritone;
    /**
     * A <code>Button</code> object in the bottom two rows of the interval activity window.
     */
    private Button unison, second, third, fourth, fifth, sixth, seventh, octave;
    /**
     * Allows the user to replay the last interval.
     */
    private Button replay;
    /**
     * <code>true</code> if the correct interval is identified.
     */
    private boolean answerCorrect = true;
    /**
     * Displays info to the user on screen.
     */
    private TextView tv;
    /**
     * Displays the current score.
     */
    private TextView hs;
    /**
     * The first note of the interval being played.
     */
    private int note1;
    /**
     * The current score of the user in this activity.
     */
    private int score;
    /**
     * The intervals that the user wishes to be tested on.
     */
    private Set<String> selections;
    /**
     * <code>true</code> if the user wants automatic replays.
     */
    private boolean prefRepeat;
    /**
     * <code>true</code> if the user wants to be tested on one or more interval(s).
     */
    private boolean allowPerfect;
    /**
     * Used to play sound after a specified amount of time.
     */
    private Handler handler = new Handler();
    private boolean isReplaying;
    private int testType;
    private HashMap<String, Integer> intervalToSemitoneGap = new HashMap<>();

    /**
     * Initializes the <code>Button</code> fields and begins the test.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Intervals");
        setContentView(R.layout.activity_intervals);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tv = (TextView) findViewById(R.id.insDisplay);
        hs = (TextView) findViewById(R.id.chordProgressionScore);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> defaultSet = new HashSet<>(Arrays.asList(new String[]{
                "Minor Second", "Major Second", "Minor Sixth",
                "Major Sixth", "Minor Seventh", "Major Seventh",
                "Perfect Unison", "Perfect Fourth", "Perfect Fifth",
                "Perfect Octave", "Aug 4"}));
        selections = sharedPrefs.getStringSet("pref_intervals", defaultSet);
        prefRepeat = sharedPrefs.getBoolean("pref_repeat", true);
        testType = Integer.parseInt(sharedPrefs.getString("pref_intervals_advanced", "4"));

        initializeMap();

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
            if (s.contains("Perfect")) {
                allowPerfect = true;
                break;
            }
        }
    }

    private void initializeMap() {
        intervalToSemitoneGap.put("Perfect Unison", 0);
        intervalToSemitoneGap.put("Minor Second", 1);
        intervalToSemitoneGap.put("Major Second", 2);
        intervalToSemitoneGap.put("Minor Third", 3);
        intervalToSemitoneGap.put("Major Third", 4);
        intervalToSemitoneGap.put("Perfect Fourth", 5);
        intervalToSemitoneGap.put("Aug 4 Aug 4", 6);
        intervalToSemitoneGap.put("Perfect Fifth", 7);
        intervalToSemitoneGap.put("Minor Sixth", 8);
        intervalToSemitoneGap.put("Major Sixth", 9);
        intervalToSemitoneGap.put("Minor Seventh", 10);
        intervalToSemitoneGap.put("Major Seventh", 11);
        intervalToSemitoneGap.put("Perfect Octave", 12); // check this one
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
        for (int i = 0; i < 2; i++) {
            if (mp[i] != null) {
                mp[i].release();
                mp[i] = null;
            }
        }
    }

    /**
     * Loads all the Button fields.
     */
    private void initializeButtons() {
        perfect = (Button) findViewById(R.id.perfect);
        major = (Button) findViewById(R.id.major);
        minor = (Button) findViewById(R.id.minor);
        tritone = (Button) findViewById(R.id.tritone);
        unison = (Button) findViewById(R.id.unison);
        second = (Button) findViewById(R.id.second);
        third = (Button) findViewById(R.id.third);
        fourth = (Button) findViewById(R.id.fourth);
        fifth = (Button) findViewById(R.id.fifth);
        sixth = (Button) findViewById(R.id.sixth);
        seventh = (Button) findViewById(R.id.seventh);
        octave = (Button) findViewById(R.id.octave);
        replay = (Button) findViewById(R.id.replay);
    }

    /**
     * Generates a new random interval and stores it in <code>answer1</code> and <code>answer2</code>.
     * Chances: 10% tritone, 30% perfect, 30% major, 30% minor.
     * Method is invoked only when the last answer provided is correct.
     */
    private void setAnswer() {
        if (Math.random() < 0.1) {
            answer1 = "Aug 4";
            answer2 = "Aug 4";
            if (!selections.contains(answer1)) {
                setAnswer();
            }
            return;
        }

        double randNum = Math.random() * 3;
        double randNum2 = Math.random() * 4;

        if (randNum < 1) {
            answer1 = "Perfect";

            if (randNum2 < 1) {
                answer2 = "Unison";
            } else if (randNum2 < 2) {
                answer2 = "Fourth";
            } else if (randNum2 < 3) {
                answer2 = "Fifth";
            } else {
                answer2 = "Octave";
            }
        } else {
            if (randNum < 2) {
                answer1 = "Major";
            } else {
                answer1 = "Minor";
            }

            if (randNum2 < 1) {
                answer2 = "Second";
            } else if (randNum2 < 2) {
                answer2 = "Third";
            } else if (randNum2 < 3) {
                answer2 = "Sixth";
            } else {
                answer2 = "Seventh";
            }
        }
        String answer = answer1 + " " + answer2;
        if (!answer.equals("Major Third") && !answer.equals("Minor Third") && !selections.contains(answer)) {
            setAnswer();
        }
    }

    private boolean getIncreasing() {
        switch (testType) {
            case 1: // solid
            case 2: // increasing
                return true;
            case 3: // decreasing
                return false;
            default: // increasing or decreasing
                return Math.random() < 0.5;
        }
    }

    private boolean getSolid() {
        return testType == 1;
    }

    /**
     * Plays the interval specified by <code>answer1</code> and <code>answer2</code>.
     * When playing a new interval, the starting note is pseudo-randomly picked.
     */
    private void playAnswer() {
        // set up UI
        setFirstRowEnabled(false);
        setBottomRowsEnabled(false, false);
        replay.setEnabled(false);

        if (answerCorrect) {
            tv.setText(getResources().getString(R.string.playing_interval));
            note1 = (int) (Math.random() * 16) + 1;
        } else {
            tv.setText(getResources().getString(R.string.replaying));
        }

        CharSequence interval = answer1 + " " + answer2;
        int note2 = note1 + intervalToSemitoneGap.get(interval.toString());

        if (getIncreasing()) {
            mp[0] = MediaPlayer.create(this, NoteMappings.getResourceId(note1));
            mp[1] = MediaPlayer.create(this, NoteMappings.getResourceId(note2));
        } else {
            mp[0] = MediaPlayer.create(this, NoteMappings.getResourceId(note2));
            mp[1] = MediaPlayer.create(this, NoteMappings.getResourceId(note1));
        }

        if (getSolid()) {
            mp[0].start();
            mp[1].start();
        } else {
            mp[0].start();
            mp[0].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer med) {
                    mp[1].start();
                }
            });
        }

        mp[1].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer med) {
                mp[0].release();
                mp[0] = null;

                mp[1].release();
                mp[1] = null;

                // set up UI
                replay.setEnabled(true);
                setFirstRowEnabled(true);
                tv.setText(getResources().getString(R.string.identify_interval));
                isReplaying = false;
            }
        });
    }

    /**
     * If the last answer was correct, this method plays a newly-generated interval.
     * If the last answer was incorrect, this method replays the last interval.
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
     * Determines if the unison, fourth, fifth, or octave button should be enabled
     * based on user settings.
     *
     * @param option Unison, fourth, fifth, or octave.
     * @return <code>true</code> if the button specified by <code>option</code> should be enabled.
     */
    private boolean allowPerButton(String option) {
        for (String s : selections) {
            if (s.equals("Perfect " + option)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the button should be enabled based on user settings.
     *
     * @param option A button on the interval activities window.
     * @return <code>true</code> if the button specified by <code>option</code> should be enabled.
     */
    private boolean allowButton(String option) {
        for (String s : selections) {
            if (s.equals(part1 + " " + option)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Enables or disables the bottom two rows of buttons.
     *
     * @param perfect Controls the unison, fourth, fifth, and octave buttons.
     * @param other   Controls the second, third, sixth, and seventh buttons.
     */
    private void setBottomRowsEnabled(boolean perfect, boolean other) {
        unison.setEnabled(allowPerButton("Unison") && perfect);
        fourth.setEnabled(allowPerButton("Fourth") && perfect);
        fifth.setEnabled(allowPerButton("Fifth") && perfect);
        octave.setEnabled(allowPerButton("Octave") && perfect);

        second.setEnabled(allowButton("Second") && other);
        third.setEnabled(other);
        sixth.setEnabled(allowButton("Sixth") && other);
        seventh.setEnabled(allowButton("Seventh") && other);
    }

    /**
     * Enables or disables the first row of buttons.
     *
     * @param enabled Controls the perfect, major, minor, and tritone buttons.
     */
    private void setFirstRowEnabled(boolean enabled) {
        perfect.setEnabled(allowPerfect && enabled);
        major.setEnabled(enabled);
        minor.setEnabled(enabled);
        tritone.setEnabled(selections.contains("Aug 4") && enabled);
    }

    /**
     * Displays the result of the user's input as "Correct! Next one playing..." or "Incorrect...".
     * The score is either incremented (if correct) or reset to zero (if incorrect).
     */
    private void displayResult() {
        if (part2.equals("Aug 4") && answer2.equals("Aug 4")) {
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
     *
     * @param score The current score.
     */
    private void setHighScores(int score) {
        SharedPreferences pref = getSharedPreferences("high scores", Context.MODE_PRIVATE);
        if (pref.getInt("ihs", 0) < score) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("ihs", score);
            editor.apply();
        }
    }

    /**
     * Replays the last interval for the user.
     *
     * @param view The REPLAY button pressed.
     */
    public void replayInterval(View view) {
        answerCorrect = false;
        isReplaying = true;
        playAnswer();
    }

    /**
     * Sets the value of <code>part1</code> after the user has selected perfect, major, or minor.
     *
     * @param view The button clicked by the user: perfect, major, or minor.
     */
    public void part1Clicked(View view) {
        part1 = ((Button) view).getText();
        setFirstRowEnabled(false);
        if (part1.equals("Perfect")) {
            setBottomRowsEnabled(true, false);
        } else {
            setBottomRowsEnabled(false, true);
        }
    }

    /**
     * Sets the value of <code>part2</code> after the user has selected an interval.
     * The result is displayed, and the activity is reset.
     *
     * @param view The button clicked by the user: unison, second, third, fourth, fifth,
     *             sixth, seventh, octave, or tritone.
     */
    public void part2Clicked(View view) {
        part2 = ((Button) view).getText();
        if (part2.equals("Aug 4")) {
            part1 = "Aug 4";
        }
        setFirstRowEnabled(false);
        setBottomRowsEnabled(false, false);
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
