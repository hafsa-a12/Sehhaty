package com.sante.app.util;

import com.sante.app.helper.LocationHelper;
import com.sante.app.model.Doctor;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Logique de filtrage / tri de la liste des médecins (Java pur).
 * Sortie de la couche ViewModel : l'Activity n'a plus qu'à afficher.
 */
public final class DoctorFilter {

    private DoctorFilter() {}

    /**
     * @param all        tous les médecins
     * @param query      texte libre (nom ou spécialité), insensible à la casse et aux accents ; null/vide = pas de filtre
     * @param specialite spécialité exacte (insensible casse/accents) ; null/vide = toutes
     * @param userLat    latitude de l'utilisateur, ou null si inconnue
     * @param userLng    longitude de l'utilisateur, ou null si inconnue
     * @return nouvelle liste ; si la position est connue, les distances sont renseignées et la liste triée par proximité
     */
    public static List<Doctor> apply(List<Doctor> all, String query, String specialite,
                                     Double userLat, Double userLng) {
        List<Doctor> result = new ArrayList<>();
        if (all == null) return result;

        String q = normalize(query);
        String spec = normalize(specialite);

        for (Doctor d : all) {
            String nom = normalize(d.getNom());
            String sp = normalize(d.getSpecialite());
            boolean matchQuery = q.isEmpty() || nom.contains(q) || sp.contains(q);
            boolean matchSpec = spec.isEmpty() || sp.equals(spec);
            if (matchQuery && matchSpec) result.add(d);
        }

        if (userLat != null && userLng != null) {
            for (Doctor d : result) {
                d.setDistance(LocationHelper.calculerDistance(
                        userLat, userLng, d.getLatitude(), d.getLongitude()));
            }
            Collections.sort(result, (a, b) -> Double.compare(a.getDistance(), b.getDistance()));
        }
        return result;
    }

    /** Minuscules + suppression des accents : "Pédiatre" -> "pediatre". */
    static String normalize(String s) {
        if (s == null) return "";
        String decomposed = Normalizer.normalize(s.trim(), Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
    }
}
