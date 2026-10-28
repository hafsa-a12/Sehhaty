package com.sante.app.util;

/**
 * Détection de chute basée sur la norme de l'accélération (m/s²).
 * Un délai de "refroidissement" évite de déclencher l'alerte en rafale.
 */
public class FallDetector {

    public static final double THRESHOLD = 25.0;       // ~2,5 g
    public static final long COOLDOWN_MS = 5_000L;

    private final double threshold;
    private final long cooldownMs;
    private long lastAlertAt = Long.MIN_VALUE;

    public FallDetector() {
        this(THRESHOLD, COOLDOWN_MS);
    }

    public FallDetector(double threshold, long cooldownMs) {
        this.threshold = threshold;
        this.cooldownMs = cooldownMs;
    }

    public static double magnitude(float x, float y, float z) {
        return Math.sqrt((double) x * x + (double) y * y + (double) z * z);
    }

    /** @return true si une chute possible est détectée ET que le délai depuis la dernière alerte est écoulé. */
    public boolean onSample(float x, float y, float z, long nowMs) {
        if (magnitude(x, y, z) <= threshold) return false;
        if (lastAlertAt != Long.MIN_VALUE && nowMs - lastAlertAt < cooldownMs) return false;
        lastAlertAt = nowMs;
        return true;
    }
}
