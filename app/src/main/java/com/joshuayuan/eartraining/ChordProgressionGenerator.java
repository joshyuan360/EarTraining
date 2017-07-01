package com.joshuayuan.eartraining;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.joshuayuan.eartraining.Utilities.MAX_NOTE;
import static com.joshuayuan.eartraining.Utilities.MIN_NOTE;

/**
 * Ear Training API for chord progression generator.
 * Used for chord progression activity.
 * @author Joshua Yuan
 */

public class ChordProgressionGenerator {
    // todo: make the test "fair" for each answer, make the general results higher in pitch, keep testing
    // make sure chord never "goes back"
    // retest each progression then release!
    private static List<ChordProgression> chordProgressionToSend = new ArrayList<>();

    /** Number of chords in the current chord progression. */
    private static int SEQ_LENGTH = 5;
    /**  Template of legal, non-cadential chord progressions. */
    private static HashMap<String, List<ChordProgression>> chordProgressions = new HashMap<>();
    /**  Template of legal, cadential chord progressions. */
    private static HashMap<String, List<ChordProgression>> cadentialProgressions = new HashMap<>();

    /** API caller can get info about the last played chord progression. */
    private static String[] chordSequence = new String[SEQ_LENGTH];
    /** The array returned by the API call. */
    private static int[] notes = new int[SEQ_LENGTH * 4];

    private static boolean includeSixth;
    private static boolean includeCadential;

    public static int[] nextChordProgression() {
        for (int i = 0; i < 200; i++) {
            chordProgressionToSend.clear();
            setChordSequence();
            extractNotesAndMetaData();
        }
        modulateNotes();

        return notes;
    }

    public static String[] getChordSequence() { return chordSequence; }
    public static void includeSixth(boolean includeSixth) {
        ChordProgressionGenerator.includeSixth = includeSixth;
        chordProgressions.clear();
        initializeChordProgressions();
    }
    public static void includeCadential(boolean includeCadential) {
        ChordProgressionGenerator.includeCadential = includeCadential;
        cadentialProgressions.clear();
        initializeCadentialProgressions();
    }

    static {
        initializeChordProgressions();
        initializeCadentialProgressions();
    }

    /**  Represents a chord progression with a name and voice leading rules. */
    private static class ChordProgression {
        private int[] voiceLeading;
        private String[] chordNames;

        ChordProgression getClone() {
            int[] voiceLeadingCopy = Arrays.copyOf(voiceLeading, voiceLeading.length);
            String[] chordNamesCopy = Arrays.copyOf(chordNames, chordNames.length);

            return new ChordProgression(chordNamesCopy, voiceLeadingCopy);
        }

        ChordProgression(String[] chordNames, int[] voiceLeading) {
            this.chordNames = chordNames;
            this.voiceLeading = voiceLeading;
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
            for (int i = 0; i < voiceLeading.length; i++) {
                if (mod(voiceLeading[i] - 5, 12) == 0 || mod(voiceLeading[i] + 2, 12) == 0) {
                    voiceLeading[i]--;
                }
            }
        }

        String[] getAllChordNames(boolean includeFirst) {
            if (includeFirst) return chordNames;
            return Arrays.copyOfRange(chordNames, 1, chordNames.length);
        }

        @Override
        public boolean equals(Object chordProgression) {
            return Arrays.equals(chordNames, ((ChordProgression)chordProgression).getAllChordNames(true));
        }
    }

