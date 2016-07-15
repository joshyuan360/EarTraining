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
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Set;

/**
 * The interval activity plays an interval and asks the user to identify it.
 * The score is based on the number of consecutive correct answers.
 * @author Joshua Yuan
 */
public class IntervalsActivity extends AppCompatActivity {
    /** User input for perfect, major, or minor. */
    private CharSequence part1;
    /** User input for unison, second, third, fourth, fifth, sixth, seventh, or octave. */
    private CharSequence part2;
    /** Stores the first part of the correct answer: perfect, major, or minor. */
    private CharSequence answer1;
    /** Stores the second part of the correct answer: unison, second, third, fourth, fifth, sixth seventh, or octave. */
    private CharSequence answer2;
    /** A <code>Button</code> object in the first row of the interval activity window. */
    private Button perfect, major, minor, tritone;
    /** A <code>Button</code> object in the bottom two rows of the interval activity window. */
    private Button unison, second, third, fourth, fifth, sixth, seventh, octave;
    /** Allows the user to replay the last interval. */
    private Button replay;
    /** <code>true</code> if the correct interval is identified. */
    private boolean answerCorrect = true;
    /** True if the interval to be played will be increasing. */
    private boolean increasing;
    /** Displays info to the user on screen. */
    private TextView tv;
    /** Displays the current score. */
    private TextView hs;
    /** The first note of the interval being played. */
    private int note1;
    /** The current score of the user in this activity. */
    private int score;
    /** The interval sound files to be played. */
    private final MediaPlayer[] mp = new MediaPlayer[2];
    private Set<String> selections;
    private boolean prefRepeat, allowPerfect;
    /**
     * Initializes the <code>Button</code> fields and begins the test.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervals);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tv = (TextView) findViewById(R.id.insDisplay);
        hs = (TextView) findViewById(R.id.chordScore);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        selections = sharedPrefs.getStringSet("pref_intervals", null);
        prefRepeat = sharedPrefs.getBoolean("pref_repeat", true);

        initializeButtons();
        setFirstRowEnabled(false);
        setBottomRowsEnabled(false, false);
        replay.setEnabled(false);
        replay.setBackgroundColor(Color.parseColor("#2400F2FF"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                testUser();
                setFirstRowEnabled(true);
            }
        }, 1500);

        for (String s : selections) {
            if (s.contains("Perfect")) {
                allowPerfect = true;
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
        try {
            for (int i = 0; i < 2; i++) {
                if (mp[i] != null && mp[i].isPlaying()) {
                    mp[i].stop();
                    mp[i].release();
                }
            }
        } catch (IllegalStateException e) {
            //if the internal player engine has not been initialized or has been released, do nothing
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
        if (Math.random() < 0.1) { //to do: chances should be based on number of options overall
            answer1 = "Tritone";
            answer2 = "Tritone";
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

    /**
     * Plays the interval specified by <code>answer1</code> and <code>answer2</code>.
     * When playing a new interval, the starting note is pseudo-randomly picked.
     */
    private void playAnswer() {
        replay.setEnabled(false);
        replay.setBackgroundColor(Color.parseColor("#2400F2FF"));
        if (answerCorrect) {
            note1 = (int) (Math.random() * 16) + 1; //1 to 15
        }
        int note2;

        CharSequence interval = answer1 + " " + answer2;
        if (interval.equals("Perfect Unison")) {
            note2 = note1;
        } else if (interval.equals("Minor Second")) {
            note2 = note1 + 1;
        } else if (interval.equals("Major Second")) {
            note2 = note1 + 2;
        } else if (interval.equals("Minor Third")) {
            note2 = note1 + 3;
        } else if (interval.equals("Major Third")) {
            note2 = note1 + 4;
        } else if (interval.equals("Perfect Fourth")) {
            note2 = note1 + 5;
        } else if (interval.equals("Tritone Tritone")) {
            note2 = note1 + 6;
        } else if (interval.equals("Perfect Fifth")) {
            note2 = note1 + 7;
        } else if (interval.equals("Minor Sixth")) {
            note2 = note1 + 8;
        } else if (interval.equals("Major Sixth")) {
            note2 = note1 + 9;
        } else if (interval.equals("Minor Seventh")) {
            note2 = note1 + 10;
        } else if (interval.equals("Major Seventh")) {
            note2 = note1 + 11;
        } else {
            note2 = note1 + 12;
        }

        if (answerCorrect) {
            increasing = Math.random() < 0.5;
        }

        if (increasing) {
            mp[0] = MediaPlayer.create(this, Utilities.getResourceId(note1));
            mp[1] = MediaPlayer.create(this, Utilities.getResourceId(note2));
        } else {
            mp[0] = MediaPlayer.create(this, Utilities.getResourceId(note2));
            mp[1] = MediaPlayer.create(this, Utilities.getResourceId(note1));
        }

        mp[0].start();
        while (true) {
            if (!mp[0].isPlaying()) {
                mp[1].start();
                break;
            }
        }

        mp[1].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer med) {
                mp[0].release();
                mp[1].release();
                replay.setEnabled(true);
                replay.setBackgroundColor(Color.parseColor("#7B00F2FF"));
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
     * After a two second delay, this method disables the first row and starts a new test.
     */
    private void reset() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (answerCorrect)
                    tv.setText ("Identify the interval...");
                else
                    tv.setText("Try again!");
                setFirstRowEnabled(true);
                testUser();
            }
        }, 1500);
    }

    private boolean allowPerButton(String option) {
        for (String s : selections) {
            if (s.equals("Perfect " + option)) {
                return true;
            }
        }
        return false;
    }

    private boolean allowButton(String option) {
        for (String s : selections) {
            if (s.equals (part1 + " " + option)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Enables or disables the bottom two rows of buttons.
     * @param perfect Controls the unison, fourth, fifth, and octave buttons.
     * @param other Controls the second, third, sixth, and seventh buttons.
     */
    private void setBottomRowsEnabled(boolean perfect, boolean other) {
        unison.setEnabled(allowPerButton("Unison") && perfect);
        fourth.setEnabled(allowPerButton("Fourth") && perfect);
        fifth.setEnabled(allowPerButton("Fifth") && perfect);
        octave.setEnabled(allowPerButton("Octave") && perfect);

        unison.setBackgroundColor(allowPerButton("Unison") && perfect ? Color.parseColor("#7B00F2FF") : Color.parseColor("#1500F2FF"));
        fourth.setBackgroundColor(allowPerButton("Fourth") && perfect ? Color.parseColor("#7B00F2FF") : Color.parseColor("#1500F2FF"));
        fifth.setBackgroundColor(allowPerButton("Fifth") && perfect ? Color.parseColor("#7B00F2FF") : Color.parseColor("#1500F2FF"));
        octave.setBackgroundColor(allowPerButton("Octave") && perfect ? Color.parseColor("#7B00F2FF") : Color.parseColor("#1500F2FF"));

        second.setEnabled(allowButton("Second") && other);
        third.setEnabled(other);
        sixth.setEnabled(allowButton("Sixth") && other);
        seventh.setEnabled(allowButton("Seventh") && other);

        second.setBackgroundColor(allowButton("Second") && other ? Color.parseColor("#7B00F2FF") : Color.parseColor("#1500F2FF"));
        third.setBackgroundColor(other ? Color.parseColor("#7B00F2FF") : Color.parseColor("#1500F2FF"));
        sixth.setBackgroundColor(allowButton("Sixth") && other ? Color.parseColor("#7B00F2FF") : Color.parseColor("#1500F2FF"));
        seventh.setBackgroundColor(allowButton("Seventh") && other ? Color.parseColor("#7B00F2FF") : Color.parseColor("#1500F2FF"));
    }

    /**
     * Enables or disables the first row of buttons.
     * @param enabled Controls the perfect, major, minor, and tritone buttons.
     */
    private void setFirstRowEnabled(boolean enabled) {
        perfect.setEnabled(allowPerfect && enabled);
        major.setEnabled(enabled);
        minor.setEnabled(enabled);
        tritone.setEnabled(selections.contains("Tritone") && enabled);

        perfect.setBackgroundColor(allowPerfect && enabled ? Color.parseColor("#7B00F2FF") : Color.parseColor("#1500F2FF"));
        major.setBackgroundColor(enabled ? Color.parseColor("#7B00F2FF") : Color.parseColor("#1500F2FF"));
        minor.setBackgroundColor(enabled ? Color.parseColor("#7B00F2FF") : Color.parseColor("#1500F2FF"));
        tritone.setBackgroundColor(selections.contains("Tritone") && enabled ? Color.parseColor("#7B00F2FF") : Color.parseColor("#1500F2FF"));
    }

    /**
     * Displays the result of the user's input as "Correct!" or "Try Again!".
     * The score is either incremented (if correct) or reset to zero (if incorrect).
     */
    private void displayResult() {
        if (part2.equals("Tritone") && answer2.equals("Tritone")) {
            tv.setText("Correct! Next one playing...");
            answerCorrect = true;
            score++;
        } else if (part1.equals(answer1) && part2.equals(answer2)) {
            tv.setText("Correct! Next one playing...");
            answerCorrect = true;
            score++;
        } else {
            tv.setText("Incorrect...");
            answerCorrect = false;
            score = 0;
        }
        hs.setText(""+score);
        setHighScores(score);
    }

    /**
     * If the current score is higher than the high score, the new high score is updated
     * in shared preferences.
     * @param score The current score.
     */
    private void setHighScores(int score) {
        SharedPreferences pref = getSharedPreferences("high scores", Context.MODE_PRIVATE);
        if (pref.getInt("ihs", 0) < score) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("ihs", score);
            editor.commit();
        }
    }

    /**
     * Replays the last interval for the user.
     * @param view The REPLAY button pressed.
     */
    public void replayInterval(View view) {
        answerCorrect = false;
        playAnswer();
    }

    /**
     * Sets the value of <code>part1</code> after the user has selected perfect, major, or minor.
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
     * @param view The button clicked by the user: unison, second, third, fourth, fifth,
     *             sixth, seventh, octave, or tritone.
     */
    public void part2Clicked(View view) {
        part2 = ((Button) view).getText();
        if (part2.equals("Tritone")) {
            part1 = "Tritone";
            setFirstRowEnabled(false);
        }
        setBottomRowsEnabled(false, false);

        displayResult();
        reset();
    }
}
