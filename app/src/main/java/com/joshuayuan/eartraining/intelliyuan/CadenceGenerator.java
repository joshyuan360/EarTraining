package com.joshuayuan.eartraining.intelliyuan;

/**
 * Ear Training API for cadence generation.
 * Used for cadence and chord progression activity.
 * @author Joshua Yuan
 */

public class CadenceGenerator {
    public enum Cadence {
        PERFECT, PLAGAL, IMPERFECT, DECEPTIVE
    }

    public static int[] getCadence(Cadence type) {
        switch (type) {
            case PERFECT:
                return randPerfectCadence();
            case PLAGAL:
                return randPlagalCadence();
            case IMPERFECT:
                return randImperfectCadence();
            case DECEPTIVE:
                return randDeceptiveCadence();
        }

        return null;
    }

    /**
     * Generates a random perfect cadence in C major or C minor.
     * The method generates a variety of Perfect Authentic Cadences and Inauthentic Cadences
     * involving V I/i and V7 I/i progressions.
     * Inversions are included, and different SATB possibilities are accounted for.
     * All cadences generated follow voice leading rules set by traditional Western music standards.
     * @return A random perfect cadence in C major (50%) or C minor (50%).
     */
    private static int[] randPerfectCadence() {
        // to switch any cadence to minor, subtract 1 from all values equal to 5, 17, or 29
        int[][] majorPerfectCadences =
                {
                        {8, 8, 15, 24, 1, 8, 17, 25}, // complete 2-3 tonic song, V I
                        {0, 8, 15, 24, 1, 8, 13, 17}, // incomplete 2-1 tonic song, V6 I HELP
                        {8, 8, 15, 24, 5, 8, 13, 25}, // 5-3 inversion song, V I6
                        {-4, 8, 12, 15, 1, 5, 13, 13}, // incomplete 2-1 tonic song, V I
                        {0, 8, 20, 27, 1, 13, 20, 29}, // 2-3 inversion song, V6 I

                        {-4, 12, 15, 18, 1, 13, 13, 17}, // complete 2-1 V7 song, V7 I
                        {8, 8, 18, 24, 1, 8, 17, 25}, // incomplete 5-5 V7 song, V7 I
                        {-4, 6, 12, 15, 1, 5, 8, 13}, // V7 sacrifice song, V7 I

                        {0, 6, 8, 15, 1, 5, 8, 13}, // V(6-5) I
                        {3, 6, 8, 12, 1, 5, 8, 13}, // V(4-3) I
                        {6, 15, 20, 24, 5, 13, 20, 25} // V(4-2) I6
                };

        int randRow = (int)(Math.random() * majorPerfectCadences.length);
        if (Math.random() < 0.5) {
            ChordExtensions.toMinor(majorPerfectCadences[randRow]);
        }

        majorPerfectCadences[randRow] = modifiedCadence(majorPerfectCadences[randRow]);

        return majorPerfectCadences[randRow];
    }

    /**
     * Generates a random imperfect cadence in C major or C minor.
     * The method generates a variety of Imperfect Cadences
     * involving I V progressions.
     * Inversions are included, and different SATB possibilities are accounted for.
     * All cadences generated follow voice leading rules set by traditional Western music standards.
     * @return A random imperfect cadence in C major (50%) or C minor (50%).
     */
    private static int[] randImperfectCadence() {
        // to switch any cadence to minor, subtract 1 from all values equal to 5, 17, or 29
        int[][] majorImperfectCadences =
                {
                        {1, 8, 17, 25, -4, 8, 15, 24}, // I V version 1
                        {1, 17, 20, 25, -4, 12, 20, 27}, // I V version 2
                        {1, 8, 17, 25, -4, 12, 20, 27}, // I V version 3
                        {1, 13, 17, 20, -4, 15, 20, 24}, // I V version 4
                        {1, 13, 20, 29, -4, 12, 20, 27}, // I V version 5

                        {5, 13, 20, 25, 0, 8, 20, 27}, // I6 V6
                        {1, 8, 17, 25, 0, 8, 20, 27}, // I V6
                        {5, 13, 20, 25, 8, 12, 20, 27} // I6 V
                };

        int randRow = (int)(Math.random() * majorImperfectCadences.length);
        if (Math.random() < 0.5) {
            ChordExtensions.toMinor(majorImperfectCadences[randRow]);
        }

        majorImperfectCadences[randRow] = modifiedCadence(majorImperfectCadences[randRow]);

        return majorImperfectCadences[randRow];
    }