    private static void initializeChordProgressions() { // todo: deal with cadentials and then unit test this
        List<ChordProgression> _1r_chordProgressions = new ArrayList<>();
        List<ChordProgression> _11_chordProgressions = new ArrayList<>();
        List<ChordProgression> _4r_chordProgressions = new ArrayList<>();
        List<ChordProgression> _41_chordProgressions = new ArrayList<>();
        List<ChordProgression> _5r_chordProgressions = new ArrayList<>();
        List<ChordProgression> _51_chordProgressions = new ArrayList<>();
        List<ChordProgression> _6r_chordProgressions = new ArrayList<>();
        List<ChordProgression> _61_chordProgressions = new ArrayList<>();
        List<ChordProgression> _cad_chordProgressions = new ArrayList<>();

        _1r_chordProgressions.add(new ChordProgression(new String[] { "1r", "4r" }, new int[] { -11, -4, 5, 13, -6, -2, 6, 13 }));
        _1r_chordProgressions.add(new ChordProgression(new String[] { "1r", "41" }, new int[] { -11, -4, 5, 13, -14, -6, 6, 13 }));
        _1r_chordProgressions.add(new ChordProgression(new String[] { "1r", "5r" }, new int[] { -11, -4, 5, 13, -16, -4, 3, 12 }));
        _1r_chordProgressions.add(new ChordProgression(new String[] { "1r", "51" }, new int[] { -11, -4, 5, 13, -12, -4, 8, 15 }));

        _11_chordProgressions.add(new ChordProgression(new String[] { "11", "5r" }, new int[] { -7, 1, 8, 13, -4, 3, 8, 12 }));
        _11_chordProgressions.add(new ChordProgression(new String[] { "11", "5r" }, new int[] { -7, 1, 8, 13, -4, 0, 8, 15 }));
        _11_chordProgressions.add(new ChordProgression(new String[] { "11", "51" }, new int[] { -7, 1, 8, 13, -12, -4, 8, 15 }));
        _11_chordProgressions.add(new ChordProgression(new String[] { "11", "41" }, new int[] { -7, 1, 8, 13, -14, 1, 6, 10 }));
        _11_chordProgressions.add(new ChordProgression(new String[] { "11", "4r" }, new int[] { -7, 1, 8, 13, -6, 1, 6, 10 }));

        _4r_chordProgressions.add(new ChordProgression(new String[] { "4r", "5r" }, new int[] { -6, 1, 10, 18, -4, 0, 8, 15 }));
        _4r_chordProgressions.add(new ChordProgression(new String[] { "4r", "51" }, new int[] { -6, 1, 10, 18, -12, 0, 8, 20 }));
        _4r_chordProgressions.add(new ChordProgression(new String[] { "4r", "11" }, new int[] { -6, 1, 6, 10, -7, 1, 8, 13 }));
        _4r_chordProgressions.add(new ChordProgression(new String[] { "4r", "1r" }, new int[] { -6, -2, 6, 13, -11, -4, 5, 13 }));

        _41_chordProgressions.add(new ChordProgression(new String[] { "41", "11" }, new int[] { -14, -6, 6, 13, -11, -4, 5, 13 }));
        _41_chordProgressions.add(new ChordProgression(new String[] { "41", "11" }, new int[] { -14, 1, 6, 10, -7, 1, 8, 13 }));
        _41_chordProgressions.add(new ChordProgression(new String[] { "41", "5r" }, new int[] { -14, -6, 6, 13, -16, -4, 3, 12 }));
        _41_chordProgressions.add(new ChordProgression(new String[] { "41", "5r" }, new int[] { -14, -6, 6, 13, -16, -9, 8, 12 }));

        _5r_chordProgressions.add(new ChordProgression(new String[] { "5r", "1r" }, new int[] { -16, -4, 3, 12, -11, -4, 5, 13 }));
        _5r_chordProgressions.add(new ChordProgression(new String[] { "5r", "11" }, new int[] { -4, 3, 8, 12, -7, 1, 8, 13 }));
        _5r_chordProgressions.add(new ChordProgression(new String[] { "5r", "11" }, new int[] { -4, 0, 8, 15, -7, 1, 8, 13 }));

        _51_chordProgressions.add(new ChordProgression(new String[] { "51", "1r" }, new int[] { -12, -4, 8, 15, -11, -4, 5, 13 }));
        _51_chordProgressions.add(new ChordProgression(new String[] { "51", "11" }, new int[] { -12, -4, 8, 15, -7, 1, 8, 13 }));


        if (includeSixth) {
            _1r_chordProgressions.add(new ChordProgression(new String[] { "1r", "6r" }, new int[] { -11, -4, 5, 13, -14, -2, 5, 13 }));
            _1r_chordProgressions.add(new ChordProgression(new String[] { "1r", "61" }, new int[] { -11, -4, 5, 13, -11, -2, 5, 10 }));
            _11_chordProgressions.add(new ChordProgression(new String[] { "11", "6r" }, new int[] { -7, 1, 8, 1, -11, -2, 10, 5 }));

            _5r_chordProgressions.add(new ChordProgression(new String[] { "5r", "6r" }, new int[] { -16, 0, 8, 15, -14, -2, 5, 13 }));
            _51_chordProgressions.add(new ChordProgression(new String[] { "51", "6r" }, new int[] { -12, -4, 3, 8, -14, -7, 1, 10 }));

            _6r_chordProgressions.add(new ChordProgression(new String[] { "6r", "4r" }, new int[] { -14, -7, 1, 10, -18, -6, 1, 10 }));
            _6r_chordProgressions.add(new ChordProgression(new String[] { "6r", "41" }, new int[] { -14, -7, 1, 10, -14, -6, 1, 6 }));
            _61_chordProgressions.add(new ChordProgression(new String[] { "61", "4r" }, new int[] { -11, -2, 10, 5, -6, -2, 13, 6 }));
            _61_chordProgressions.add(new ChordProgression(new String[] { "61", "4r" }, new int[] { -11, -2, 10, 5, -6, 1, 10, 6 }));
            _61_chordProgressions.add(new ChordProgression(new String[] { "61", "41" }, new int[] { -11, -2, 10, 5, -14, 1, 6, 6 }));
            _61_chordProgressions.add(new ChordProgression(new String[] { "61", "41" }, new int[] { -11, -2, 10, 5, -14, -6, 13, 6 }));
        }

        if (includeCadential) {
            _cad_chordProgressions.add(new ChordProgression(new String[] {}, new int[] {}));

            _1r_chordProgressions.add(new ChordProgression(new String[] { "1r", "cc" }, new int[] { -11, -4, 5, 13, -16, -4, 5, 13 }));
            _11_chordProgressions.add(new ChordProgression(new String[] { "11", "cc" }, new int[] { -7, 1, 8, 13, -4, 5, 8, 13 }));
            _11_chordProgressions.add(new ChordProgression(new String[] { "11", "cc" }, new int[] { -7, 1, 8, 13, -4, 1, 8, 17 }));
            _4r_chordProgressions.add(new ChordProgression(new String[] { "4r", "cc" }, new int[] { -6, 1, 10, 18, -4, 1, 8, 17 }));
            _41_chordProgressions.add(new ChordProgression(new String[] { "41", "cc" }, new int[] { -14, -6, 6, 13, -16, -4, 5, 13 }));
            _41_chordProgressions.add(new ChordProgression(new String[] { "41", "cc" }, new int[] { -14, -6, 6, 13, -16, -7, 8, 13 }));

            _cad_chordProgressions.add(new ChordProgression(new String[] { "cc", "5r" }, new int[] { -16, -4, 5, 13, -16, -4, 3, 12 }));
        }

        chordProgressions.put("1r", _1r_chordProgressions);
        chordProgressions.put("11", _11_chordProgressions);
        chordProgressions.put("4r", _4r_chordProgressions);
        chordProgressions.put("41", _41_chordProgressions);
        chordProgressions.put("5r", _5r_chordProgressions);
        chordProgressions.put("51", _51_chordProgressions);
        chordProgressions.put("6r", _6r_chordProgressions);
        chordProgressions.put("61", _61_chordProgressions);
        chordProgressions.put("cc", _cad_chordProgressions);
    }

