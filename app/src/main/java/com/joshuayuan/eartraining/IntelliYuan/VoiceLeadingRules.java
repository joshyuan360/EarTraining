package com.joshuayuan.eartraining.IntelliYuan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Used by ChordProgressionGenerator to generate a sequence of raw chord progression objects
 * whose order is legal by Western music conventions. These objects must be merged properly
 * before use.
 *
 * @author Joshua Yuan
 */
class VoiceLeadingRules {

    static void initializeChordProgressions(HashMap<String, List<ChordProgression>> chordProgressions) {
        List<ChordProgression> _1r_chordProgressions = new ArrayList<>();
        List<ChordProgression> _11_chordProgressions = new ArrayList<>();
        List<ChordProgression> _4r_chordProgressions = new ArrayList<>();
        List<ChordProgression> _41_chordProgressions = new ArrayList<>();
        List<ChordProgression> _5r_chordProgressions = new ArrayList<>();
        List<ChordProgression> _51_chordProgressions = new ArrayList<>();

        _1r_chordProgressions.add(new ChordProgression(new String[]{"1r", "4r"}, new int[]{-11, -4, 5, 13, -6, -2, 6, 13}));
        _1r_chordProgressions.add(new ChordProgression(new String[]{"1r", "41"}, new int[]{-11, -4, 5, 13, -14, -6, 6, 13}));
        _1r_chordProgressions.add(new ChordProgression(new String[]{"1r", "5r"}, new int[]{-11, -4, 5, 13, -16, -4, 3, 12}));
        _1r_chordProgressions.add(new ChordProgression(new String[]{"1r", "51"}, new int[]{-11, -4, 5, 13, -12, -4, 8, 15}));

        _11_chordProgressions.add(new ChordProgression(new String[]{"11", "5r"}, new int[]{-7, 1, 8, 13, -4, 3, 8, 12}));
        _11_chordProgressions.add(new ChordProgression(new String[]{"11", "5r"}, new int[]{-7, 1, 8, 13, -4, 0, 8, 15}));
        _11_chordProgressions.add(new ChordProgression(new String[]{"11", "51"}, new int[]{-7, 1, 8, 13, -12, -4, 8, 15}));
        _11_chordProgressions.add(new ChordProgression(new String[]{"11", "4r"}, new int[]{-7, 1, 8, 13, -6, 1, 6, 10}));

        _4r_chordProgressions.add(new ChordProgression(new String[]{"4r", "5r"}, new int[]{-6, 1, 10, 18, -4, 0, 8, 15}));
        _4r_chordProgressions.add(new ChordProgression(new String[]{"4r", "51"}, new int[]{-6, 1, 10, 18, -12, 3, 8, 20}));
        _4r_chordProgressions.add(new ChordProgression(new String[]{"4r", "11"}, new int[]{-6, 1, 6, 10, -7, 1, 8, 13}));
        _4r_chordProgressions.add(new ChordProgression(new String[]{"4r", "1r"}, new int[]{-6, -2, 6, 13, -11, -4, 5, 13}));

        _41_chordProgressions.add(new ChordProgression(new String[]{"41", "11"}, new int[]{-14, -6, 6, 13, -11, -4, 5, 13}));
        _41_chordProgressions.add(new ChordProgression(new String[]{"41", "5r"}, new int[]{-14, -6, 6, 13, -16, -4, 3, 12}));
        _41_chordProgressions.add(new ChordProgression(new String[]{"41", "5r"}, new int[]{-14, -6, 6, 13, -16, -9, 8, 12}));

        _5r_chordProgressions.add(new ChordProgression(new String[]{"5r", "1r"}, new int[]{-16, -4, 3, 12, -11, -4, 5, 13}));
        _5r_chordProgressions.add(new ChordProgression(new String[]{"5r", "11"}, new int[]{-4, 3, 8, 12, -7, 1, 8, 13}));
        _5r_chordProgressions.add(new ChordProgression(new String[]{"5r", "11"}, new int[]{-4, 0, 8, 15, -7, 1, 8, 13}));

        _51_chordProgressions.add(new ChordProgression(new String[]{"51", "1r"}, new int[]{-12, -4, 8, 15, -11, -4, 5, 13}));
        _51_chordProgressions.add(new ChordProgression(new String[]{"51", "11"}, new int[]{-12, -4, 8, 15, -7, 1, 8, 13}));

        chordProgressions.put("1r", _1r_chordProgressions);
        chordProgressions.put("11", _11_chordProgressions);
        chordProgressions.put("4r", _4r_chordProgressions);
        chordProgressions.put("41", _41_chordProgressions);
        chordProgressions.put("5r", _5r_chordProgressions);
        chordProgressions.put("51", _51_chordProgressions);
    }

