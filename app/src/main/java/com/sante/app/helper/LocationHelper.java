package com.sante.app.helper;

import java.util.Locale;

/**
 * Calculs de distance (formule de Haversine). Java pur, sans dépendance Android.
 */
public final class LocationHelper {

    private static final double EARTH_RADIUS_KM = 6371.0088;

    private LocationHelper() {}

    /** Distance à vol d'oiseau en km, arrondie à 0,1 km. */
    public static double calculerDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double km = 2 * EARTH_RADIUS_KM * Math.asin(Math.min(1.0, Math.sqrt(a)));
        return Math.round(km * 10.0) / 10.0;
    }

    /** "850 m" sous 1 km, sinon "3.4 km". */
    public static String formaterDistance(double distanceKm) {
        if (distanceKm < 1.0) {
            return (int) (distanceKm * 1000) + " m";
        }
        return String.format(Locale.US, "%.1f km", distanceKm);
    }
}