    /**
     * Generates a random plagal cadence in C major or C minor.
     * The method generates a variety of Plagal Cadences
     * involving IV I progressions.
     * Inversions are included, and different SATB possibilities are accounted for.
     * All cadences generated follow voice leading rules set by traditional Western music standards.
     * @return A random plagal cadence in C major (50%) or C minor (50%).
     */
    private static int[] randPlagalCadence() {
        // to switch any cadence to minor, subtract 1 from all values equal to -2, 5, 10, 17, 22, 29
        int[][] majorPlagalCadences =
                {
                        {6, 18, 22, 25, 1, 17, 20, 25},//amen
                        {6, 10, 13, 18, 1, 8, 13, 17} //4-3 plagal
                };

        int randRow = (int)(Math.random() * majorPlagalCadences.length);
        if (Math.random() < 0.5) {
            ChordExtensions.toMinor(majorPlagalCadences[randRow]);
        }

        majorPlagalCadences[randRow] = modifiedCadence(majorPlagalCadences[randRow]);

        return majorPlagalCadences[randRow];
    }

    /**
     * Generates a random deceptive cadence in C major or C minor.
     * The method generates a variety of Deceptive Cadences
     * involving V vi progressions.
     * Inversions are included, and different SATB possibilities are accounted for.
     * All cadences generated follow voice leading rules set by traditional Western music standards.
     * @return A random deceptive cadence in C major (50%) or C minor (50%).
     */
    private static int[] randDeceptiveCadence() {
        // to switch any cadence to minor, subtract 1 from all values equal to -2, 5, 10, 17, 22, 29
        int[][] majorDeceptiveCadences =
                {
                        {8, 12, 20, 27, 10, 13, 17, 25}, // weak version 1, V vi
                        {8, 12, 18, 27, 10, 13, 17, 25}, // strong version 1, V7 vi

                        {-4, 8, 12, 15, -2, 5, 13, 13}, // weak version 2, V vi
                        {-4, 6, 12, 15, -2, 5, 13, 13}, // strong version 2, V7 vi
                };

        int randRow = (int)(Math.random() * majorDeceptiveCadences.length);
        if (Math.random() < 0.5) {
            ChordExtensions.toMinor(majorDeceptiveCadences[randRow]);
        }

        if (Math.random() < 0.5) { // change some octaves for more variety
            majorDeceptiveCadences[randRow][0] -= 12;
            majorDeceptiveCadences[randRow][4] -= 12;
            if (Math.random() < 0.5) {
                majorDeceptiveCadences[randRow][1] -= 12;
                majorDeceptiveCadences[randRow][5] -= 12;
            }
        }

        return majorDeceptiveCadences[randRow];
    }

    private static int[] modifiedCadence(int[] cadence) {
        int bass1 = cadence[0], tenor1 = cadence[1], alto1 = cadence[2], soprano1 = cadence[3];
        int bass2 = cadence[4], tenor2 = cadence[5], alto2 = cadence[6], soprano2 = cadence[7];

        if (Math.random() < 0.5) { // move bass down an octave
            int tempLowest = Math.min(bass1 - 12, bass2 - 12);
            int tempHighest = Math.max(soprano1, soprano2);
            if (tempHighest - tempLowest < 35) {
                bass1 -= 12;
                bass2 -= 12;
            }
        }
        if (       alto1 - 12 - 2 > bass1 && alto1 - 12 + 2 < tenor1
                && alto2 - 12 - 2 > bass2 && alto2 - 12 + 2 < tenor2
                && Math.random() < 0.5) { // move alto down an octave
            alto1 -= 12;
            alto2 -= 12;
        }
        if (       bass2 + 12 + 2 < tenor2
                && bass1 > bass2
                && Math.random() < 0.5) {
            bass2 += 12;
        }

        return new int[]{bass1, tenor1, alto1, soprano1, bass2, tenor2, alto2, soprano2};
    }
}
