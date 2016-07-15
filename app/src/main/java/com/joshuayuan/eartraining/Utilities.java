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
package com.joshuayuan.eartraining;

/**
 * Used in multiple classes within the com.joshuayuan package.
 * @author Joshua Yuan
 */
public class Utilities {
    public static boolean resetHighScores = false;

    /**
     * Returns the resource ID of the requested note.
     * @param note The note to be played.
     * @return The integer ID of the note specified by the parameter.
     */
    public static int getResourceId(int note) {
        if (note == 1) return R.raw.n1;
        if (note == 2) return R.raw.n2;
        if (note == 3) return R.raw.n3;
        if (note == 4) return R.raw.n4;
        if (note == 5) return R.raw.n5;
        if (note == 6) return R.raw.n6;
        if (note == 7) return R.raw.n7;
        if (note == 8) return R.raw.n8;
        if (note == 9) return R.raw.n9;
        if (note == 10) return R.raw.n10;
        if (note == 11) return R.raw.n11;
        if (note == 12) return R.raw.n12;
        if (note == 13) return R.raw.n13;
        if (note == 14) return R.raw.n14;
        if (note == 15) return R.raw.n15;
        if (note == 16) return R.raw.n16;
        if (note == 17) return R.raw.n17;
        if (note == 18) return R.raw.n18;
        if (note == 19) return R.raw.n19;
        if (note == 20) return R.raw.n20;
        if (note == 21) return R.raw.n21;
        if (note == 22) return R.raw.n22;
        if (note == 23) return R.raw.n23;
        if (note == 24) return R.raw.n24;
        if (note == 25) return R.raw.n25;
        if (note == 26) return R.raw.n26;
        if (note == 27) return R.raw.n27;
        if (note == 28) return R.raw.n28;
        if (note == 0) return R.raw.low0;
        if (note == -1) return R.raw.low1;
        if (note == -2) return R.raw.low2;
        if (note == -3) return R.raw.low3;
        if (note == -4) return R.raw.low4;
        if (note == -5) return R.raw.low5;
        if (note == -6) return R.raw.low6;
        if (note == -7) return R.raw.low7;
        if (note == -8) return R.raw.low8;
        if (note == -9) return R.raw.low9;
        if (note == -10) return R.raw.low10;
        return R.raw.low11;
    }

}