    private static void initializeCadentialProgressions() {
        List<ChordProgression> _1r_chordProgressions = new ArrayList<>();
        List<ChordProgression> _11_chordProgressions = new ArrayList<>();
        List<ChordProgression> _4r_chordProgressions = new ArrayList<>();
        List<ChordProgression> _41_chordProgressions = new ArrayList<>();
        List<ChordProgression> _5r_chordProgressions = new ArrayList<>();
        List<ChordProgression> _51_chordProgressions = new ArrayList<>();
        List<ChordProgression> _6r_chordProgressions = new ArrayList<>();
        List<ChordProgression> _61_chordProgressions = new ArrayList<>();

        // *** PERFECT CADENCES ***
        _5r_chordProgressions.add(new ChordProgression(new String[] { "5r", "1r" }, new int[] { 8, 8, 15, 24, 1, 8, 17, 25 }));
        _51_chordProgressions.add(new ChordProgression(new String[] { "51", "1r" }, new int[] { 0, 8, 15, 24, 1, 8, 13, 17 }));
        _5r_chordProgressions.add(new ChordProgression(new String[] { "5r", "11" }, new int[] { 8, 8, 15, 24, 5, 8, 13, 25 }));
        _5r_chordProgressions.add(new ChordProgression(new String[] { "5r", "1r" }, new int[] { -4, 8, 12, 15, 1, 5, 13, 13 }));
        _51_chordProgressions.add(new ChordProgression(new String[] { "51", "1r" }, new int[] { 0, 8, 20, 27, 1, 13, 20, 29 }));

        // TODO: must add more cadences here to ensure a cadence is always possible!!
        // *** IMPERFECT CADENCES ***
        _1r_chordProgressions.add(new ChordProgression(new String[] { "1r", "5r" }, new int[] { 1, 8, 17, 25, -4, 8, 15, 24 }));
        _1r_chordProgressions.add(new ChordProgression(new String[] { "1r", "5r" }, new int[] { 1, 17, 20, 25, -4, 12, 20, 27 }));
        _1r_chordProgressions.add(new ChordProgression(new String[] { "1r", "5r" }, new int[] { 1, 8, 17, 25, -4, 12, 20, 27 }));
        _1r_chordProgressions.add(new ChordProgression(new String[] { "1r", "5r" }, new int[] { 1, 13, 17, 20, -4, 15, 20, 24 }));
        _1r_chordProgressions.add(new ChordProgression(new String[] { "1r", "5r" }, new int[] { 1, 13, 20, 29, -4, 12, 20, 27 }));
        _11_chordProgressions.add(new ChordProgression(new String[] { "11", "51" }, new int[] { 5, 13, 20, 25, 0, 8, 20, 27 }));
        _1r_chordProgressions.add(new ChordProgression(new String[] { "1r", "51" }, new int[] { 1, 8, 17, 25, 0, 8, 20, 27 }));
        _11_chordProgressions.add(new ChordProgression(new String[] { "11", "5r" }, new int[] { 5, 13, 20, 25, 8, 12, 20, 27 }));
        _41_chordProgressions.add(new ChordProgression(new String[] { "41", "5r" }, new int[] { -14, -6, 6, 13, -16, -4, 3, 12 }));
        _41_chordProgressions.add(new ChordProgression(new String[] { "41", "5r" }, new int[] { -14, -6, 6, 13, -16, -9, 8, 12 }));
        _6r_chordProgressions.add(new ChordProgression(new String[] { "6r", "5r" }, new int[] { -14, -2, 5, 13, -16, 0, 8, 15 }));
        _6r_chordProgressions.add(new ChordProgression(new String[] { "6r", "51" }, new int[] { -14, -7, 1, 10, -12, -4, 3, 8 }));
        _61_chordProgressions.add(new ChordProgression(new String[] { "61", "5r" }, new int[] { -11, -2, 10, 17, -16, -4, 12, 15 }));
        // *** PLAGAL CADENCES ***
        _4r_chordProgressions.add(new ChordProgression(new String[] { "4r", "1r" }, new int[] { 6, 18, 22, 25, 1, 17, 20, 25 }));
        _4r_chordProgressions.add(new ChordProgression(new String[] { "4r", "1r" }, new int[] { 6, 10, 13, 18, 1, 8, 13, 17 }));

        // *** DECEPTIVE CADENCES ***
        _5r_chordProgressions.add(new ChordProgression(new String[] { "5r", "6r" }, new int[] { 8, 12, 20, 27, 10, 13, 17, 25 }));
        _5r_chordProgressions.add(new ChordProgression(new String[] { "5r", "6r" }, new int[] { 8, 12, 20, 27, 10, 13, 17, 25 }));
        _5r_chordProgressions.add(new ChordProgression(new String[] { "5r", "6r" }, new int[] { -4, 8, 12, 15, -2, 5, 13, 13 }));

        cadentialProgressions.put("1r", _1r_chordProgressions);
        cadentialProgressions.put("11", _11_chordProgressions);
        cadentialProgressions.put("4r", _4r_chordProgressions);
        cadentialProgressions.put("41", _41_chordProgressions);
        cadentialProgressions.put("5r", _5r_chordProgressions);
        cadentialProgressions.put("51", _51_chordProgressions);
        cadentialProgressions.put("6r", _6r_chordProgressions);
        cadentialProgressions.put("61", _61_chordProgressions);
    } //todo: test these

