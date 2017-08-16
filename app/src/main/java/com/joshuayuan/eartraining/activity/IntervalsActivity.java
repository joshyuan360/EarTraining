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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.joshuayuan.eartraining.R;
import com.joshuayuan.eartraining.intelliyuan.NoteMappings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static com.joshuayuan.eartraining.activity.PreferenceKeys.HIGH_SCORES_KEY;
import static com.joshuayuan.eartraining.activity.PreferenceKeys.INTERVALS_SCORE_KEY;
import static com.joshuayuan.eartraining.activity.PreferenceKeys.INTERVALS_SPEED_KEY;
import static com.joshuayuan.eartraining.activity.PreferencesActivity.SettingsFragment.PREF_INTERVALS;
import static com.joshuayuan.eartraining.activity.PreferencesActivity.SettingsFragment.PREF_INTERVALS_ADVANCED;
import static com.joshuayuan.eartraining.activity.PreferencesActivity.SettingsFragment.PREF_REPEAT;

/**
 * The interval activity plays an interval and asks the user to identify it.
 * The score is based on the number of consecutive correct answers.
 *
 * @author Joshua Yuan
 */
public class IntervalsActivity extends EarTrainingActivity {
    private CharSequence part1;
    private CharSequence part2;
    private CharSequence answer1;
    private CharSequence answer2;

    private Button perfect, major, minor, aug;
    private Button unison, second, third, fourth, fifth, sixth, seventh, octave, ninth, tenth, eleventh, twelfth;

    private int note1;

    private boolean allowPerfect, allowAug;

    private boolean increasing;
    private int testType;

    private HashMap<String, Integer> intervalToSemitoneGap = new HashMap<>();

    /**
     * Initializes the <code>Button</code> fields and begins the test.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervals);
        setTitle("Intervals");

        onCreateEarTrainingActivity(
                INTERVALS_SCORE_KEY,
                R.id.intervals_volume,
                R.id.intervals_speed,
                INTERVALS_SPEED_KEY);

        loadAudioPlayer(2);

        initializeMap();
    }

    @Override
    protected void loadTextViews() {
        setInstructionsView((TextView) findViewById(R.id.intervalInstructions));
        setCurrentScoreView((TextView) findViewById(R.id.intervalCurrentScore));
        setHighScoreView((TextView) findViewById(R.id.intervalHighScore));
    }

    @Override
    protected void loadButtons() {
        perfect = (Button) findViewById(R.id.perfect);
        major = (Button) findViewById(R.id.major);
        minor = (Button) findViewById(R.id.minor);
        aug = (Button) findViewById(R.id.aug);

        unison = (Button) findViewById(R.id.unison);
        second = (Button) findViewById(R.id.second);
        third = (Button) findViewById(R.id.third);
        fourth = (Button) findViewById(R.id.fourth);
        fifth = (Button) findViewById(R.id.fifth);
        sixth = (Button) findViewById(R.id.sixth);
        seventh = (Button) findViewById(R.id.seventh);
        octave = (Button) findViewById(R.id.octave);
        ninth = (Button) findViewById(R.id.ninth);
        tenth = (Button) findViewById(R.id.tenth);
        eleventh = (Button) findViewById(R.id.eleventh);
        twelfth = (Button) findViewById(R.id.twelfth);

        setReplayButton((Button) findViewById(R.id.replayButton));
    }

    @Override
    protected void loadSelectionsAndPreferences() {
        setHighScoresPref(getSharedPreferences(HIGH_SCORES_KEY, Context.MODE_PRIVATE));
        getHighScoreView().setText(String.valueOf(getHighScoresPref().getInt(INTERVALS_SCORE_KEY, 0)));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> defaultSet = new HashSet<>(Arrays.asList(new String[]{
                "Minor Second", "Major Second", "Minor Sixth",
                "Major Sixth", "Minor Seventh", "Major Seventh",
                "Perfect Unison", "Perfect Fourth", "Perfect Fifth",
                "Perfect Octave", "Aug Fourth", "Minor Ninth", "Major Ninth",
                "Minor Tenth", "Major Tenth", "Perfect Eleventh", "Aug Eleventh",
                "Perfect Twelfth"}));
        setUserSelections(sharedPrefs.getStringSet(PREF_INTERVALS, defaultSet));
        setPrefRepeat(sharedPrefs.getBoolean(PREF_REPEAT, true));
        testType = Integer.parseInt(sharedPrefs.getString(PREF_INTERVALS_ADVANCED, "4"));

        for (String s : getUserSelections()) {
            if (s.startsWith("Perfect")) {
                allowPerfect = true;
            } else if (s.startsWith("Aug")) {
                allowAug = true;
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
        intervalToSemitoneGap.put("Aug 4", 6);
        intervalToSemitoneGap.put("Perfect Fifth", 7);
        intervalToSemitoneGap.put("Minor Sixth", 8);
        intervalToSemitoneGap.put("Major Sixth", 9);
        intervalToSemitoneGap.put("Minor Seventh", 10);
        intervalToSemitoneGap.put("Major Seventh", 11);
        intervalToSemitoneGap.put("Perfect Octave", 12); // check this one
        intervalToSemitoneGap.put("Minor Ninth", 13);
        intervalToSemitoneGap.put("Major Ninth", 14);
        intervalToSemitoneGap.put("Minor Tenth", 15);
        intervalToSemitoneGap.put("Major Tenth", 16);
        intervalToSemitoneGap.put("Perfect Eleventh", 17);
        intervalToSemitoneGap.put("Aug 11", 18);
        intervalToSemitoneGap.put("Perfect Twelfth", 19);
    }

    /**
     * Generates a new random interval and stores it in <code>answer1</code> and <code>answer2</code>.
     * Chances: 10% tritone, 30% perfect, 30% major, 30% minor.
     * Method is invoked only when the last answer provided is correct.
     */
    protected void setAnswer() {
        String[] primaryKey = new String[]{"Perfect", "Major", "Minor", "Aug"};
        Random random = new Random();

        answer1 = primaryKey[random.nextInt(primaryKey.length)];

        String[] nextValue;
        if (answer1.equals("Perfect")) {
            nextValue = new String[]{"Unison", "Fourth", "Fifth", "Octave", "Eleventh", "Twelfth"};
        } else if (answer1.equals("Major") || answer1.equals("Minor")) {
            nextValue = new String[]{"Second", "Third", "Sixth", "Seventh", "Ninth", "Tenth"};
        } else {
            nextValue = new String[]{"4", "11"};
        }

        answer2 = nextValue[random.nextInt(nextValue.length)];

        String answer = answer1 + " " + answer2;
        if (!answer.equals("Major Third") && !answer.equals("Minor Third") && !getUserSelections().contains(answer)) {
            setAnswer(); // todo: better algorithm (6.3)
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
                return isAnswerCorrect() ? Math.random() < 0.5 : increasing;
        }
    }

