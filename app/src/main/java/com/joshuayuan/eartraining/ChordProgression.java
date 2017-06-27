package com.joshuayuan.eartraining;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
 * The chord progression activity plays a chord progression twice as per the
 * RCM syllabus. The score is based on the number of consecutive correct answers.
 * @author Joshua Yuan
 */
public class ChordProgression extends AppCompatActivity {
    /** A <code>Button</code> object in the cadences activity. */
    private Button one, four, five, six, cadential;
    /** The sequence of chords that are currently being played. */
    public String[] answer = new String[5 * 4];
    /** Allows the user to replay the last chord progression. */
    private Button replay;
    /** User input for the last chord played. */
    private CharSequence response;
    /** <code>true</code> if the correct chord is identified. */
    private boolean answerCorrect = true;
    /** Displays info to the user on screen. */
    private TextView tv;
    /** Contains all of the notes in the chord progression that are to be played. */
    private int notes[];
    /** The current score of the user */
    private int score;
    /** Displays the user's high score. */
    private TextView hs;
    /** Contains the sound files required to play the cadence. */
    private final MediaPlayer[] mp = new MediaPlayer[20];
    private MediaPlayer tonic = new MediaPlayer();
    /** The chords that the user wishes to be tested on. */
    private Set<String> selections = new HashSet<>();
    /** <code>true</code> if the user wants automatic replays. */
    private boolean prefRepeat;
    /** Used to play sound after a specified amount of time. */
    private Handler handler = new Handler();
    private boolean isReplaying;
    /** The index of the next chord that will be played, starting from zero. */
    private int chordNumber = 0;

    /**
     * Initializes the <code>Button</code> fields and begins the test.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Chord Progression");
        setContentView(R.layout.activity_chord_progression);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tv = (TextView) findViewById(R.id.chordProgressionText);
        hs = (TextView) findViewById(R.id.chordProgressionScore);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> defaultSet = new HashSet(Arrays.asList(new String[] { "six", "cadential" }));
        selections = sharedPrefs.getStringSet("pref_chord_progressions", defaultSet);
        prefRepeat = sharedPrefs.getBoolean("pref_repeat", true);

        ChordProgressionGenerator.includeSixth(selections.contains("six"));
        ChordProgressionGenerator.includeCadential(selections.contains("cadential"));

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
        for (int i = 0; i < mp.length; i++) {
            if (mp[i] != null) {
                mp[i].release();
                mp[i] = null;
            }
        }
    }

    /**
     * Generates a new random chord progression and stores it in <code>notes</code>
     * and <code>answer</code>. Method is invoked only when the last answer provided is correct.
     * Chord Progression API handles random chord generation.
     */
    public void setAnswer() {
        notes = ChordProgressionGenerator.nextChordProgression(); //todo: do not generate chords they don't want (stabilize)
        answer = ChordProgressionGenerator.getChordSequence();
    }

    /**
     * Loads all the button fields.
     */
    private void initializeButtons() {
        one = (Button) findViewById(R.id.one);
        four = (Button) findViewById(R.id.four);
        five = (Button) findViewById(R.id.five);
        six = (Button) findViewById(R.id.six);
        cadential = (Button) findViewById((R.id.cadential));
        replay = (Button) findViewById(R.id.replay);
    }

    /**
     * Enables or disables the answer buttons.
     * @param enabled Controls the perfect, plagal, imperfect, and deceptive buttons.
     */
    private void setButtonsEnabled(boolean enabled) {
        one.setEnabled(enabled);
        four.setEnabled(enabled);
        five.setEnabled(enabled);

        // look into this
        six.setEnabled(enabled && selections.contains("six"));
        cadential.setEnabled(enabled && selections.contains("cadential"));
    }

    /**
     * Displays the result of the user's input as "Correct!" or "Try Again!"
     * The score is either incremented (if correct) or reset to zero (if incorrect).
     */
    private void displayResult() {
        if (response.equals(answer[chordNumber])) {
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
        if (pref.getInt("cphs", 0) < score) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("cphs", score);
            editor.apply();
        }
    }

    /**
     * If the last answer was correct, this method plays a newly-generated chord progression.
     * If the last answer was incorrect, this method replays the last cadence.
     */
    public void testUser() {
        if (answerCorrect) {
            setAnswer();
            playAll();
        } else if (prefRepeat){
            playAll();
            //playAnswer(true);
        }
    }

    /**
     * Plays the entire chord progression specified by <code>answer</code>.
     * When playing a new progression, the five chords are pseudo-randomly generated.
     */
    private void playAll() {
        // set up UI
        setButtonsEnabled(false);
        replay.setEnabled(false);
        if (answerCorrect) {
            tv.setText("Playing tonic note and chord progression..."); // TODO: use string resources
        } else {
            tv.setText(getResources().getString(R.string.replaying));
        }

        for (int i = 0; i < 20; i++) {
            mp[i] = MediaPlayer.create(this, Utilities.getResourceId(notes[i]));
        }

        firePlayer(0);
    }

    public void firePlayer(final int start) {
        Log.i("firePlayer", "start: " + start);
        if (start == 20) {
            tv.setText("Playing each chord individually...");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playChord();
                }
            }, 1000);
            return;
        }

        for (int n = start; n < start + 4; n++) {
            mp[n].start();
        }

        mp[start + 3].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer med) {
                for (int n = start; n < start + 4; n++) {
                    mp[n].stop();
                    mp[n].release();
                    mp[n] = null;
                }
                firePlayer(start + 4);
            }
        });
    }

    public void playChord() {
        for (int i = 0; i < 20; i++) {
            mp[i] = MediaPlayer.create(this, Utilities.getResourceId(notes[i]));
        }
        for (int i = chordNumber * 4; i < chordNumber * 4 + 4; i++) {
            mp[i].start();
        }
        mp[chordNumber * 4 + 3].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer med) {
                for (int n = chordNumber * 4; n < chordNumber * 4 + 4; n++) {
                    mp[n].stop();
                    mp[n].release();
                    mp[n] = null;
                }
                setButtonsEnabled(true);
                replay.setEnabled(true);
            }
        });
    }

    /**
     * Replays the last chord progression for the user.
     * @param view the REPLAY button pressed.
     */
    public void replayChordProgression(View view) {
        answerCorrect = false;
        isReplaying = true;
        replay.setEnabled(false);
        playChord();
    }

    public void answerClicked(View view) {
        response = ((Button)view).getText();
        setButtonsEnabled(false);
        replay.setEnabled(false);
        displayResult();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (answerCorrect) {
                    if (chordNumber != 4) {
                        chordNumber++;
                        playChord();
                    } else {
                        chordNumber = 0;
                        testUser();
                    }
                } else if (prefRepeat) {
                    playChord();
                    answerCorrect = false;
                } else if (!isReplaying) {
                    tv.setText(getResources().getString(R.string.incorrect));
                    answerCorrect = false;
                    score = 0;
                }
            }
        }, 1500);
    }
}
