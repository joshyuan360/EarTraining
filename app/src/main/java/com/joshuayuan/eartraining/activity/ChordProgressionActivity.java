package com.joshuayuan.eartraining.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.joshuayuan.eartraining.intelliyuan.ChordProgressionGenerator;
import com.joshuayuan.eartraining.intelliyuan.NoteMappings;
import com.joshuayuan.eartraining.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.joshuayuan.eartraining.activity.PreferenceKeys.HIGH_SCORES_KEY;
import static com.joshuayuan.eartraining.activity.PreferenceKeys.PROGRESSIONS_SCORE_KEY;
import static com.joshuayuan.eartraining.activity.PreferencesActivity.SettingsFragment.PREF_CHORD_PROGRESSIONS;
import static com.joshuayuan.eartraining.activity.PreferencesActivity.SettingsFragment.PREF_PROGRESSION_TONALITY;
import static com.joshuayuan.eartraining.activity.PreferencesActivity.SettingsFragment.PREF_REPEAT;
import static com.joshuayuan.eartraining.activity.PreferencesActivity.SettingsFragment.PREF_SEQ_LENGTH;

/**
 * The chord progression activity plays a chord progression twice as per the
 * RCM syllabus. The score is based on the number of consecutive correct answers.
 *
 * @author Joshua Yuan
 */
public class ChordProgressionActivity extends AppCompatActivity {
    public String[] answer;

    private Button one, four, five, six, cadential;
    private Button replay;

    private CharSequence response;
    private boolean answerCorrect = true;
    private TextView tv;

    private int notes[];
    private int score;
    private TextView currentScore, highScore;

    private MediaPlayer[] mp;
    private Set<String> selections = new HashSet<>();
    private boolean prefRepeat;
    private Handler handler = new Handler();

    private int chordNumber = 0;
    private boolean isReplaying;
    SharedPreferences pref;

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
        currentScore = (TextView) findViewById(R.id.currentScore);
        highScore = (TextView) findViewById(R.id.chordHighestProgressionScore);

        pref = getSharedPreferences(HIGH_SCORES_KEY, Context.MODE_PRIVATE);
        highScore.setText(String.valueOf(pref.getInt(PROGRESSIONS_SCORE_KEY, 0)));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> defaultSet = new HashSet(Arrays.asList(new String[]{"six", "cadential"}));
        selections = sharedPrefs.getStringSet(PREF_CHORD_PROGRESSIONS, defaultSet);
        prefRepeat = sharedPrefs.getBoolean(PREF_REPEAT, true);

        int seqLength = Integer.parseInt(sharedPrefs.getString(PREF_SEQ_LENGTH, "5"));
        boolean includeSix = selections.contains("six");
        boolean includeCadential = selections.contains("cadential");
        int tonalityChoice = Integer.parseInt(sharedPrefs.getString(PREF_PROGRESSION_TONALITY, "3"));

        ChordProgressionGenerator.initialize(seqLength, includeSix, includeCadential, tonalityChoice);
        mp = new MediaPlayer[seqLength * 4];

