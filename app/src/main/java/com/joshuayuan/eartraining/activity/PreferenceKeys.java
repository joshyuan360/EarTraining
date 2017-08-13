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

/**
 * Shared preferences keys for high scores.
 *
 * @author Joshua Yuan
 */
public class PreferenceKeys {
    // do not change these; they will affect existing app users
    static final String HIGH_SCORES_KEY = "high scores";
    static final String CONTROLS_KEY = "controls";

    static final String INTERVALS_SCORE_KEY = "ihs";
    static final String CHORDS_SCORE_KEY = "chhs";
    static final String CADENCES_SCORE_KEY = "cahs";
    static final String PROGRESSIONS_SCORE_KEY = "cphs";

    static final String INTERVALS_SPEED_KEY = "intervals_speed";
    static final String CHORDS_SPEED = "chords_speed";
    static final String CADENCES_SPEED = "cadences_speed";
    static final String PROGRESSIONS_SPEED = "progressions_speed";
}