    private boolean getSolid() {
        return testType == 1;
    }

    /**
     * Plays the interval specified by <code>answer1</code> and <code>answer2</code>.
     * When playing a new interval, the starting note is pseudo-randomly picked.
     */
    protected void playAnswer() {
        // set up UI
        setAllRowsEnabled(false);
        getReplayButton().setEnabled(false);

        CharSequence interval = answer1 + " " + answer2;
        int intervalGap = intervalToSemitoneGap.get(interval.toString());
        int maxBottom = NoteMappings.MAX_NOTE - intervalGap;

        if (isAnswerCorrect()) {
            getInstructionsView().setText(getResources().getString(R.string.playing_interval));
            note1 = (int) (Math.random() * maxBottom) + 1;
        } else {
            getInstructionsView().setText(getResources().getString(R.string.replaying));
        }

        int note2 = note1 + intervalToSemitoneGap.get(interval.toString());

        increasing = getIncreasing();
        if (increasing) {
            audioPlayer[0] = MediaPlayer.create(this, NoteMappings.getResourceId(note1));
            audioPlayer[1] = MediaPlayer.create(this, NoteMappings.getResourceId(note2));
        } else {
            audioPlayer[0] = MediaPlayer.create(this, NoteMappings.getResourceId(note2));
            audioPlayer[1] = MediaPlayer.create(this, NoteMappings.getResourceId(note1));
        }

        if (getSolid()) {
            fireHarmonicInterval();
        } else {
            fireMelodicInterval();
        }
    }

    private void fireHarmonicInterval() {
        audioPlayer[0].start();
        audioPlayer[1].start();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < audioPlayer.length; i++) {
                    audioPlayer[i].stop();
                    audioPlayer[i].release();
                    audioPlayer[i] = null;
                }