    static void initializeCadentialProgressions(HashMap<String, List<ChordProgression>> cadentialProgressions) {
        List<ChordProgression> _1r_chordProgressions = new ArrayList<>();
        List<ChordProgression> _11_chordProgressions = new ArrayList<>();
        List<ChordProgression> _4r_chordProgressions = new ArrayList<>();
        List<ChordProgression> _41_chordProgressions = new ArrayList<>();
        List<ChordProgression> _5r_chordProgressions = new ArrayList<>();
        List<ChordProgression> _51_chordProgressions = new ArrayList<>();
        List<ChordProgression> _6r_chordProgressions = new ArrayList<>();
        List<ChordProgression> _61_chordProgressions = new ArrayList<>();

        // *** PERFECT CADENCES ***
        _5r_chordProgressions.add(new ChordProgression(new String[]{"5r", "1r"}, new int[]{8, 8, 15, 24, 1, 8, 17, 25}));
        _51_chordProgressions.add(new ChordProgression(new String[]{"51", "1r"}, new int[]{0, 8, 15, 20, 1, 8, 13, 17}));
        _5r_chordProgressions.add(new ChordProgression(new String[]{"5r", "11"}, new int[]{8, 8, 15, 24, 5, 8, 13, 25}));
        _5r_chordProgressions.add(new ChordProgression(new String[]{"5r", "1r"}, new int[]{-4, 8, 12, 15, 1, 5, 13, 13}));
        _51_chordProgressions.add(new ChordProgression(new String[]{"51", "1r"}, new int[]{0, 8, 20, 27, 1, 13, 20, 29}));

        // *** IMPERFECT CADENCES ***
        _1r_chordProgressions.add(new ChordProgression(new String[]{"1r", "5r"}, new int[]{1, 8, 17, 25, -4, 8, 15, 24}));
        _1r_chordProgressions.add(new ChordProgression(new String[]{"1r", "5r"}, new int[]{1, 17, 20, 25, -4, 12, 20, 27}));
        _1r_chordProgressions.add(new ChordProgression(new String[]{"1r", "5r"}, new int[]{1, 8, 17, 25, -4, 12, 20, 27}));
        _1r_chordProgressions.add(new ChordProgression(new String[]{"1r", "5r"}, new int[]{1, 13, 17, 20, -4, 15, 20, 24}));
        _1r_chordProgressions.add(new ChordProgression(new String[]{"1r", "5r"}, new int[]{1, 13, 20, 29, -4, 12, 20, 27}));
        _11_chordProgressions.add(new ChordProgression(new String[]{"11", "51"}, new int[]{5, 13, 20, 25, 0, 8, 20, 27}));
        _1r_chordProgressions.add(new ChordProgression(new String[]{"1r", "51"}, new int[]{1, 8, 17, 25, 0, 8, 20, 27}));
        _11_chordProgressions.add(new ChordProgression(new String[]{"11", "5r"}, new int[]{5, 13, 20, 25, 8, 12, 20, 27}));
        _41_chordProgressions.add(new ChordProgression(new String[]{"41", "5r"}, new int[]{-14, -6, 6, 13, -16, -4, 3, 12}));
        _41_chordProgressions.add(new ChordProgression(new String[]{"41", "5r"}, new int[]{-14, -6, 6, 13, -16, -9, 8, 12}));
        _6r_chordProgressions.add(new ChordProgression(new String[]{"6r", "5r"}, new int[]{-14, -2, 5, 13, -16, 0, 8, 15}));
        _6r_chordProgressions.add(new ChordProgression(new String[]{"6r", "51"}, new int[]{-14, -7, 1, 10, -12, -4, 3, 8}));
        _61_chordProgressions.add(new ChordProgression(new String[]{"61", "5r"}, new int[]{-11, -2, 10, 17, -16, -4, 12, 15}));

        // *** PLAGAL CADENCES ***
        _4r_chordProgressions.add(new ChordProgression(new String[]{"4r", "1r"}, new int[]{6, 18, 22, 25, 1, 17, 20, 25}));
        _4r_chordProgressions.add(new ChordProgression(new String[]{"4r", "1r"}, new int[]{6, 10, 13, 18, 1, 8, 13, 17}));

        // *** DECEPTIVE CADENCES ***
        _5r_chordProgressions.add(new ChordProgression(new String[]{"5r", "6r"}, new int[]{8, 12, 20, 27, 10, 13, 17, 25}));
        _5r_chordProgressions.add(new ChordProgression(new String[]{"5r", "6r"}, new int[]{8, 12, 20, 27, 10, 13, 17, 25}));
        _5r_chordProgressions.add(new ChordProgression(new String[]{"5r", "6r"}, new int[]{-4, 8, 12, 15, -2, 5, 13, 13}));

        cadentialProgressions.put("1r", _1r_chordProgressions);
        cadentialProgressions.put("11", _11_chordProgressions);
        cadentialProgressions.put("4r", _4r_chordProgressions);
        cadentialProgressions.put("41", _41_chordProgressions);
        cadentialProgressions.put("5r", _5r_chordProgressions);
        cadentialProgressions.put("51", _51_chordProgressions);
        cadentialProgressions.put("6r", _6r_chordProgressions);
        cadentialProgressions.put("61", _61_chordProgressions);
    }

