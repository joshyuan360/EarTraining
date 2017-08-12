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
package com.joshuayuan.eartraining.intelliyuan;

import com.joshuayuan.eartraining.R;

public class NoteMappings {
    public static int MAX_NOTE = 28;
    public static int MIN_NOTE = -11;

    /**
     * Returns the resource ID of the requested note.
     *
     * @param note The note to be played.
     * @return The integer ID of the note specified by the parameter.
     */
    public static int getResourceId(int note) {
        switch (note) {
            case 1:
                return R.raw.n1;
            case 2:
                return R.raw.n2;
            case 3:
                return R.raw.n3;
            case 4:
                return R.raw.n4;
            case 5:
                return R.raw.n5;
            case 6:
                return R.raw.n6;
            case 7:
                return R.raw.n7;
            case 8:
                return R.raw.n8;
            case 9:
                return R.raw.n9;
            case 10:
                return R.raw.n10;
            case 11:
                return R.raw.n11;
            case 12:
                return R.raw.n12;
            case 13:
                return R.raw.n13;
            case 14:
                return R.raw.n14;
            case 15:
                return R.raw.n15;
            case 16:
                return R.raw.n16;
            case 17:
                return R.raw.n17;
            case 18:
                return R.raw.n18;
            case 19:
                return R.raw.n19;
            case 20:
                return R.raw.n20;
            case 21:
                return R.raw.n21;
            case 22:
                return R.raw.n22;
            case 23:
                return R.raw.n23;
            case 24:
                return R.raw.n24;
            case 25:
                return R.raw.n25;
            case 26:
                return R.raw.n26;
            case 27:
                return R.raw.n27;
            case 28:
                return R.raw.n28;
            case 0:
                return R.raw.low0;
            case -1:
                return R.raw.low1;
            case -2:
                return R.raw.low2;
            case -3:
                return R.raw.low3;
            case -4:
                return R.raw.low4;
            case -5:
                return R.raw.low5;
            case -6:
                return R.raw.low6;
            case -7:
                return R.raw.low7;
            case -8:
                return R.raw.low8;
            case -9:
                return R.raw.low9;
            case -10:
                return R.raw.low10;
            case -11:
                return R.raw.low11;
            default:
                return -1000;
        }
    }

}
