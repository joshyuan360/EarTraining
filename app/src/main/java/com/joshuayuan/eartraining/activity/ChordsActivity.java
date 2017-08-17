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

import com.joshuayuan.eartraining.intelliyuan.ChordExtensions;
import com.joshuayuan.eartraining.intelliyuan.NoteMappings;
import com.joshuayuan.eartraining.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static com.joshuayuan.eartraining.intelliyuan.ChordExtensions.modulateNotes;

/**
 * The chords activity plays a chord and asks the user to identify it.
 * The score is based on the number of consecutive correct answers.
 *
 * @author Joshua Yuan
 */
public class ChordsActivity extends EarTrainingActivity {
    private Button major, minor, dominant, diminished, augmented, major7, minor7;
    private Button root, first, second, third;

    private CharSequence part1, part2;
    private CharSequence answer;

    private int shift;

    private boolean prefSolid;
    private boolean allowDom, allowDim, allowAug, allowMaj7, allowMin7;

    /**
     * Initializes the <code>Button</code> fields and begins the test.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Chords");
        setContentView(R.layout.activity_chords);

        onCreateEarTrainingActivity(
                getString(R.string.CHORDS_SCORE_KEY),
                R.id.chords_volume,
                R.id.chords_speed,
                getString(R.string.CHORDS_SPEED_KEY));
        loadAudioPlayer(4);

        initializeIntervalToSemitoneMap();
    }

    @Override
    protected void setAllRowsEnabled(boolean enabled) {
        setFirstRowEnabled(enabled);
        setBottomRowsEnabled(enabled, enabled);
    }

    @Override
    protected void loadTextViews() {
        setInstructionsView((TextView) findViewById(R.id.chordInstructions));
        setCurrentScoreView((TextView) findViewById(R.id.chordCurrentScore));
        setHighScoreView((TextView) findViewById(R.id.chordHighScore));
    }

    @Override
    protected void loadSelectionsAndPreferences() {
        setHighScoresPref(getSharedPreferences(getString(R.string.HIGH_SCORES_KEY), Context.MODE_PRIVATE));
        getHighScoreView().setText(String.valueOf(getHighScoresPref().getInt(getString(R.string.CHORDS_SCORE_KEY), 0)));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> defaultSet = new HashSet<>(Arrays.asList(getResources().getStringArray(R.array.pref_chords_values)));
        setUserSelections(sharedPrefs.getStringSet(getString(R.string.PREF_CHORDS), defaultSet));
        setPrefRepeat(sharedPrefs.getBoolean(getString(R.string.PREF_REPEAT), true));
        prefSolid = sharedPrefs.getString(getString(R.string.PREF_CHORDS_ADVANCED), "1").equals("1");

        for (String s : getUserSelections()) {
            if (s.startsWith(getString(R.string.dom_7))) {
                allowDom = true;
            } else if (s.startsWith(getString(R.string.dim_7))) {
                allowDim = true;
            } else if (s.startsWith(getString(R.string.aug))) {
                allowAug = true;
            } else if (s.startsWith(getString(R.string.majmaj7))) {
                allowMaj7 = true;
            } else if (s.startsWith(getString(R.string.minmin7))) {
                allowMin7 = true;
            }
        }
    }

    @Override
    protected void loadButtons() {
        major = (Button) findViewById(R.id.major);
        minor = (Button) findViewById(R.id.minor);
        dominant = (Button) findViewById(R.id.dom7);
        diminished = (Button) findViewById(R.id.dim7);
        augmented = (Button) findViewById(R.id.augChord);

        major7 = (Button) findViewById(R.id.majmaj7);
        minor7 = (Button) findViewById(R.id.minmin7);

        root = (Button) findViewById(R.id.root);
        first = (Button) findViewById(R.id.first);
        second = (Button) findViewById(R.id.second);
        third = (Button) findViewById(R.id.third);

        setReplayButton((Button) findViewById(R.id.replayButton));
    }

    private void setFirstRowEnabled(boolean enabled) {
        major.setEnabled(enabled);
        minor.setEnabled(enabled);
        dominant.setEnabled(allowDom && enabled);
        diminished.setEnabled(allowDim && enabled);
        augmented.setEnabled(allowAug && enabled);
        major7.setEnabled(allowMaj7 && enabled);
        minor7.setEnabled(allowMin7 && enabled);
    }

    private boolean allowInvButton(String inversion) {
        return part1 != null && getUserSelections().contains(part1 + " " + inversion);
    }

    private boolean allowRoot() {
        return part1 != null && (
                part1.equals(getString(R.string.major)) || part1.equals(getString(R.string.minor))
                || getUserSelections().contains(part1 + " " + getString(R.string.root_pos)));
    }

    private void setBottomRowsEnabled(boolean enabled, boolean enableThird) {
        root.setEnabled(allowRoot() && enabled);
        first.setEnabled(allowInvButton(getString(R.string._1st_inv)) && enabled);
        second.setEnabled(allowInvButton(getString(R.string._2nd_inv)) && enabled);
        third.setEnabled(allowInvButton(getString(R.string._3rd_inv)) && enableThird);
    }

    protected void setAnswer() {
        String[] primaryKey = new String[] {
                getString(R.string.major), getString(R.string.minor), getString(R.string.dom_7),
                getString(R.string.dim_7), getString(R.string.aug), getString(R.string.majmaj7),
                getString(R.string.minmin7) };
        Random random = new Random();

        answer = primaryKey[random.nextInt(primaryKey.length)];

        String[] nextValue;
        if (answer.equals(getString(R.string.major)) || answer.equals(getString(R.string.minor))) {
            nextValue = new String[] {
                    getString(R.string.root_pos), getString(R.string._1st_inv), getString(R.string._2nd_inv) };
            answer = answer + " " + nextValue[random.nextInt(nextValue.length)];
        } else if (answer.equals(
                getString(R.string.dom_7)) || answer.equals(getString(R.string.majmaj7)) || answer.equals(getString(R.string.minmin7))) {
            nextValue = new String[] {
                    getString(R.string.root_pos), getString(R.string._1st_inv), getString(R.string._2nd_inv),
                    getString(R.string._3rd_inv) };
            answer = answer + " " +  nextValue[random.nextInt(nextValue.length)];
        }

        if (!answer.equals(getString(R.string.major) + " " + getString(R.string.root_pos)) &&
                !answer.equals(getString(R.string.minor) + " " + getString(R.string.root_pos)) &&
                !getUserSelections().contains(answer)) {
            setAnswer();
        }
    }

    private HashMap<String, int[]> chordToSemitoneGaps = new HashMap<>();
    private void initializeIntervalToSemitoneMap() {
        chordToSemitoneGaps.put(getString(R.string.major) + " " + getString(R.string.root_pos), new int[] {4, 3});
        chordToSemitoneGaps.put(getString(R.string.major) + " " + getString(R.string._1st_inv), new int[] {3, 5});
        chordToSemitoneGaps.put(getString(R.string.major) + " " + getString(R.string._2nd_inv), new int[] {5, 4});
        chordToSemitoneGaps.put(getString(R.string.minor) + " " + getString(R.string.root_pos), new int[] {3, 4});
        chordToSemitoneGaps.put(getString(R.string.minor) + " " + getString(R.string._1st_inv), new int[] {4, 5});
        chordToSemitoneGaps.put(getString(R.string.minor) + " " + getString(R.string._2nd_inv), new int[] {5, 3});
        chordToSemitoneGaps.put(getString(R.string.dom_7) + " " + getString(R.string.root_pos), new int[] {4, 3, 3});
        chordToSemitoneGaps.put(getString(R.string.dom_7) + " " + getString(R.string._1st_inv), new int[] {3, 3, 2});
        chordToSemitoneGaps.put(getString(R.string.dom_7) + " " + getString(R.string._2nd_inv), new int[] {3, 2, 4});
        chordToSemitoneGaps.put(getString(R.string.dom_7) + " " + getString(R.string._3rd_inv), new int[] {2, 4, 3});

        chordToSemitoneGaps.put(getString(R.string.dim_7), new int[] {3, 3, 3});
        chordToSemitoneGaps.put(getString(R.string.aug), new int[] {4, 4});

        chordToSemitoneGaps.put(getString(R.string.majmaj7) + " " + getString(R.string.root_pos), new int[] {4, 3, 4});
        chordToSemitoneGaps.put(getString(R.string.majmaj7) + " " + getString(R.string._1st_inv), new int[] {3, 4, 1});
        chordToSemitoneGaps.put(getString(R.string.majmaj7) + " " + getString(R.string._2nd_inv), new int[] {4, 1, 4});
        chordToSemitoneGaps.put(getString(R.string.majmaj7) + " " + getString(R.string._3rd_inv), new int[] {1, 4, 3});

        chordToSemitoneGaps.put(getString(R.string.minmin7) + " " + getString(R.string.root_pos), new int[] {3, 4, 3});
        chordToSemitoneGaps.put(getString(R.string.minmin7) + " " + getString(R.string._1st_inv), new int[] {4, 3, 2});
        chordToSemitoneGaps.put(getString(R.string.minmin7) + " " + getString(R.string._2nd_inv), new int[] {3, 2, 3});
        chordToSemitoneGaps.put(getString(R.string.minmin7) + " " + getString(R.string._3rd_inv), new int[] {2, 3, 4});
    }
    /**
     * Plays the chord specified by <code>answer</code> and <code>answer2</code>.
     * When playing a new chord, the starting note is pseudo-randomly picked.
     */
    protected void playAnswer() {
        // set up UI
        setFirstRowEnabled(false);
        setBottomRowsEnabled(false, false);
        getReplayButton().setEnabled(false);
        if (isAnswerCorrect()) {
            getInstructionsView().setText(getResources().getString(R.string.playing_chord));
        } else {
            getInstructionsView().setText(getResources().getString(R.string.replaying));
        }

        int[] stepInfo = chordToSemitoneGaps.get(answer.toString());

        int[] chordNotes = new int[stepInfo.length + 1];
        chordNotes[0] = 1;
        for (int i = 1; i < chordNotes.length; i++) {
            chordNotes[i] = chordNotes[i - 1] + stepInfo[i - 1];
        }

        if (isAnswerCorrect()) {
            shift = ChordExtensions.modulateNotesRand(chordNotes, 10);
        } else {
            modulateNotes(chordNotes, shift);
        }

        for (int i = 0; i < chordNotes.length; i++) {
            audioPlayer[i] = MediaPlayer.create(this, NoteMappings.getResourceId(chordNotes[i]));
        }

        if (prefSolid) {
            fireSolidChord(chordNotes.length);
        } else {
            fireBrokenChord(chordNotes.length);
        }
    }

