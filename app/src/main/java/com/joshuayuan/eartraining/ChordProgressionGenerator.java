package com.joshuayuan.eartraining;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.joshuayuan.eartraining.Utilities.MAX_NOTE;
import static com.joshuayuan.eartraining.Utilities.MIN_NOTE;

/**
 * Ear Training API for chord progression generator.
 * Used for chord progression activity.
 * @author Joshua Yuan
 */

public class ChordProgressionGenerator {
    // todo: fix bad chord generation
    private static List<ChordProgression> chordProgressionToSend = new ArrayList<>();

    /** Number of chords in the current chord progression. */
    private static int SEQ_LENGTH = 5;
    /**  Template of legal, non-cadential chord progressions. */
    private static List<ChordProgression> chordProgressions = new ArrayList<>();
    /**  Template of legal, cadential chord progressions. */
    private static List<ChordProgression> cadentialProgressions = new ArrayList<>();

    /** API caller can get info about the last played chord progression. */
    private static String[] chordSequence = new String[SEQ_LENGTH];
    /** The array returned by the API call. */
    private static int[] notes = new int[SEQ_LENGTH * 4];

    private static boolean includeSixth;
    private static boolean includeCadential;

    public static int[] nextChordProgression() {
        chordProgressionToSend.clear();

        setChordSequence();
        extractNotesAndMetaData();

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

        ChordProgression(String[] chordNames, int[] voiceLeading) {
            this.chordNames = chordNames;
            this.voiceLeading = voiceLeading;
        }

        void setVoiceLeading(int[] voiceLeading) {
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

        int getNumChords() {
            return chordNames.length;
        }

        boolean canMerge(ChordProgression progression) {
            return getChordName(getNumChords() - 1).equals(progression.getChordName(0));
        }

        void toMinor() {
            Set<Integer> minorValues = new HashSet<>(Arrays.asList(-2, 5, 10, 17, 22, 29));
            for (int i = 0; i < voiceLeading.length; i++) {
                if (minorValues.contains(voiceLeading[i])) {
                    voiceLeading[i]--;
                }
            }
        }

        void modulate(int semitones) { // 0 is C maj/min
            for (int i = 0; i < voiceLeading.length; i++) {
                voiceLeading[i] += semitones;
            }
        }

        String[] getAllChordNames(boolean includeFirst) {
            if (includeFirst) return chordNames;
            return Arrays.copyOfRange(chordNames, 1, chordNames.length);
        }
    }

    private static void initializeChordProgressions() {
        chordProgressions.add(new ChordProgression(new String[] { "4r", "5r" }, new int[] { 8, 8, 15, 24, 1, 8, 17, 25 }));
        chordProgressions.add(new ChordProgression(new String[] { "5r", "4r" }, new int[] { 1, 8, 17, 25, 8, 8, 15, 24 }));

        if (includeSixth) { // include chords that LEAD to a sixth chord; chords that begin with a sixth will not merge anyway

        }
    }

    private static void initializeCadentialProgressions() {
        // *** PERFECT CADENCES ***
        cadentialProgressions.add(new ChordProgression(new String[] { "5r", "1r" }, new int[] { 8, 8, 15, 24, 1, 8, 17, 25 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "51", "1r" }, new int[] { 0, 8, 15, 24, 1, 8, 13, 17 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "5r", "11" }, new int[] { 8, 8, 15, 24, 5, 8, 13, 25 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "5r", "1r" }, new int[] { -4, 8, 12, 15, 1, 5, 13, 13 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "51", "1r" }, new int[] { 0, 8, 20, 27, 1, 13, 20, 29 }));
        // these are actually seventh chord, check syllabus for requirements
        cadentialProgressions.add(new ChordProgression(new String[] { "5r", "1r" }, new int[] { -4, 12, 15, 18, 1, 13, 13, 17 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "5r", "1r" }, new int[] { 8, 8, 18, 24, 1, 8, 17, 25 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "5r", "1r" }, new int[] { -4, 6, 12, 15, 1, 5, 8, 13 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "51", "1r" }, new int[] { 0, 6, 8, 15, 1, 5, 8, 13 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "52", "1r" }, new int[] { 3, 6, 8, 12, 1, 5, 8, 13 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "53", "11" }, new int[] { 6, 15, 20, 24, 5, 13, 20, 25 }));

        // TODO: must add more cadences here to ensure a cadence is always possible!!
        // *** IMPERFECT CADENCES ***
        cadentialProgressions.add(new ChordProgression(new String[] { "1r", "5r" }, new int[] { 1, 8, 17, 25, -4, 8, 15, 24 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "1r", "5r" }, new int[] { 1, 17, 20, 25, -4, 12, 20, 27 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "1r", "5r" }, new int[] { 1, 8, 17, 25, -4, 12, 20, 27 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "1r", "5r" }, new int[] { 1, 13, 17, 20, -4, 15, 20, 24 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "1r", "5r" }, new int[] { 1, 13, 20, 29, -4, 12, 20, 27 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "11", "51" }, new int[] { 5, 13, 20, 25, 0, 8, 20, 27 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "1r", "51" }, new int[] { 1, 8, 17, 25, 0, 8, 20, 27 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "11", "5r" }, new int[] { 5, 13, 20, 25, 8, 12, 20, 27 }));

        // *** PLAGAL CADENCES ***
        cadentialProgressions.add(new ChordProgression(new String[] { "4r", "1r" }, new int[] { 6, 18, 22, 25, 1, 17, 20, 25 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "4r", "1r" }, new int[] { 6, 10, 13, 18, 1, 8, 13, 17 }));

        // *** DECEPTIVE CADENCES ***
        cadentialProgressions.add(new ChordProgression(new String[] { "1r", "6r" }, new int[] { 8, 12, 20, 27, 10, 13, 17, 25 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "1r", "6r" }, new int[] { 8, 12, 20, 27, 10, 13, 17, 25 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "5r", "6r" }, new int[] { -4, 8, 12, 15, -2, 5, 13, 13 }));
        cadentialProgressions.add(new ChordProgression(new String[] { "5r", "6r" }, new int[] { -4, 6, 12, 15, -2, 5, 13, 13 })); // TODO: V7 here, what to do?

        if (includeCadential) {

        }
    }

    /** Generate a pseudo-random valid chord progression. */
    private static void setChordSequence() {
        // create a chord progression
        chordProgressionToSend.add(getRandChordProgression(false));
        while (chordProgressionToSend.size() != SEQ_LENGTH - 1) {
            chordProgressionToSend.add(getRandChordProgression(chordProgressionToSend.size() == SEQ_LENGTH - 1));

            int lastIndex = chordProgressionToSend.size() - 1;
            if (!chordProgressionToSend.get(lastIndex).canMerge(chordProgressionToSend.get(lastIndex - 1))) {
                chordProgressionToSend.remove(lastIndex);
            }
        }

        // flip a coin and set progression to minor
        if (Math.random() < 0.5) {
            for (ChordProgression c : chordProgressionToSend) {
                c.toMinor();
            }
        }

        // add variety to the chord progression; change key
        Random rand = new Random();
        int delta = rand.nextInt(12) - 6; // careful here, check bounds
        for (ChordProgression c : chordProgressionToSend) {
            c.modulate(delta);
        }

        // resolve all merge conflicts and then check for any notes that are out of bound
        boolean mergeSuccess = mergeProgression();
        boolean verifySuccess = verifyCorrectness();
        if (!mergeSuccess || !verifySuccess) {
            setChordSequence();
        }
    }

    /** Find notes out of bound and adjust all notes in that voice. */
    private static boolean verifyCorrectness() {
        Log.i("verifyCorrectness", "Start progression verification");
        int[] voiceShift = {0, 0, 0, 0};

        for (int i = 0; i < SEQ_LENGTH - 1; i++) {
            int[] chordNotes = chordProgressionToSend.get(i).getNotes(true);
            for (int n = 0; n < chordNotes.length; n++) {
                if (chordNotes[n] > MAX_NOTE) {
                    if (voiceShift[n % 4] != 0 && voiceShift[n % 4] != -12) return false;
                    voiceShift[n % 4] = -12;
                } else if (chordNotes[n] < MIN_NOTE) {
                    if (voiceShift[n % 4] != 0 && voiceShift[n % 4] != 12) return false;
                    voiceShift[n % 4] = 12;
                }
            }
        }

        for (int i = 0; i < SEQ_LENGTH - 1; i++) {
            ChordProgression progression = chordProgressionToSend.get(i);
            int[] notes = progression.getNotes(true);
            for (int n = 0; n < notes.length; n++) {
                notes[n] += voiceShift[n % 4];
            }
        }

        Log.i("verifyCorrectness", "End progression verification");
        return true; // should be false if impossible to fix problem
    }

    /** Analyze transitions between sets of chords and adjust as necessary. */
    private static boolean mergeProgression() {
        Log.i("mergeProgression", "Start merging chords");
        int[] mergeShift = {-1, -1, -1, -1};

        for (int i = 0; i < SEQ_LENGTH - 2; i++) {
            ChordProgression source = chordProgressionToSend.get(i);
            ChordProgression target = chordProgressionToSend.get(i + 1);
            int[] sourceNotes = source.getNotes(true);
            int[] targetNotes = target.getNotes(true);

            int[] sourceNotesLast = Arrays.copyOfRange(sourceNotes, sourceNotes.length - 4, sourceNotes.length);
            int[] targetNotesFirst = Arrays.copyOfRange(targetNotes, 0, 4);

            for (int n = 0; n < 4; n++) {
                int note1 = sourceNotesLast[n];
                for (int m = 0; m < 4; m++) {
                    int note2 = targetNotesFirst[m];
                    if (note2 != Integer.MAX_VALUE && note1 % 12 == note2 % 12) {
                        mergeShift[n] = note2 - note1;
                        targetNotesFirst[m] = Integer.MAX_VALUE;
                        break;
                    }
                }
            }
        }

        for (int i : mergeShift) {
            if (i == -1) return false;
        }

        for (int i = 0; i < SEQ_LENGTH - 1; i++) {
            ChordProgression progression = chordProgressionToSend.get(i);
            int[] notes = progression.getNotes(true);
            for (int n = 0; n < notes.length; n++) {
                notes[n] += mergeShift[n % 4];
            }
        }
        Log.i("mergeProgression", "Finish merging chords");
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

    /** Returns a random two-step chord progression. */
    private static ChordProgression getRandChordProgression(boolean cadential) {
        Random random = new Random();
        int randIndex;

        if (cadential) {
            randIndex = random.nextInt(cadentialProgressions.size());
            return cadentialProgressions.get(randIndex);
        } else {
            randIndex = random.nextInt(chordProgressions.size());
            return chordProgressions.get(randIndex);
        }
    }
}
