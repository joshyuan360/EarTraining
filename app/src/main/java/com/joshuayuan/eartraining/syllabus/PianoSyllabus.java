package com.joshuayuan.eartraining.syllabus;

import android.util.SparseArray;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * RCM piano syllabus requirements.
 */
public class PianoSyllabus {
    private static SparseArray<String[]> levelToIntervalMap;
    private static SparseArray<String> levelToIntervalTypeMap;
    private static SparseArray<String[]> levelToChordsMap;
    private static SparseArray<String> levelToChordTypeMap;
    private static SparseArray<String[]> levelToCadencesMap;
    private static SparseArray<String[]> levelToProgressionsMap;
    private static SparseArray<String> levelToProgressionLengthMap;
    private static SparseArray<String> levelToProgressionTonalityMap;
    // todo: this is piano only, add instrumental (release 6.5) and use string resources
    static {
        levelToIntervalMap = new SparseArray<>();
        levelToIntervalTypeMap = new SparseArray<>();
        levelToChordsMap = new SparseArray<>();
        levelToChordTypeMap = new SparseArray<>();
        levelToCadencesMap = new SparseArray<>();
        levelToProgressionsMap = new SparseArray<>();
        levelToProgressionLengthMap = new SparseArray<>();
        levelToProgressionTonalityMap = new SparseArray<>();

        levelToIntervalMap.append(1, new String[] { });
        levelToIntervalTypeMap.append(1, "4");
        levelToChordsMap.append(1, new String[] { });
        levelToChordTypeMap.append(1, "2");
        levelToCadencesMap.append(1, new String [] { });
        levelToProgressionsMap.append(1, new String[] {  });
        levelToProgressionLengthMap.append(1, "3" );
        levelToProgressionTonalityMap.append(1, "1");

        levelToIntervalMap.append(2, new String[] { "Perfect Fifth" });
        levelToIntervalTypeMap.append(2, "4");
        levelToChordsMap.append(2, new String[] { });
        levelToChordTypeMap.append(2, "1");
        levelToCadencesMap.append(2, new String [] { });
        levelToProgressionsMap.append(2, new String[] {  });
        levelToProgressionLengthMap.append(2, "3" );
        levelToProgressionTonalityMap.append(2, "1");

        levelToIntervalMap.append(3, new String[] { "Perfect Fourth", "Perfect Fifth" });
        levelToIntervalTypeMap.append(3, "4");
        levelToChordsMap.append(3, new String[] { });
        levelToChordTypeMap.append(3, "1");
        levelToCadencesMap.append(3, new String [] { });
        levelToProgressionsMap.append(3, new String[] {  });
        levelToProgressionLengthMap.append(3, "3" );
        levelToProgressionTonalityMap.append(3, "1");

        levelToIntervalMap.append(4, new String[] { "Perfect Fourth", "Perfect Fifth", "Perfect Octave" });
        levelToIntervalTypeMap.append(4, "4");
        levelToChordsMap.append(4, new String[] { });
        levelToChordTypeMap.append(4, "1");
        levelToCadencesMap.append(4, new String [] { });
        levelToProgressionsMap.append(4, new String[] {  });
        levelToProgressionLengthMap.append(4, "3" );
        levelToProgressionTonalityMap.append(4, "1");

        levelToIntervalMap.append(5, new String[] { "Perfect Fourth", "Perfect Fifth", "Minor Sixth", "Major Sixth", "Perfect Octave" });
        levelToIntervalTypeMap.append(5, "4");
        levelToChordsMap.append(5, new String[] { "Dom 7 Root Pos" });
        levelToChordTypeMap.append(5, "1");
        levelToCadencesMap.append(5, new String [] { });
        levelToProgressionsMap.append(5, new String[] {  });
        levelToProgressionLengthMap.append(5, "3" );
        levelToProgressionTonalityMap.append(5, "1");

        levelToIntervalMap.append(6, new String[] { "Minor Second", "Major Second", "Minor Third", "Major Third", "Perfect Fourth", "Perfect Fifth", "Minor Sixth", "Major Sixth", "Perfect Octave" });
        levelToIntervalTypeMap.append(6, "4");
        levelToChordsMap.append(6, new String[] { "Dom 7 Root Pos", "Dim 7" });
        levelToChordTypeMap.append(6, "1");
        levelToCadencesMap.append(6, new String [] { });
        levelToProgressionsMap.append(6, new String[] { });
        levelToProgressionLengthMap.append(6, "3" );
        levelToProgressionTonalityMap.append(6, "3");

        levelToIntervalMap.append(7, new String[] { "Minor Second", "Major Second", "Minor Third", "Major Third", "Perfect Fourth", "Perfect Fifth", "Minor Sixth", "Major Sixth", "Minor Seventh", "Major Seventh", "Perfect Octave" });
        levelToIntervalTypeMap.append(7, "4");
        levelToChordsMap.append(7, new String[] { "Dom 7 Root Pos", "Dim 7", "Aug" });
        levelToChordTypeMap.append(7, "1");
        levelToCadencesMap.append(7, new String [] { });
        levelToProgressionsMap.append(7, new String[] { });
        levelToProgressionLengthMap.append(7, "3" );
        levelToProgressionTonalityMap.append(7, "3");

        levelToIntervalMap.append(8, new String[] { "Minor Second", "Major Second", "Minor Third", "Major Third", "Perfect Fourth", "Aug Fourth", "Perfect Fifth", "Minor Sixth", "Major Sixth", "Minor Seventh", "Major Seventh", "Perfect Octave" });
        levelToIntervalTypeMap.append(8, "4");
        levelToChordsMap.append(8, new String[] { "Dom 7 Root Pos", "Dim 7", "Aug" });
        levelToChordTypeMap.append(8, "1");
        levelToCadencesMap.append(8, new String [] { });
        levelToProgressionsMap.append(8, new String[] { "six" });
        levelToProgressionLengthMap.append(8, "4" );
        levelToProgressionTonalityMap.append(8, "3");

        levelToIntervalMap.append(9, new String[] { "Minor Second", "Major Second", "Minor Third", "Major Third", "Perfect Fourth", "Aug Fourth", "Perfect Fifth", "Minor Sixth", "Major Sixth", "Minor Seventh", "Major Seventh", "Perfect Octave" });
        levelToIntervalTypeMap.append(9, "4");
        levelToChordsMap.append(9, new String[] { "Major 1st Inv", "Minor 1st Inv", "Dom 7 Root Pos", "Dim 7", "Aug" });
        levelToChordTypeMap.append(9, "1");
        levelToCadencesMap.append(9, new String [] { });
        levelToProgressionsMap.append(9, new String[] { "six" });
        levelToProgressionLengthMap.append(9, "4" );
        levelToProgressionTonalityMap.append(9, "3");

        levelToIntervalMap.append(10, new String[] { "Minor Second", "Major Second", "Minor Third", "Major Third", "Perfect Fourth", "Aug Fourth", "Perfect Fifth", "Minor Sixth", "Major Sixth", "Minor Seventh", "Major Seventh", "Perfect Octave", "Major Ninth", "Minor Ninth" });
        levelToIntervalTypeMap.append(10, "4");
        levelToChordsMap.append(10, new String[] { "Major 1st Inv", "Minor 1st Inv", "Dom 7 Root Pos", "Dim 7", "Aug", "Major 7 Root Pos", "Minor 7 Root Pos" }); // todo: add four note chords (release 6.4)
        levelToChordTypeMap.append(10, "1");
        levelToCadencesMap.append(10, new String [] { });
        levelToProgressionsMap.append(10, new String[] { "six", "cadential"});
        levelToProgressionLengthMap.append(10, "5" );
        levelToProgressionTonalityMap.append(10, "3");
    }

    public static Set<String> getIntervalsFromLevel(int level) {
        return new HashSet<>(Arrays.asList(levelToIntervalMap.get(level)));
    }

    public static String getIntervalTypeFromLevel(int level) {
        return levelToIntervalTypeMap.get(level);
    }

    public static Set<String> getChordsFromLevel(int level) {
        return new HashSet<>(Arrays.asList(levelToChordsMap.get(level)));
    }

    public static String getChordTypeFromLevel(int level) {
        return levelToChordTypeMap.get(level);
    }

    public static Set<String> getCadencesFromLevel(int level) {
        return new HashSet<>(Arrays.asList(levelToCadencesMap.get(level)));
    }

    public static Set<String> getProgressionsFromLevel(int level) {
        return new HashSet<>(Arrays.asList(levelToProgressionsMap.get(level)));
    }

    public static String getProgressionTonalityFromLevel(int level) {
        return levelToProgressionTonalityMap.get(level);
    }

    public static String getProgressionLengthFromLevel(int level) {
        return levelToProgressionLengthMap.get(level);
    }
}
