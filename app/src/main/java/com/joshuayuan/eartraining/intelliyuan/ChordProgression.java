package com.joshuayuan.eartraining.intelliyuan;

import java.util.Arrays;

/**
 * Represents a single chord progression from one chord to another.
 * A ChordProgression contains the associated chord names and voice leading rules.
 *
 * @author Joshua Yuan
 */
class ChordProgression {
    private int[] voiceLeading;
    private String[] chordNames;

    ChordProgression(String[] chordNames, int[] voiceLeading) {
        this.chordNames = chordNames;
        this.voiceLeading = voiceLeading;
    }

    ChordProgression getClone() {
        int[] voiceLeadingCopy = Arrays.copyOf(voiceLeading, voiceLeading.length);
        String[] chordNamesCopy = Arrays.copyOf(chordNames, chordNames.length);

        return new ChordProgression(chordNamesCopy, voiceLeadingCopy);
    }

    int[] getNotes(boolean includeFirst) {
        if (includeFirst) {
            return voiceLeading;
        } else {
            return Arrays.copyOfRange(voiceLeading, 4, voiceLeading.length);
        }
    }

    String getChordName(int chord) {
        return chordNames[chord];
    }

    void toMinor() {
        ChordExtensions.toMinor(voiceLeading);
    }

    String[] getAllChordNames(boolean includeFirst) {
        if (includeFirst) return chordNames;
        return Arrays.copyOfRange(chordNames, 1, chordNames.length);
    }

    @Override
    public boolean equals(Object chordProgression) {
        return Arrays.equals(chordNames, ((ChordProgression) chordProgression).getAllChordNames(true));
    }

    ChordProgression reverse() {
        return new ChordProgression(new String[]{chordNames[1], chordNames[0]}, null);
        // no need to implement the actual voice leading for now, perhaps need it in the future
    }
}