    static void includeSixth(HashMap<String, List<ChordProgression>> chordProgressions) {
        List<ChordProgression> _1r_chordProgressions = chordProgressions.get("1r");
        List<ChordProgression> _11_chordProgressions = chordProgressions.get("11");
        List<ChordProgression> _5r_chordProgressions = chordProgressions.get("5r");
        List<ChordProgression> _51_chordProgressions = chordProgressions.get("51");

        List<ChordProgression> _6r_chordProgressions = new ArrayList<>();
        List<ChordProgression> _61_chordProgressions = new ArrayList<>();

        _1r_chordProgressions.add(new ChordProgression(new String[]{"1r", "6r"}, new int[]{-11, -4, 5, 13, -14, -2, 5, 13}));
        _1r_chordProgressions.add(new ChordProgression(new String[]{"1r", "61"}, new int[]{-11, -4, 5, 13, -11, -2, 5, 10}));
        _11_chordProgressions.add(new ChordProgression(new String[]{"11", "6r"}, new int[]{-7, 1, 1, 8, -11, -2, 5, 10}));

        _5r_chordProgressions.add(new ChordProgression(new String[]{"5r", "6r"}, new int[]{-16, 0, 8, 15, -14, -2, 5, 13}));
        _51_chordProgressions.add(new ChordProgression(new String[]{"51", "6r"}, new int[]{-12, -4, 3, 8, -14, -7, 1, 10}));

        _6r_chordProgressions.add(new ChordProgression(new String[]{"6r", "4r"}, new int[]{-14, -7, 1, 10, -18, -6, 1, 10}));
        _6r_chordProgressions.add(new ChordProgression(new String[]{"6r", "41"}, new int[]{-14, -7, 1, 10, -14, -6, 1, 6}));
        _61_chordProgressions.add(new ChordProgression(new String[]{"61", "4r"}, new int[]{-11, -2, 5, 10, -6, -2, 13, 6}));
        _61_chordProgressions.add(new ChordProgression(new String[]{"61", "4r"}, new int[]{-11, -2, 5, 10, -6, 1, 6, 10}));
        _61_chordProgressions.add(new ChordProgression(new String[]{"61", "41"}, new int[]{-11, -2, 5, 10, -14, 1, 6, 6}));
        _61_chordProgressions.add(new ChordProgression(new String[]{"61", "41"}, new int[]{-11, -2, 5, 10, -14, -6, 6, 13}));

        chordProgressions.put("6r", _6r_chordProgressions);
        chordProgressions.put("61", _61_chordProgressions);
    }

    static void includeCadential(HashMap<String, List<ChordProgression>> chordProgressions) {
        List<ChordProgression> _1r_chordProgressions = chordProgressions.get("1r");
        List<ChordProgression> _11_chordProgressions = chordProgressions.get("11");
        List<ChordProgression> _4r_chordProgressions = chordProgressions.get("4r");
        List<ChordProgression> _41_chordProgressions = chordProgressions.get("41");

        List<ChordProgression> _cad_chordProgressions = new ArrayList<>();

        _1r_chordProgressions.add(new ChordProgression(new String[]{"1r", "cc"}, new int[]{-11, -4, 5, 13, -16, -4, 5, 13}));
        _11_chordProgressions.add(new ChordProgression(new String[]{"11", "cc"}, new int[]{-7, 1, 8, 13, -4, 5, 8, 13}));
        _11_chordProgressions.add(new ChordProgression(new String[]{"11", "cc"}, new int[]{-7, 1, 8, 13, -4, 1, 8, 17}));
        _4r_chordProgressions.add(new ChordProgression(new String[]{"4r", "cc"}, new int[]{-6, 1, 10, 18, -4, 1, 8, 17}));
        _41_chordProgressions.add(new ChordProgression(new String[]{"41", "cc"}, new int[]{-14, -6, 6, 13, -16, -4, 5, 13}));
        _41_chordProgressions.add(new ChordProgression(new String[]{"41", "cc"}, new int[]{-14, -6, 6, 13, -16, -7, 8, 13}));

        // duplicate of above to increases chance of landing on a cadential 64 (roughly 1/6 after this addition)
        _1r_chordProgressions.add(new ChordProgression(new String[]{"1r", "cc"}, new int[]{-11, -4, 5, 13, -16, -4, 5, 13}));
        _11_chordProgressions.add(new ChordProgression(new String[]{"11", "cc"}, new int[]{-7, 1, 8, 13, -4, 5, 8, 13}));
        _11_chordProgressions.add(new ChordProgression(new String[]{"11", "cc"}, new int[]{-7, 1, 8, 13, -4, 1, 8, 17}));
        _4r_chordProgressions.add(new ChordProgression(new String[]{"4r", "cc"}, new int[]{-6, 1, 10, 18, -4, 1, 8, 17}));
        _41_chordProgressions.add(new ChordProgression(new String[]{"41", "cc"}, new int[]{-14, -6, 6, 13, -16, -4, 5, 13}));
        _41_chordProgressions.add(new ChordProgression(new String[]{"41", "cc"}, new int[]{-14, -6, 6, 13, -16, -7, 8, 13}));

        _cad_chordProgressions.add(new ChordProgression(new String[]{"cc", "5r"}, new int[]{-16, -4, 5, 13, -16, -4, 3, 12}));

        chordProgressions.put("cc", _cad_chordProgressions);
    }
}
