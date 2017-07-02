package com.joshuayuan.eartraining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    // todo: should i play tonic chord once in the very beginning? check syllabus
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

    public static int[] nextChordProgression() { //todo: initial message is wrong
        do {
            chordProgressionToSend.clear();
            setChordSequence();
            extractNotesAndMetaData();
        } while (!modulateNotes());

        return notes;
    }

    public static String[] getChordSequence() { return chordSequence; }

    public static void initialize(boolean includeSixth, boolean includeCadential) {
        chordProgressions.clear();
        cadentialProgressions.clear();

        VoiceLeadingRules.initializeChordProgressions(chordProgressions);
        VoiceLeadingRules.initializeCadentialProgressions(cadentialProgressions);

        if (includeSixth) {
            VoiceLeadingRules.includeSixth(chordProgressions);
        }
        if (includeCadential) {
            VoiceLeadingRules.includeCadential(chordProgressions);
        }
    }

    /** Generate a pseudo-random valid chord progression. */
    private static void setChordSequence() {
        // create a chord progression
        chordProgressionToSend.add(getRandChordProgression(false, null, false));

        // generate chord progression skeleton
        for (int i = 0; i < SEQ_LENGTH - 2; i++) {
            int size = chordProgressionToSend.size();
            ChordProgression lastChord = chordProgressionToSend.get(size - 1);
            ChordProgression next = getRandChordProgression(size == SEQ_LENGTH - 2, lastChord, size == 1);

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

        // resolve all merge conflicts
        mergeProgression();
    }

    /** Find notes out of bound and adjust all notes in that voice. */
    private static boolean modulateNotes() {
        int min = notes[0];
        int max = notes[0];

        for (int i : notes) {
            if (i < min) min = i;
            if (i > max) max = i;
        }

        int boost = 5; // chord progression may be too low otherwise
        int minShift = MIN_NOTE - min + boost;
        int maxShift = MAX_NOTE - max;

        if (minShift > maxShift) {
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
    private static boolean mergeProgression() {
        for (int c = 0; c < chordProgressionToSend.size() - 1; c++) {
            int[] source = chordProgressionToSend.get(c).getNotes(true);
            int[] target = chordProgressionToSend.get(c + 1).getNotes(true);

            boolean[] modified = {false, false, false, false};

            for (int i = 4; i < 8; i++) {
                for (int j = 0; j < 4; j++) {
                    if (!modified[j] && mod(source[i] - target[j], 12) == 0) {
                        target[j + 4] += source[i] - target[j];
                        modified[j] = true;
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
                tempList.removeAll(Collections.singleton(previous.reverse()));
            } else {
                tempList = cadentialProgressions.get(prevStart);
            }
        }

        // deep copy so original array is unaffected by deletion operations
        List<ChordProgression> listCopy = new ArrayList<>();
        for (ChordProgression c : tempList) {
            listCopy.add(c.getClone());
        }

        if (!allowRootInv2) {
            List<ChordProgression> rootInv2 = new ArrayList<>();
            for (ChordProgression c : tempList) {
                if (c.getChordName(1).equals("cc")) {
                    rootInv2.add(c);
                }
            }
            listCopy.removeAll(rootInv2);
        }

        Random random = new Random();
        int randIndex = random.nextInt(listCopy.size());

        return listCopy.get(randIndex);
    }
}
