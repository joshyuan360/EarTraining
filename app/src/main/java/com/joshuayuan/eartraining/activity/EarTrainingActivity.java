package com.joshuayuan.eartraining.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.joshuayuan.eartraining.R;

import java.util.Set;

import static com.joshuayuan.eartraining.activity.PreferenceKeys.CONTROLS_KEY;

public abstract class EarTrainingActivity extends AppCompatActivity {
    protected MediaPlayer[] audioPlayer = null;
    protected Handler handler = new Handler();
    private SharedPreferences highScoresPref;
    private TextView instructionsView;
    private TextView currentScoreView;
    private TextView highScoreView;
    private Button replayButton;
    private boolean answerCorrect = true;
    private boolean prefRepeat;
    private boolean isReplaying;
    private int score;
    private String highScoresKey;
    private Set<String> userSelections;
    private String speedKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    protected void onCreateEarTrainingActivity(
            String scoreKey, int volumeSeekBarId,
            int speedSeekBarId, String speedKey) {
        setHighScoresKey(scoreKey);
        initVolumeSeekBar(volumeSeekBarId);
        this.speedKey = speedKey;

        loadTextViews();
        loadSelectionsAndPreferences();
        loadButtons();

        initSpeedSeekBar(speedSeekBarId, speedKey);

        getReplayButton().setEnabled(false);
        setAllRowsEnabled(false);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                testUser();
            }
        }, 1000);
    }

    protected void loadAudioPlayer(int numPlayers) {
        audioPlayer = new MediaPlayer[numPlayers];
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        for (int i = 0; i < audioPlayer.length; i++) {
            if (audioPlayer[i] != null) {
                audioPlayer[i].stop();
                audioPlayer[i].release();
                audioPlayer[i] = null;
            }
        }
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

    private void initVolumeSeekBar(int seekBarId) {
        SeekBar volumeSeekBar = (SeekBar) findViewById(seekBarId);
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        volumeSeekBar.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeSeekBar.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));


        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        progress, 0);
            }
        });
    }

    private void initSpeedSeekBar(int seekBarId, final String key) {
        SeekBar speedSeekBar = (SeekBar) findViewById(seekBarId);
        final SharedPreferences sharedPref = getSharedPreferences(CONTROLS_KEY, MODE_PRIVATE);

        speedSeekBar.setMax(3000);
        speedSeekBar.setProgress(3000 - sharedPref.getInt(key, 1500));

        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar speedBar) {
                int targetSpeed = (3000 - speedBar.getProgress());

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(key, targetSpeed);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
            }
        });
    }

    protected void testUser() {
        if (isAnswerCorrect()) {
            setAnswer();
            playAnswer();
        } else if (isPrefRepeat()) {
            playAnswer();
        }
    }

    protected void setHighScores() {
        int savedHighScore = getHighScoresPref().getInt(getHighScoresKey(), 0);

        if (savedHighScore < score) {
            savedHighScore = score;

            SharedPreferences.Editor editor = getHighScoresPref().edit();

            editor.putInt(getHighScoresKey(), savedHighScore);
            editor.apply();
        }

        getHighScoreView().setText(String.valueOf(savedHighScore));
    }

    protected void displayResult() {
        if (answerCorrect()) {
            getInstructionsView().setText(getResources().getString(R.string.correct));
            setAnswerCorrect(true);
            score++;
        } else {
            getInstructionsView().setText(getResources().getString(R.string.incorrect));
            setAnswerCorrect(false);
            score = 0;
        }
        getCurrentScoreView().setText(String.valueOf(score));
        setHighScores();
    }

    public void replayTest(View view) {
        setAnswerCorrect(false);
        setReplaying(true);
        playAnswer();
    }

    protected int getDelay() {
        return getSharedPreferences(CONTROLS_KEY, MODE_PRIVATE)
                .getInt(speedKey, 1500);
    }

    protected abstract boolean answerCorrect();

    protected abstract void setAnswer();

    protected abstract void playAnswer();

    protected abstract void loadTextViews();

    protected abstract void loadSelectionsAndPreferences();

    protected abstract void loadButtons();

    protected abstract void setAllRowsEnabled(boolean enabled);

    protected String getHighScoresKey() {
        return highScoresKey;
    }

    protected void setHighScoresKey(String highScoresKey) {
        this.highScoresKey = highScoresKey;
    }

    protected TextView getInstructionsView() {
        return instructionsView;
    }

    protected void setInstructionsView(TextView instructionsView) {
        this.instructionsView = instructionsView;
    }

    protected TextView getCurrentScoreView() {
        return currentScoreView;
    }

    protected void setCurrentScoreView(TextView currentScoreView) {
        this.currentScoreView = currentScoreView;
    }

    protected TextView getHighScoreView() {
        return highScoreView;
    }

    protected void setHighScoreView(TextView highScoreView) {
        this.highScoreView = highScoreView;
    }

    protected Button getReplayButton() {
        return replayButton;
    }

    protected void setReplayButton(Button replayButton) {
        this.replayButton = replayButton;
    }

    protected Set<String> getUserSelections() {
        return userSelections;
    }

    protected void setUserSelections(Set<String> userSelections) {
        this.userSelections = userSelections;
    }

    protected boolean isAnswerCorrect() {
        return answerCorrect;
    }

    protected void setAnswerCorrect(boolean answerCorrect) {
        this.answerCorrect = answerCorrect;
    }

    protected boolean isPrefRepeat() {
        return prefRepeat;
    }

    protected void setPrefRepeat(boolean prefRepeat) {
        this.prefRepeat = prefRepeat;
    }

    protected boolean isReplaying() {
        return isReplaying;
    }

    protected void setReplaying(boolean replaying) {
        isReplaying = replaying;
    }

    protected SharedPreferences getHighScoresPref() {
        return highScoresPref;
    }

    protected void setHighScoresPref(SharedPreferences highScoresPref) {
        this.highScoresPref = highScoresPref;
    }
}