    /** Generate a pseudo-random valid chord progression. */
    private static void setChordSequence() {
        // create a chord progression
        chordProgressionToSend.add(getRandChordProgression(false, null, false));

        // generate chord progression skeleton
        for (int i = 0; i < SEQ_LENGTH - 1; i++) {
            int size = chordProgressionToSend.size();
            ChordProgression lastChord = chordProgressionToSend.get(size - 1);
            ChordProgression next = getRandChordProgression(size == SEQ_LENGTH - 1, lastChord, size == 2);

            chordProgressionToSend.add(next);
        }

        // create copy so that original array is not affected by merging and modulation
        for (int i = 0; i < chordProgressionToSend.size(); i++) {
            ChordProgression copy = chordProgressionToSend.get(i).getClone();
            chordProgressionToSend.set(i, copy);
        }

        // flip a coin and set progression to minor
        if (Math.random() < 0.5) {
            for (ChordProgression c : chordProgressionToSend) {
                c.toMinor();
            }
        }

        // resolve all merge conflicts and then check for any notes that are out of bound
        mergeProgression();
    }

    /** Find notes out of bound and adjust all notes in that voice. */
    private static boolean modulateNotes() { // todo: see if cadences will crash for bound reasons
        int min = notes[0];
        int max = notes[0];

        for (int i : notes) {
            if (i < min) min = i;
            if (i > max) max = i;
        }

        int minShift = MIN_NOTE - min;
        int maxShift = MAX_NOTE - max;

        if (minShift > maxShift) {
            Log.i("minShift", minShift+"");
            Log.i("maxShift", maxShift+"");
            return false;
        }

        int randomShift = minShift + (int) (Math.random() * (maxShift - minShift + 1));
        for (int i = 0; i < notes.length; i++) {
            notes[i] += randomShift;
        }

        return true;
    }

