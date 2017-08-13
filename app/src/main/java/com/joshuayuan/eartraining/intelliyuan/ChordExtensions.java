package com.joshuayuan.eartraining.intelliyuan;

import static com.joshuayuan.eartraining.intelliyuan.NoteMappings.MAX_NOTE;
import static com.joshuayuan.eartraining.intelliyuan.NoteMappings.MIN_NOTE;

/**
 * Helper class for Chords.
 *
 * @author Joshua Yuan
 */

public class ChordExtensions {
    static int mod(int x, int y) {
        int result = x % y;
        if (result < 0) {
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

    public static void modulateNotes(int[] notes, int semitones) {
        for (int i = 0; i < notes.length; i++) {
            notes[i] += semitones;
        }
    }

    public static int modulateNotesRand(int[] notes, int boost) {
        int min = notes[0];
        int max = notes[0];

        for (int i : notes) {
            if (i < min) min = i;
            if (i > max) max = i;
        }

        int minShift = MIN_NOTE - min + boost;
        int maxShift = MAX_NOTE - max;

        if (minShift > maxShift) {
            return Integer.MAX_VALUE;
        }

        int randomShift = minShift + (int) (Math.random() * (maxShift - minShift + 1));
        for (int i = 0; i < notes.length; i++) {
            notes[i] += randomShift;
        }

        return randomShift;
    }
}