    private void fireSolidChord(final int length) {
        for (int i = 0; i < length; i++) {
            audioPlayer[i].start();
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < length; i++) {
                    audioPlayer[i].stop();
                    audioPlayer[i].release();
                    audioPlayer[i] = null;

                    getReplayButton().setEnabled(true);
                    setFirstRowEnabled(true);
                    getInstructionsView().setText(getResources().getString(R.string.identify_chord));
                    setReplaying(false);
                }
            }
        }, getDelay());
    }

    private void fireBrokenChord(final int length) {
        for (int i = 0; i < length + 1; i++) {
            final int copy = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (copy > 0) {
                        audioPlayer[copy - 1].stop();
                        audioPlayer[copy - 1].release();
                        audioPlayer[copy - 1] = null;
                    }

                    if (copy == length) {
                        getReplayButton().setEnabled(true);
                        setFirstRowEnabled(true);
                        getInstructionsView().setText(getResources().getString(R.string.identify_chord));
                        setReplaying(false);

                        return;
                    }

                    audioPlayer[copy].start();
                }
            }, i * (int)(getDelay() / 3.0));
        }
    }

    @Override
    protected boolean answerCorrect() {
        return answer.equals(part1 + " " + part2);
    }

    /**
     * Sets the value of <code>part1</code> after the user has selected
     * major, minor, dominant, or diminished.
     *
     * @param view The button clicked by the user: major, minor, dominant, or diminished.
     */
    public void cpart1Clicked(View view) {
        setFirstRowEnabled(false);
        part1 = ((Button) view).getText();
        boolean isSeventh = part1.equals(getString(R.string.dom_7)) ||
                part1.equals(getString(R.string.majmaj7)) ||
                part1.equals(getString(R.string.minmin7));

        setBottomRowsEnabled(true, isSeventh);
    }

    /**
     * Sets the value of <code>part2</code> after the user has selected a chord.
     * The result is displayed, and the activity is reset.
     *
     * @param view The button clicked by the user:
     *             root pos, 1st inv, 2nd inv, 3rd inv, or diminished.
     */
    public void cpart2Clicked(View view) {
        setBottomRowsEnabled(false, false);
        part2 = ((Button) view).getText();

        if (part2.equals(getString(R.string.dim_7)) || part2.equals(getString(R.string.aug))) {
            part1 = "";
            setFirstRowEnabled(false);
        }

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
