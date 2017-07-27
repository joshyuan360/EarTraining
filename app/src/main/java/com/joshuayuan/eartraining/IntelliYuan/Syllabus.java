package com.joshuayuan.eartraining.IntelliYuan;

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

    static {
        levelToIntervalMap = new SparseArray<>();
        levelToIntervalTypeMap = new SparseArray<>();
        levelToChordsMap = new SparseArray<>();
        levelToCadencesMap = new SparseArray<>();
        levelToProgressionsMap = new SparseArray<>();
        levelToProgressionLengthMap = new SparseArray<>();

        // grade 5
        levelToIntervalMap.append(5, new String[] { "Perfect Fourth", "Perfect Fifth" });
        levelToIntervalTypeMap.append(5, "2");
        levelToChordsMap.append(5, new String[] { "Major Root Pos", "Minor Root Pos" });
        levelToCadencesMap.append(5, new String[] { "Perfect", "Plagal" });
        levelToProgressionsMap.append(5, new String[] { "Perfect Fourth", "Perfect Fifth" });
        levelToProgressionLengthMap.append(5, "easy" );
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

    public static String getProgressionLengthFromLevel(int level) {
        return levelToProgressionLengthMap.get(level);
    }
}
