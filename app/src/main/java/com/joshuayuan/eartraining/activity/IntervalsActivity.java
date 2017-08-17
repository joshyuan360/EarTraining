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

/**
 * The interval activity plays an interval and asks the user to identify it.
 * The score is based on the number of consecutive correct answers.
 *
 * @author Joshua Yuan
 */
public class IntervalsActivity extends EarTrainingActivity {
    private CharSequence part1;
    private CharSequence part2;
    private CharSequence answer;

    private Button perfect, major, minor, aug;
    private Button unison, second, third, fourth, fifth, sixth, seventh, octave, ninth, tenth, eleventh, twelfth;

    private int note1;

    private boolean allowPerfect, allowAug;

    private boolean increasing;
    private int testType;

    private HashMap<String, Integer> intervalToSemitoneGap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervals);
        setTitle("Intervals");

        onCreateEarTrainingActivity(
                getString(R.string.INTERVALS_SCORE_KEY),
                R.id.intervals_volume,
                R.id.intervals_speed,
                getString(R.string.INTERVALS_SPEED_KEY));

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
        setHighScoresPref(getSharedPreferences(getString(R.string.HIGH_SCORES_KEY), Context.MODE_PRIVATE));
        getHighScoreView().setText(String.valueOf(getHighScoresPref().getInt(getString(R.string.INTERVALS_SCORE_KEY), 0)));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> defaultSet = new HashSet<>(Arrays.asList(getResources().getStringArray(R.array.pref_intervals_values)));
        setUserSelections(sharedPrefs.getStringSet(getString(R.string.PREF_INTERVALS), defaultSet));
        setPrefRepeat(sharedPrefs.getBoolean(getString(R.string.PREF_REPEAT), true));
        testType = Integer.parseInt(sharedPrefs.getString(getString(R.string.PREF_INTERVALS_ADVANCED), "4"));

        for (String s : getUserSelections()) {
            if (s.startsWith(getString(R.string.perfect))) {
                allowPerfect = true;
            } else if (s.startsWith(getString(R.string.aug))) {
                allowAug = true;
            }
        }
    }

    private void initializeMap() {
        intervalToSemitoneGap.put(getString(R.string.perfect) + " " + getString(R.string.unison), 0);
        intervalToSemitoneGap.put(getString(R.string.minor) + " " + getString(R.string.second), 1);
        intervalToSemitoneGap.put(getString(R.string.major) + " " + getString(R.string.second), 2);
        intervalToSemitoneGap.put(getString(R.string.minor) + " " + getString(R.string.third), 3);
        intervalToSemitoneGap.put(getString(R.string.major) + " " + getString(R.string.third), 4);
        intervalToSemitoneGap.put(getString(R.string.perfect) + " " + getString(R.string.fourth), 5);
        intervalToSemitoneGap.put(getString(R.string.aug) + " " + getString(R.string.fourth), 6);
        intervalToSemitoneGap.put(getString(R.string.perfect) + " " + getString(R.string.fifth), 7);
        intervalToSemitoneGap.put(getString(R.string.minor) + " " + getString(R.string.sixth), 8);
        intervalToSemitoneGap.put(getString(R.string.major) + " " + getString(R.string.sixth), 9);
        intervalToSemitoneGap.put(getString(R.string.minor) + " " + getString(R.string.seventh), 10);
        intervalToSemitoneGap.put(getString(R.string.major) + " " + getString(R.string.seventh), 11);
        intervalToSemitoneGap.put(getString(R.string.perfect) + " " + getString(R.string.octave), 12);
        intervalToSemitoneGap.put(getString(R.string.minor) + " " + getString(R.string.ninth), 13);
        intervalToSemitoneGap.put(getString(R.string.major) + " " + getString(R.string.ninth), 14);
        intervalToSemitoneGap.put(getString(R.string.minor) + " " + getString(R.string.tenth), 15);
        intervalToSemitoneGap.put(getString(R.string.major) + " " + getString(R.string.tenth), 16);
        intervalToSemitoneGap.put(getString(R.string.perfect) + " " + getString(R.string.eleventh), 17);
        intervalToSemitoneGap.put(getString(R.string.aug) + " " + getString(R.string.eleventh), 18);
        intervalToSemitoneGap.put(getString(R.string.perfect) + " " + getString(R.string.twelfth), 19);
    }

    protected void setAnswer() {
        String[] primaryKey = new String[]{
                getString(R.string.perfect), getString(R.string.major),
                getString(R.string.minor), getString(R.string.aug)};
        Random random = new Random();

        answer = primaryKey[random.nextInt(primaryKey.length)];

        String[] nextValue;
        if (answer.equals(getString(R.string.perfect))) {
            nextValue = new String[]{
                    getString(R.string.unison), getString(R.string.fourth), getString(R.string.fifth),
                    getString(R.string.octave), getString(R.string.eleventh), getString(R.string.twelfth)};
        } else if (answer.equals(getString(R.string.major)) || answer.equals(getString(R.string.minor))) {
            nextValue = new String[]{
                    getString(R.string.second), getString(R.string.third), getString(R.string.sixth),
                    getString(R.string.seventh), getString(R.string.ninth), getString(R.string.tenth)};
        } else {
            nextValue = new String[]{getString(R.string.fourth), getString(R.string.eleventh)};
        }

        answer = answer + " " + nextValue[random.nextInt(nextValue.length)];

        if (!answer.equals(getString(R.string.major) + " "  + getString(R.string.third)) &&
                !answer.equals(getString(R.string.minor) + " " + getString(R.string.third)) &&
                !getUserSelections().contains(answer)) {
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

    protected void playAnswer() {
        // set up UI
        setAllRowsEnabled(false);
        getReplayButton().setEnabled(false);

        int intervalGap = intervalToSemitoneGap.get(answer.toString());
        int maxBottom = NoteMappings.MAX_NOTE - intervalGap;

        if (isAnswerCorrect()) {
            getInstructionsView().setText(getResources().getString(R.string.playing_interval));
            note1 = (int) (Math.random() * maxBottom) + 1;
        } else {
            getInstructionsView().setText(getResources().getString(R.string.replaying));
        }

        int note2 = note1 + intervalGap;

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

    private boolean allowPerfectButton(String option) {
        return getUserSelections().contains(getString(R.string.perfect) + " " + option);
    }

    private boolean allowMajorMinorButton(String option) {
        return getUserSelections().contains(part1 + " " + option);
    }

    private boolean allowAugButton(String option) {
        return getUserSelections().contains(getString(R.string.aug) + " " + option);
    }

    protected void setAllRowsEnabled(boolean enabled) {
        setPerfectRowsEnabled(enabled);
        setMajorMinorRowsEnabled(enabled);
        setAugRowsEnabled(enabled);
        setFirstRowEnabled(enabled);
    }

    private void setPerfectRowsEnabled(boolean enabled) {
        unison.setEnabled(allowPerfectButton(getString(R.string.unison)) && enabled);
        fourth.setEnabled(allowPerfectButton(getString(R.string.fourth)) && enabled);
        fifth.setEnabled(allowPerfectButton(getString(R.string.fifth)) && enabled);
        octave.setEnabled(allowPerfectButton(getString(R.string.octave)) && enabled);

        eleventh.setEnabled(allowPerfectButton(getString(R.string.eleventh)) && enabled);
        twelfth.setEnabled(allowPerfectButton(getString(R.string.twelfth)) && enabled);
    }

    private void setMajorMinorRowsEnabled(boolean enabled) {
        second.setEnabled(allowMajorMinorButton(getString(R.string.second)) && enabled);
        third.setEnabled(enabled);
        sixth.setEnabled(allowMajorMinorButton(getString(R.string.sixth)) && enabled);
        seventh.setEnabled(allowMajorMinorButton(getString(R.string.seventh)) && enabled);

        ninth.setEnabled(allowMajorMinorButton(getString(R.string.ninth)) && enabled);
        tenth.setEnabled(allowMajorMinorButton(getString(R.string.tenth)) && enabled);
    }

    private void setAugRowsEnabled(boolean enabled) {
        fourth.setEnabled(allowAugButton(getString(R.string.fourth)) && enabled);
        eleventh.setEnabled(allowAugButton(getString(R.string.eleventh)) && enabled);
    }

    private void setFirstRowEnabled(boolean enabled) {
        perfect.setEnabled(allowPerfect && enabled);
        major.setEnabled(enabled);
        minor.setEnabled(enabled);
        aug.setEnabled(allowAug && enabled);
    }

    protected boolean answerCorrect() {
        return answer.equals(part1 + " " + part2);
    }

    public void part1Clicked(View view) {
        part1 = ((Button) view).getText();
        setFirstRowEnabled(false);
        if (part1.equals(getString(R.string.perfect))) {
            setPerfectRowsEnabled(true);
        } else if (part1.equals(getString(R.string.major)) || part1.equals(getString(R.string.minor))) {
            setMajorMinorRowsEnabled(true);
        } else {
            setAugRowsEnabled(true);
        }
    }

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
