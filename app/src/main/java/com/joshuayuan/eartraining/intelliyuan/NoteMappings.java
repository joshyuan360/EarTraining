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

import android.util.SparseIntArray;

import com.joshuayuan.eartraining.R;

public class NoteMappings {
    public static int MAX_NOTE = -11 + 49 - 1;
    public static int MIN_NOTE = -11;

    private static SparseIntArray noteMap = new SparseIntArray();

    static {
        int key = MIN_NOTE;

        noteMap.append(key++, R.raw.a1);
        noteMap.append(key++, R.raw.bb1);
        noteMap.append(key++, R.raw.b1);

        noteMap.append(key++, R.raw.c2);
        noteMap.append(key++, R.raw.db2);
        noteMap.append(key++, R.raw.d2);
        noteMap.append(key++, R.raw.eb2);
        noteMap.append(key++, R.raw.e2);
        noteMap.append(key++, R.raw.f2);
        noteMap.append(key++, R.raw.gb2);
        noteMap.append(key++, R.raw.g2);
        noteMap.append(key++, R.raw.ab2);
        noteMap.append(key++, R.raw.a2);
        noteMap.append(key++, R.raw.bb2);
        noteMap.append(key++, R.raw.b2);

        noteMap.append(key++, R.raw.c3);
        noteMap.append(key++, R.raw.db3);
        noteMap.append(key++, R.raw.d3);
        noteMap.append(key++, R.raw.eb3);
        noteMap.append(key++, R.raw.e3);
        noteMap.append(key++, R.raw.f3);
        noteMap.append(key++, R.raw.gb3);
        noteMap.append(key++, R.raw.g3);
        noteMap.append(key++, R.raw.ab3);
        noteMap.append(key++, R.raw.a3);
        noteMap.append(key++, R.raw.bb3);
        noteMap.append(key++, R.raw.b3);

        noteMap.append(key++, R.raw.c4);
        noteMap.append(key++, R.raw.db4);
        noteMap.append(key++, R.raw.d4);
        noteMap.append(key++, R.raw.eb4);
        noteMap.append(key++, R.raw.e4);
        noteMap.append(key++, R.raw.f4);
        noteMap.append(key++, R.raw.gb4);
        noteMap.append(key++, R.raw.g4);
        noteMap.append(key++, R.raw.ab4);
        noteMap.append(key++, R.raw.a4);
        noteMap.append(key++, R.raw.bb4);
        noteMap.append(key++, R.raw.b4);

        noteMap.append(key++, R.raw.c5);
        noteMap.append(key++, R.raw.db5);
        noteMap.append(key++, R.raw.d5);
        noteMap.append(key++, R.raw.eb5);
        noteMap.append(key++, R.raw.e5);
        noteMap.append(key++, R.raw.f5);
        noteMap.append(key++, R.raw.gb5);
        noteMap.append(key++, R.raw.g5);
        noteMap.append(key++, R.raw.ab5);
        noteMap.append(key, R.raw.a5);
    }

    public static int getResourceId(int note) {
        return noteMap.get(note);
    }
}