        initializeButtons();
        setButtonsEnabled(false);
        replay.setEnabled(false);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                testUser();
            }
        }, 1000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        notes = ChordProgressionGenerator.nextChordProgression();
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
     *
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
        currentScore.setText(String.valueOf(score));
        setHighScores(score);
    }

    /**
     * If the current score is higher than the high score, the new high score is updated
     * in shared preferences.
     *
     * @param score The current score.
     */
    private void setHighScores(int score) {
        int hs = pref.getInt(PROGRESSIONS_SCORE_KEY, 0);

        if (hs < score) {
            hs = score;

            SharedPreferences.Editor editor = pref.edit();

            editor.putInt(PROGRESSIONS_SCORE_KEY, hs);
            editor.apply();
        }

        highScore.setText(String.valueOf(hs));
    }

    /**
     * If the last answer was correct, this method plays a newly-generated chord progression.
     * If the last answer was incorrect, this method replays the last cadence.
     */
    public void testUser() {
        if (answerCorrect) {
            setAnswer();
            playAll();
        } else if (prefRepeat) {
            playAll();
        }
    }

    /**
     * Plays the entire chord progression specified by <code>answer</code>.
     */
    private void playAll() {
        // set up UI
        setButtonsEnabled(false);
        replay.setEnabled(false);
        if (answerCorrect) {
            tv.setText(getResources().getString(R.string.playing_chord_progression));
        } else {
            tv.setText(getResources().getString(R.string.replaying));
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
        for (int i = 0; i < mp.length; i++) {
            mp[i] = MediaPlayer.create(this, NoteMappings.getResourceId(notes[i]));
        }

        for (int i = 0; i < mp.length + 4; i += 4) {
            final int start = i;

            int delay = 0;
            if (start >= mp.length) delay = 1500;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (start - 4 >= 0) {
                        for (int n = start - 4; n < start; n++) {
                            mp[n].stop();
                            mp[n].release();
                            mp[n] = null;
                        }
                    }
                    
                    if (start >= mp.length) {
                        playChord();
                        return;
                    }

                    for (int n = start; n < start + 4; n++) {
                        mp[n].start();
                    }
                }
            }, i / 4 * 1500 + delay);
        }
    }

    /**
     * Creates and fires four MediaPlayer events to play the next chord in the progression.
     * Resources are deleted immediately afterwards.
     */
    public void playChord() {
        // set up UI
        setButtonsEnabled(false);
        replay.setEnabled(false);
        if (answerCorrect) {
            String text;
            if (chordNumber == 0) {
                text = getResources().getString(R.string.playing_first_chord);
            } else {
                text = String.format(getResources().getString(R.string.playing_each_chord), chordNumber + 1);
            }
            tv.setText(text);
        } else {
            tv.setText(getResources().getString(R.string.replaying));
        }

        for (int i = chordNumber * 4; i < chordNumber * 4 + 4; i++) {
            mp[i] = MediaPlayer.create(this, NoteMappings.getResourceId(notes[i]));
        }
        for (int i = chordNumber * 4; i < chordNumber * 4 + 4; i++) {
            mp[i].start();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int n = chordNumber * 4; n < chordNumber * 4 + 4; n++) {
                    mp[n].stop();
                    mp[n].release();
                    mp[n] = null;
                }
                setButtonsEnabled(true);
                replay.setEnabled(true);
                String text;
                if (chordNumber == 0) {
                    text = getResources().getString(R.string.identify_tonic_in_progression);
                } else {
                    text = String.format(getResources().getString(R.string.identify_chord_in_progression), chordNumber + 1);
                }
                tv.setText(text);
                isReplaying = false;
            }
        }, 1500);
    }

    /**
     * Replays the last chord progression for the user.
     *
     * @param view the REPLAY button pressed.
     */
    public void replayChordProgression(View view) {
        answerCorrect = false;
        isReplaying = true;
        playChord();
    }

    /**
     * Sets the value of <code>response</code> after the user has selected a cadence.
     * The result is displayed, and the activity is reset.
     *
     * @param view The button clicked by the user: tonic, subdominant, dominant, or submediant.
     */
    public void answerClicked(View view) {
        response = ((Button) view).getText();
        setButtonsEnabled(false);
        displayResult();
        if (answerCorrect || prefRepeat) {
            replay.setEnabled(false);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (answerCorrect) {
                    if (chordNumber != answer.length - 1) {
                        chordNumber++;
                        playChord();
                    } else {
                        chordNumber = 0;
                        testUser();
                    }
                } else if (prefRepeat) {
                    tv.setText(getResources().getString(R.string.replaying));
                    playChord();
                    answerCorrect = false;
                } else if (!isReplaying) {
                    setButtonsEnabled(true);
                    tv.setText(getResources().getString(R.string.try_again));
                    answerCorrect = false;
                    score = 0;
                }
            }
        }, 1500);
    }
}
