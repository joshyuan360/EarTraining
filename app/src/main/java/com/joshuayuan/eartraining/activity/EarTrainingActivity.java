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
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.joshuayuan.eartraining.R;

import static com.joshuayuan.eartraining.activity.PreferenceKeys.CONTROLS_KEY;

public abstract class EarTrainingActivity extends AppCompatActivity {
    public TextView instructions, currentScore, highScore;
    public Button replay;

    public MediaPlayer[] mp = null;

    public Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervals);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loadTextViews();
        loadPreferences();
        loadButtons();

        replay.setEnabled(false);
        setAllRowsEnabled(false);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        initVolumeSeekBar();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                testUser();
            }
        }, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        for (int i = 0; i < mp.length; i++) {
            if (mp[i] != null) {
                mp[i].stop();
                mp[i].release();
                mp[i] = null;
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

    private void initVolumeSeekBar()
    {
        SeekBar volumeSeekBar = (SeekBar)findViewById(R.id.intervals_volume);
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        volumeSeekBar.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeSeekBar.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));


        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {}

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {}

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
            {
                audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        progress, 0);
            }
        });
    }

    protected void initSpeedSeekBar(final String key)
    {
        SeekBar speedSeekBar = (SeekBar)findViewById(R.id.intervals_speed);
        final SharedPreferences sharedPref = getSharedPreferences(CONTROLS_KEY, MODE_PRIVATE);

        speedSeekBar.setMax(3000);
        speedSeekBar.setProgress(3000 - sharedPref.getInt(key, MODE_PRIVATE));

        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar speedBar)
            {
                int targetSpeed = (3000 - speedBar.getProgress());

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(key, targetSpeed);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {}
            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {}
        });
    }

    protected abstract void testUser();
    protected abstract void loadTextViews();
    protected abstract void loadPreferences();
    protected abstract void loadButtons();
    protected abstract void setAllRowsEnabled(boolean enabled);
}
