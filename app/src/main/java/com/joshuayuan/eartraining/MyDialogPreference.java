package com.joshuayuan.eartraining;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Josh on 7/14/2016.
 */
public class MyDialogPreference extends DialogPreference { //dont need to implement DialogPreferences.OnClickListener
    //just override onClick

    public MyDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onClick(DialogInterface dialog, int which){
        if (which == DialogInterface.BUTTON_POSITIVE) {
            // do your stuff to handle positive button
            Utilities.resetHighScores = true;
            Log.i ("bb", "nbn");
        }
    }
}
