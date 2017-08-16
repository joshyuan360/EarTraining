package com.joshuayuan.eartraining.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.joshuayuan.eartraining.intelliyuan.ChordProgressionGenerator;
import com.joshuayuan.eartraining.intelliyuan.NoteMappings;
import com.joshuayuan.eartraining.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The chord progression activity plays a chord progression twice as per the
 * RCM syllabus. The score is based on the number of consecutive correct answers.
 *
 * @author Joshua Yuan
 */
public class ChordProgressionActivity extends EarTrainingActivity {
    private String[] answer;

    private Button one, four, five, six, cadential;
    private CharSequence response;

    private int notes[];

    private int chordNumber = 0;
    private int seqLength;

    /**
     * Initializes the <code>Button</code> fields and begins the test.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Chord Progression");
        setContentView(R.layout.activity_chord_progression);

        onCreateEarTrainingActivity(
                getString(R.string.PROGRESSIONS_SCORE_KEY),
                R.id.progressions_volume,
                R.id.progressions_speed,
                getString(R.string.PROGRESSIONS_SPEED_KEY));

        loadAudioPlayer(seqLength * 4);
    }

    @Override
    protected void loadSelectionsAndPreferences() {
        setHighScoresPref(PreferenceManager.getDefaultSharedPreferences(this));
        Set<String> defaultSet = new HashSet(Arrays.asList(new String[]{"six", "cadential"}));
        setUserSelections(getHighScoresPref().getStringSet(getString(R.string.PREF_CHORD_PROGRESSIONS), defaultSet));
        setPrefRepeat(getHighScoresPref().getBoolean(getString(R.string.PREF_REPEAT), true));

        seqLength = Integer.parseInt(getHighScoresPref().getString(getString(R.string.PREF_SEQ_LENGTH), "5"));
        boolean includeSix = getUserSelections().contains("six");
        boolean includeCadential = getUserSelections().contains("cadential");
        int tonalityChoice = Integer.parseInt(getHighScoresPref().getString(getString(R.string.PREF_PROGRESSION_TONALITY), "3"));

        ChordProgressionGenerator.initialize(seqLength, includeSix, includeCadential, tonalityChoice);
    }

    @Override
    protected void loadTextViews() {
        setInstructionsView((TextView) findViewById(R.id.chordProgressionText));
        setCurrentScoreView((TextView) findViewById(R.id.chordCurrentScore));
        setHighScoreView((TextView) findViewById(R.id.chordHighestProgressionScore));
    }

    @Override
    protected void setAnswer() {
        notes = ChordProgressionGenerator.nextChordProgression();
        answer = ChordProgressionGenerator.getChordSequence();
    }

    @Override
    protected void loadButtons() {
        one = (Button) findViewById(R.id.one);
        four = (Button) findViewById(R.id.four);
        five = (Button) findViewById(R.id.five);
        six = (Button) findViewById(R.id.six);
        cadential = (Button) findViewById((R.id.cadential));
        setReplayButton((Button) findViewById(R.id.replayButton));
    }

    @Override
    protected void setAllRowsEnabled(boolean enabled) {
        one.setEnabled(enabled);
        four.setEnabled(enabled);
        five.setEnabled(enabled);

        // look into this
        six.setEnabled(enabled && getUserSelections().contains("six"));
        cadential.setEnabled(enabled && getUserSelections().contains("cadential"));
    }

    @Override
    protected boolean answerCorrect() {
        return response.equals(answer[chordNumber]);
    }

    @Override
    protected void playAnswer() {
        // set up UI
        setAllRowsEnabled(false);
        getReplayButton().setEnabled(false);
        if (isAnswerCorrect()) {
            getInstructionsView().setText(getResources().getString(R.string.playing_chord_progression));
        } else {
            getInstructionsView().setText(getResources().getString(R.string.replaying));
        }

        firePlayer();
    }

    /**
     * Recursive method that continuously fires four MediaPlayer events simultaneously,
     * one per chord. Resources are deleted after each chord is played to make this operation
     * as memory efficient as possible.
     *
     */
    public void firePlayer() {
        for (int i = 0; i < audioPlayer.length; i++) {
            audioPlayer[i] = MediaPlayer.create(this, NoteMappings.getResourceId(notes[i]));
        }

        for (int i = 0; i < audioPlayer.length + 4; i += 4) {
            final int start = i;

            int delay = 0;
            if (start >= audioPlayer.length) delay = getDelay();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (start - 4 >= 0) {
                        for (int n = start - 4; n < start; n++) {
                            audioPlayer[n].stop();
                            audioPlayer[n].release();
                            audioPlayer[n] = null;
                        }
                    }
                    
                    if (start >= audioPlayer.length) {
                        playChord();
                        return;
                    }

                    for (int n = start; n < start + 4; n++) {
                        audioPlayer[n].start();
                    }
                }
            }, i / 4 * getDelay() + delay);
        }
    }

    @Override
    public void replayTest(View view) {
        playChord();
    }

    /**
     * Creates and fires four MediaPlayer events to play the next chord in the progression.
     * Resources are deleted immediately afterwards.
     */
    public void playChord() {
        // set up UI
        setAllRowsEnabled(false);
        getReplayButton().setEnabled(false);
        if (isAnswerCorrect()) {
            String text;
            if (chordNumber == 0) {
                text = getResources().getString(R.string.playing_first_chord);
            } else {
                text = String.format(getResources().getString(R.string.playing_each_chord), chordNumber + 1);
            }
            getInstructionsView().setText(text);
        } else {
            getInstructionsView().setText(getResources().getString(R.string.replaying));
        }

        for (int i = chordNumber * 4; i < chordNumber * 4 + 4; i++) {
            audioPlayer[i] = MediaPlayer.create(this, NoteMappings.getResourceId(notes[i]));
        }
        for (int i = chordNumber * 4; i < chordNumber * 4 + 4; i++) {
            audioPlayer[i].start();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int n = chordNumber * 4; n < chordNumber * 4 + 4; n++) {
                    audioPlayer[n].stop();
                    audioPlayer[n].release();
                    audioPlayer[n] = null;
                }
                setAllRowsEnabled(true);
                getReplayButton().setEnabled(true);
                String text;
                if (chordNumber == 0) {
                    text = getResources().getString(R.string.identify_tonic_in_progression);
                } else {
                    text = String.format(getResources().getString(R.string.identify_chord_in_progression), chordNumber + 1);
                }
                getInstructionsView().setText(text);
                setReplaying(false);
            }
        }, getDelay());
    }

    /**
     * Sets the value of <code>response</code> after the user has selected a cadence.
     * The result is displayed, and the activity is reset.
     *
     * @param view The button clicked by the user: tonic, subdominant, dominant, or submediant.
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
                if (isAnswerCorrect()) {
                    if (chordNumber != answer.length - 1) {
                        chordNumber++;
                        playChord();
                    } else {
                        chordNumber = 0;
                        testUser();
                    }
                } else if (isPrefRepeat()) {
                    getInstructionsView().setText(getResources().getString(R.string.replaying));
                    playChord();
                    setAnswerCorrect(false);
                } else if (!isReplaying()) {
                    setAllRowsEnabled(true);
                    getInstructionsView().setText(getResources().getString(R.string.try_again));
                    setAnswerCorrect(false);
                }
            }
        }, 1500);
    }
}