                getReplayButton().setEnabled(true);
                setFirstRowEnabled(true);
                getInstructionsView().setText(getResources().getString(R.string.identify_interval));
                setReplaying(false);
            }
        }, getDelay());
    }

    private void fireMelodicInterval() {
        for (int i = 0; i < audioPlayer.length + 1; i++) {
            final int index = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (index - 1 >= 0) {
                        audioPlayer[index - 1].stop();
                        audioPlayer[index - 1].release();
                        audioPlayer[index - 1] = null;
                    }

                    if (index >= audioPlayer.length) {
                        getReplayButton().setEnabled(true);
                        setFirstRowEnabled(true);
                        getInstructionsView().setText(getResources().getString(R.string.identify_interval));
                        setReplaying(false);
                        return;
                    }

                    audioPlayer[index].start();
                }
            }, i * getDelay());
        }
    }

    /**
     * Determines if the unison, fourth, fifth, or octave button should be enabled
     * based on user settings.
     *
     * @param option Unison, fourth, fifth, or octave.
     * @return <code>true</code> if the button specified by <code>option</code> should be enabled.
     */
    private boolean allowPerfectButton(String option) {
        return getUserSelections().contains("Perfect " + option);
    }

    /**
     * Determines if the button should be enabled based on user settings.
     *
     * @param option A button on the interval activities window.
     * @return <code>true</code> if the button specified by <code>option</code> should be enabled.
     */
    private boolean allowMajorMinorButton(String option) {
        return getUserSelections().contains(part1 + " " + option);
    }

    private boolean allowAugButton(String option) {
        return getUserSelections().contains("Aug " + option);
    }

    protected void setAllRowsEnabled(boolean enabled) {
        setPerfectRowsEnabled(enabled);
        setMajorMinorRowsEnabled(enabled);
        setAugRowsEnabled(enabled);
        setFirstRowEnabled(enabled);
    }

    private void setPerfectRowsEnabled(boolean enabled) {
        unison.setEnabled(allowPerfectButton("Unison") && enabled);
        fourth.setEnabled(allowPerfectButton("Fourth") && enabled);
        fifth.setEnabled(allowPerfectButton("Fifth") && enabled);
        octave.setEnabled(allowPerfectButton("Octave") && enabled);

        eleventh.setEnabled(allowPerfectButton("Eleventh") && enabled);
        twelfth.setEnabled(allowPerfectButton("Twelfth") && enabled);
    }

    private void setMajorMinorRowsEnabled(boolean enabled) {
        second.setEnabled(allowMajorMinorButton("Second") && enabled);
        third.setEnabled(enabled);
        sixth.setEnabled(allowMajorMinorButton("Sixth") && enabled);
        seventh.setEnabled(allowMajorMinorButton("Seventh") && enabled);

        ninth.setEnabled(allowMajorMinorButton("Ninth") && enabled);
        tenth.setEnabled(allowMajorMinorButton("Tenth") && enabled);
    }

    private void setAugRowsEnabled(boolean enabled) {
        fourth.setEnabled(allowAugButton("4") && enabled);
        eleventh.setEnabled(allowAugButton("11") && enabled);
    }

    private void setFirstRowEnabled(boolean enabled) {
        perfect.setEnabled(allowPerfect && enabled);
        major.setEnabled(enabled);
        minor.setEnabled(enabled);
        aug.setEnabled(allowAug && enabled);
    }

    protected boolean answerCorrect() {
        return part1.equals(answer1) && (part2.equals(answer2) ||
                part2.equals("Fourth") && answer2.equals("4") ||
                part2.equals("Eleventh") && answer2.equals("11"));
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
            setPerfectRowsEnabled(true);
        } else if (part1.equals("Major") || part1.equals("Minor")) {
            setMajorMinorRowsEnabled(true);
        } else {
            setAugRowsEnabled(true);
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

        setAllRowsEnabled(false);
        displayResult();

        if (isAnswerCorrect() || isPrefRepeat()) {
            getReplayButton().setEnabled(false);
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAnswerCorrect() || isPrefRepeat()) {
                    testUser();
                } else if (!isReplaying()) {
                    setFirstRowEnabled(true);
                    getInstructionsView().setText(getResources().getString(R.string.try_again));
                }
            }
        }, 1500);
    }
}