    private static int mod(int x, int y)
    {
        int result = x % y;
        if (result < 0)
        {
            result += y;
        }
        return result;
    }

    /** Analyze transitions between sets of chords and adjust as necessary. */
    private static boolean mergeProgression() { //todo: fix this
        Log.i("mergeProgression", "Start merging chords");
        for (int c = 0; c < chordProgressionToSend.size() - 1; c++) {
            int[] source = chordProgressionToSend.get(c).getNotes(true);
            int[] target = chordProgressionToSend.get(c + 1).getNotes(true);

            boolean[] modified = {false, false, false, false};

            for (int i = 4; i < 8; i++) {
                for (int j = 0; j < 4; j++) {
                    if (!modified[i - 4] && mod(source[i] - target[j], 12) == 0) {
                        target[j + 4] += target[j] - source[i];
                        Log.i("difference", (target[j] - source[i])+"");
                        modified[i - 4] = true;
                        break;
                    }
                }
            }

            for (boolean b : modified) {
                if (b == false) {
                    return false;
                }
            }

        }
        return true;
    }

    /** Extract and flatten chord progression metadata for the API caller to use. */
    private static void extractNotesAndMetaData() {
        List<String> metaData = new ArrayList<>();
        List<Integer> noteData = new ArrayList<>();

        for (int i = 0; i < SEQ_LENGTH - 1; i++) {
            ChordProgression c = chordProgressionToSend.get(i);
            metaData.addAll(Arrays.asList(c.getAllChordNames(i == 0)));
            int[] chordNotes = c.getNotes(i == 0);
            for (int note : chordNotes) {
                noteData.add(note);
            }
        }

        for (int i = 0; i < metaData.size(); i++) {
            switch(metaData.get(i).charAt(0)) {
                case '1':
                    chordSequence[i] = "I - TONIC";
                    break;
                case '4':
                    chordSequence[i] = "IV - SUBDOMINANT";
                    break;
                case '5':
                    chordSequence[i] = "V - DOMINANT";
                    break;
                case '6':
                    chordSequence[i] = "VI - SUBMEDIANT";
                    break;
                case 'c':
                    chordSequence[i] = "1-6-4 CADENTIAL";
            }
        }
        for (int i = 0; i < noteData.size(); i++) {
            notes[i] = noteData.get(i);
        }
        for (int i = 0; i < notes.length; i += 4) {
            Arrays.sort(notes, i, i + 4);
        }
    }

    /** Returns a random two-step chord progression that begins with the specified chord. */
    private static ChordProgression getRandChordProgression(boolean cadential, ChordProgression previous, boolean allowRootInv2) {
        List<ChordProgression> tempList;

        if (previous == null) {
            if (Math.random() < 0.5) {
                tempList = chordProgressions.get("1r");
            } else {
                tempList = chordProgressions.get("11");
            }
        } else {
            String prevStart = previous.getChordName(1);
            if (!cadential) {
                tempList = chordProgressions.get(prevStart);
            } else {
                tempList = cadentialProgressions.get(prevStart);
            }
            tempList.remove(previous);
        }

        if (tempList == null) {
            Log.i("123", "hello");
        }
        Random random = new Random();
        int randIndex;
        while (true) {
            randIndex = random.nextInt(tempList.size());
            if (!tempList.get(randIndex).getChordName(1).equals("cc") || allowRootInv2) {
                break;
            }
        }

        return tempList.get(randIndex);

    }
}
