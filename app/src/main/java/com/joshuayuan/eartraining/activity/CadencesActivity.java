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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.joshuayuan.eartraining.intelliyuan.CadenceGenerator;
import com.joshuayuan.eartraining.intelliyuan.ChordExtensions;
import com.joshuayuan.eartraining.intelliyuan.NoteMappings;
import com.joshuayuan.eartraining.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * The cadences activity plays a cadence and asks the user to identify it.
 * The score is based on the number of consecutive correct answers.
 *
 * @author Joshua Yuan
 */
public class CadencesActivity extends EarTrainingActivity { //todo: does it switch between minor and major? check this
    private Button perfect, plagal, imperfect, deceptive;

    private CharSequence answer;
    private CharSequence response;

    private int notes[];

    private MediaPlayer tonic = new MediaPlayer();

    private int randomShift;

    /**
     * Initializes the <code>Button</code> fields and begins the test.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Cadences");
        setContentView(R.layout.activity_cadences);

        onCreateEarTrainingActivity(
                getString(R.string.CADENCES_SCORE_KEY),
                R.id.cadences_volume,
                R.id.cadences_speed,
                getString(R.string.CADENCES_SPEED_KEY));

        loadAudioPlayer(8);
    }

    @Override
    protected void loadTextViews() {
        setInstructionsView((TextView) findViewById(R.id.cadenceText));
        setCurrentScoreView((TextView) findViewById(R.id.cadenceScore));
        setHighScoreView((TextView) findViewById(R.id.cadenceHighestScore));
    }

    @Override
    protected void loadSelectionsAndPreferences() {
        setHighScoresPref(getSharedPreferences(getString(R.string.HIGH_SCORE_KEYS), Context.MODE_PRIVATE));
        getHighScoreView().setText(String.valueOf(getHighScoresPref().getInt(getString(R.string.CADENCES_SCORE_KEY), 0)));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> defaultSet = new HashSet(Arrays.asList(new String[]{"Imperfect", "Deceptive"})); //TODO change this (6.3)
        setUserSelections(sharedPrefs.getStringSet(getString(R.string.PREF_CADENCES), defaultSet));
        setPrefRepeat(sharedPrefs.getBoolean(getString(R.string.PREF_REPEAT), true));
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (tonic != null) {
            tonic.release();
            tonic = null;
        }
    }

    @Override
    protected void setAnswer() {
        String[] primaryKey = new String[] { "Perfect", "Plagal", "Imperfect", "Deceptive" };

        Random random = new Random();
        answer = primaryKey[random.nextInt(primaryKey.length)];

        if (!answer.equals("Perfect") && !answer.equals("Plagal") && !getUserSelections().contains(answer)) {
            setAnswer();
        }
    }

    @Override
    protected void loadButtons() {
        perfect = (Button) findViewById(R.id.perfect);
        plagal = (Button) findViewById(R.id.plagal);
        imperfect = (Button) findViewById(R.id.imperfect);
        deceptive = (Button) findViewById(R.id.deceptive);
        setReplayButton((Button) findViewById(R.id.replayButton));
    }

    @Override
    protected void setAllRowsEnabled(boolean enabled) {
        perfect.setEnabled(enabled);
        plagal.setEnabled(enabled);

        imperfect.setEnabled(enabled && getUserSelections().contains("Imperfect"));
        deceptive.setEnabled(enabled && getUserSelections().contains("Deceptive"));
    }

    @Override
    protected void playAnswer() {
        // set up UI
        setAllRowsEnabled(false);
        getReplayButton().setEnabled(false);
        if (isAnswerCorrect()) {
            getInstructionsView().setText(getResources().getString(R.string.playing_cadence));
        } else {
            getInstructionsView().setText(getResources().getString(R.string.replaying));
        }

        if (isAnswerCorrect()) {
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
            audioPlayer[i] = MediaPlayer.create(this, NoteMappings.getResourceId(notes[i]));
        }

        tonic.start();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tonic.stop();
                tonic.release();
                tonic = null;
            }
        }, getDelay());

        for (int i = 0; i < audioPlayer.length + 4; i += 4) {
            final int start = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (start - 4 >= 0) {
                        for (int j = start - 4; j < start; j++) {
                            audioPlayer[j].stop();
                            audioPlayer[j].release();
                            audioPlayer[j] = null;
                        }
                    }

                    if (start >= audioPlayer.length) {
                        getReplayButton().setEnabled(true);
                        setAllRowsEnabled(true);
                        getInstructionsView().setText(getResources().getString(R.string.identify_cadence));
                        setReplaying(false);
                        return;
                    }

                    for (int j = start; j < start + 4; j++) {
                        audioPlayer[j].start();
                    }
                }
            }, getDelay() + i / 4 * getDelay());
        }
    }

    @Override
    protected boolean answerCorrect() {
        return response.equals(answer);
    }

    /**
     * Sets the value of <code>response</code> after the user has selected a cadence.
     * The result is displayed, and the activity is reset.
     *
     * @param view The button clicked by the user: perfect, plagal, imperfect, or deceptive.
     */
    public void answerClicked(View view) {
        response = ((Button) view).getText();
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
                    setAllRowsEnabled(true);
                    getInstructionsView().setText(getResources().getString(R.string.try_again));
                }
            }
        }, 1500);
    }
}
