package com.sante.app.util;

/** Progression vers l'objectif quotidien de pas. */
public final class StepProgress {

    private StepProgress() {}

    /** Pourcentage de l'objectif atteint (peut dépasser 100). */
    public static int percent(int steps, int goal) {
        if (goal <= 0) return 0;
        return (int) ((steps * 100L) / goal);
    }

    public static int remaining(int steps, int goal) {
        return Math.max(0, goal - steps);
    }

    public static boolean isGoalReached(int steps, int goal) {
        return goal > 0 && steps >= goal;
    }
}
