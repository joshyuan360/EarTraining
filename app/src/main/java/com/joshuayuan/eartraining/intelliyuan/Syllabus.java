package com.joshuayuan.eartraining.intelliyuan;

import android.util.SparseArray;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * RCM syllabus requirements.
 */
public class Syllabus {
    private static SparseArray<String[]> levelToIntervalMap;
    private static SparseArray<String> levelToIntervalTypeMap;
    private static SparseArray<String[]> levelToChordsMap;
    private static SparseArray<String[]> levelToCadencesMap;
    private static SparseArray<String[]> levelToProgressionsMap;
    private static SparseArray<String> levelToProgressionLengthMap;
    private static SparseArray<String> levelToProgressionTonalityMap;
    // todo: this is piano only, add instrumental (release 6.3???) change the name to make it clear :)
    static {
        levelToIntervalMap = new SparseArray<>();
        levelToIntervalTypeMap = new SparseArray<>();
        levelToChordsMap = new SparseArray<>();
        levelToCadencesMap = new SparseArray<>();
        levelToProgressionsMap = new SparseArray<>();
        levelToProgressionLengthMap = new SparseArray<>();
        levelToProgressionTonalityMap = new SparseArray<>();

        levelToIntervalMap.append(1, new String[] { });
        levelToIntervalTypeMap.append(1, "4"); // todo: intervals sometimes played twice (release 6.3)
        levelToChordsMap.append(1, new String[] { });
        levelToCadencesMap.append(1, new String [] { });
        levelToProgressionsMap.append(1, new String[] {  });
        levelToProgressionLengthMap.append(1, "3" );
        levelToProgressionTonalityMap.append(1, "1");

        levelToIntervalMap.append(2, new String[] { "Perfect Fifth" });
        levelToIntervalTypeMap.append(2, "4");
        levelToChordsMap.append(2, new String[] { });
        levelToCadencesMap.append(2, new String [] { });
        levelToProgressionsMap.append(2, new String[] {  });
        levelToProgressionLengthMap.append(2, "3" );
        levelToProgressionTonalityMap.append(2, "1");

        levelToIntervalMap.append(3, new String[] { "Perfect Fourth", "Perfect Fifth" });
        levelToIntervalTypeMap.append(3, "4");
        levelToChordsMap.append(3, new String[] { });
        levelToCadencesMap.append(3, new String [] { });
        levelToProgressionsMap.append(3, new String[] {  });
        levelToProgressionLengthMap.append(3, "3" );
        levelToProgressionTonalityMap.append(3, "1");

        // grade 5
        levelToIntervalMap.append(5, new String[] { "Perfect Fourth", "Perfect Fifth", "Minor Sixth", "Major Sixth", "Perfect Octave" });
        levelToIntervalTypeMap.append(5, "4");
        levelToChordsMap.append(5, new String[] { "Dom 7 Root Pos" });
        levelToCadencesMap.append(5, new String [] { });
        levelToProgressionsMap.append(5, new String[] {  });
        levelToProgressionLengthMap.append(5, "3" );
        levelToProgressionTonalityMap.append(5, "1");

        levelToIntervalMap.append(6, new String[] { "Minor Second", "Major Second", "Minor Third", "Major Third", "Perfect Fourth", "Perfect Fifth", "Minor Sixth", "Major Sixth", "Perfect Octave" });
        levelToIntervalTypeMap.append(6, "4");
        levelToChordsMap.append(6, new String[] { "Dom 7 Root Pos", "Dim 7 none" });
        levelToCadencesMap.append(6, new String [] { });
        levelToProgressionsMap.append(6, new String[] { });
        levelToProgressionLengthMap.append(6, "3" );
        levelToProgressionTonalityMap.append(6, "1");

        levelToIntervalMap.append(7, new String[] { "Minor Second", "Major Second", "Minor Third", "Major Third", "Perfect Fourth", "Perfect Fifth", "Minor Sixth", "Major Sixth", "Minor Seventh", "Major Seventh", "Perfect Octave" });
        levelToIntervalTypeMap.append(7, "4");
        levelToChordsMap.append(7, new String[] { "Dom 7 Root Pos", "Dim 7 none" }); // todo: chords needs augmented
        levelToCadencesMap.append(7, new String [] { });
        levelToProgressionsMap.append(7, new String[] { "six" });
        levelToProgressionLengthMap.append(7, "3" );
        levelToProgressionTonalityMap.append(7, "1");

        levelToIntervalMap.append(8, new String[] { "Minor Second", "Major Second", "Minor Third", "Major Third", "Perfect Fourth", "Aug 4", "Perfect Fifth", "Minor Sixth", "Major Sixth", "Minor Seventh", "Major Seventh", "Perfect Octave" });
        levelToIntervalTypeMap.append(8, "4");
        levelToChordsMap.append(8, new String[] { "Dom 7 Root Pos", "Dim 7 none" });
        levelToCadencesMap.append(8, new String [] { });
        levelToProgressionsMap.append(8, new String[] { "six" });
        levelToProgressionLengthMap.append(8, "4" );
        levelToProgressionTonalityMap.append(8, "1");

        levelToIntervalMap.append(9, new String[] { "Minor Second", "Major Second", "Minor Third", "Major Third", "Perfect Fourth", "Aug 4", "Perfect Fifth", "Minor Sixth", "Major Sixth", "Minor Seventh", "Major Seventh", "Perfect Octave" });
        levelToIntervalTypeMap.append(9, "4");
        levelToChordsMap.append(9, new String[] { "Dom 7 Root Pos", "Dim 7 none" });
        levelToCadencesMap.append(9, new String [] { });
        levelToProgressionsMap.append(9, new String[] { "six" });
        levelToProgressionLengthMap.append(9, "4" );
        levelToProgressionTonalityMap.append(9, "1");
        //todo: add 9th intervals (go up to 12 for a balanced UI)
        levelToIntervalMap.append(10, new String[] { "Minor Second", "Major Second", "Minor Third", "Major Third", "Perfect Fourth", "Aug 4", "Perfect Fifth", "Minor Sixth", "Major Sixth", "Minor Seventh", "Major Seventh", "Perfect Octave" });
        levelToIntervalTypeMap.append(10, "4");
        levelToChordsMap.append(10, new String[] { "Dom 7 Root Pos", "Dim 7 none" }); // todo: add four note  (release 6.3)
        levelToCadencesMap.append(10, new String [] { });
        levelToProgressionsMap.append(10, new String[] { "six", "Cadential 6-4"});
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
