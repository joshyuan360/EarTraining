package com.joshuayuan.eartraining.intelliyuan;

/**
 * Helper class for Chords.
 * @author Joshua Yuan
 */

class ChordExtensions {
    static int mod(int x, int y)
    {
        int result = x % y;
        if (result < 0)
        {
            result += y;
        }
        return result;
    }

    static void toMinor(int[] voiceLeading) {
        for (int i = 0; i < voiceLeading.length; i++) {
            if (mod(voiceLeading[i] - 5, 12) == 0 || mod(voiceLeading[i] + 2, 12) == 0) {
                voiceLeading[i]--;
            }
        }
    }
}