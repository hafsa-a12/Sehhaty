package com.sante.app.util;

/**
 * Convertit le compteur cumulé du capteur TYPE_STEP_COUNTER (pas depuis le dernier redémarrage
 * du téléphone) en "pas aujourd'hui".
 *
 * - nouveau jour  -> la valeur courante devient la nouvelle référence
 * - redémarrage   -> le capteur repart de 0 : la référence est remise à 0
 */
public class StepTracker {

    private int baseline;
    private String baselineDate;

    public StepTracker(int savedBaseline, String savedDate) {
        this.baseline = savedBaseline;
        this.baselineDate = savedDate;
    }

    /** @param today date du jour au format stable (ex. "2026-10-28") */
    public int onTotal(int totalSinceBoot, String today) {
        if (baselineDate == null || !baselineDate.equals(today)) {
            baseline = totalSinceBoot;
            baselineDate = today;
        } else if (totalSinceBoot < baseline) {
            baseline = 0;
        }
        return totalSinceBoot - baseline;
    }

    public int getBaseline() { return baseline; }
    public String getBaselineDate() { return baselineDate; }
}
