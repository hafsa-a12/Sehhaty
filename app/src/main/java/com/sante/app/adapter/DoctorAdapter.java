package com.sante.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sante.app.R;
import com.sante.app.model.Doctor;

import java.util.List;
import java.util.Locale;

/**
 * DoctorAdapter - Adaptateur pour afficher les médecins dans un RecyclerView
 * 
 * Rôle :
 *  - Afficher une liste scrollable de médecins
 *  - Gérer les clics sur les cartes médecins
 *  - Afficher les infos : nom, spécialité, disponibilité, note, distance
 *  - Créer un avatar avec la première lettre du nom
 * 
 * Différence avec ConseilAdapter :
 *  - Ajoute des boutons "Réserver" pour chaque médecin
 *  - Affiche la distance calculée par GPS
 *  - Crée un avatar (lettre initiale dans un cercle)
 */
public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {

    /**
     * Interface OnDoctorClickListener - Gère les clics sur les médecins
     * 
     * L'activité (DoctorListActivity) implémente cette interface
     * pour pouvoir réagir aux clics
     */
    public interface OnDoctorClickListener {
        /**
         * Appelé quand l'utilisateur clique sur le bouton "Réserver"
         * @param doctor : le médecin sélectionné
         */
        void onReserverClick(Doctor doctor);
        
        /**
         * Appelé quand l'utilisateur clique sur la carte du médecin
         * @param doctor : le médecin sélectionné
         */
        void onCardClick(Doctor doctor);
    }

    // ─── ATTRIBUTS ─────────────────────────────────────────
    private final Context context; // Contexte Android
    private List<Doctor> doctors; // Liste des médecins à afficher
    private final OnDoctorClickListener listener; // Listener pour gérer les clics

    /**
     * Constructeur du DoctorAdapter
     * 
     * @param context : contexte Android
     * @param doctors : liste des médecins à afficher
     * @param listener : interface pour gérer les clics (peut être null)
     */
    public DoctorAdapter(Context context, List<Doctor> doctors, OnDoctorClickListener listener) {
        this.context = context;
        this.doctors = doctors;
        this.listener = listener;
    }

    /**
     * onCreateViewHolder() - Crée la vue XML pour chaque médecin
     * 
     * Charge le fichier item_doctor.xml qui contient :
     *  - tvAvatar : cercle avec initiale du nom
     *  - tvNom : nom du médecin
     *  - tvSpecialite : type de médecin
     *  - tvDisponible : état de disponibilité
     *  - tvNote : évaluation en étoiles
     *  - tvDistance : distance calculée en km
     *  - btnReserver : bouton pour réserver
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Crée la vue XML
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor, parent, false);
        return new ViewHolder(view);
    }

    /**
     * onBindViewHolder() - Remplit les données du médecin dans la vue
     * 
     * Processus :
     *  1. Récupère le Doctor à la position
     *  2. Remplit tous les TextViews avec les données
     *  3. Formate la note (1 décimal)
     *  4. Formate la distance en km
     *  5. Crée un avatar avec la première lettre du nom
     *  6. Configure les listeners pour les clics
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Récupère le médecin à cette position
        Doctor d = doctors.get(position);

        // Remplit le nom du médecin
        holder.tvNom.setText(d.getNom());
        
        // Remplit la spécialité (ex: "Cardiologue")
        holder.tvSpecialite.setText(d.getSpecialite());
        
        // Affiche "Disponible" ou "Occupe" selon le statut
        holder.tvDisponible.setText(d.isDisponible() ? "Disponible" : "Occupe");
        
        // Affiche la note avec 1 chiffre après la virgule (ex: 4.8)
        // String.format : formate un nombre avec un certain nombre de décimales
        // Locale.getDefault() : utilise la locale du téléphone (ex: fr, en)
        holder.tvNote.setText(String.format(Locale.getDefault(), "%.1f", d.getNote()));
        
        // Affiche la distance en km (ou "-" si distance non calculée)
        holder.tvDistance.setText(d.getDistance() > 0
                ? String.format(Locale.getDefault(), "%.1f km", d.getDistance())
                : "-");

        // ─── CRÉATION DE L'AVATAR ──────────────────────────────────────
        // Un avatar est généralement la première lettre du nom dans un cercle
        String nom = d.getNom() == null ? "" : d.getNom().trim();
        
        // Si le nom est vide, affiche "D" par défaut
        // Sinon affiche la première lettre en majuscule
        holder.tvAvatar.setText(
            nom.isEmpty() ? "D" : nom.substring(0, 1).toUpperCase(Locale.ROOT)
        );

        // ─── CONFIGURATION DES CLICS ────────────────────────────────────
        
        // Clic sur la carte : appelle listener.onCardClick()
        // Si listener est null, rien ne se passe (évite les crashes)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCardClick(d);
            }
        });
        
        // Clic sur le bouton "Réserver" : appelle listener.onReserverClick()
        holder.btnReserver.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReserverClick(d);
            }
        });
    }

    /**
     * getItemCount() - Retourne le nombre total de médecins
     * 
     * @return : nombre de médecins (0 si liste vide/null)
     */
    @Override
    public int getItemCount() {
        return doctors == null ? 0 : doctors.size();
    }

    /**
     * updateList() - Met à jour la liste dynamiquement
     * 
     * Utilisé quand les données changent :
     *  - Filtrage par spécialité
     *  - Recherche par nom
     *  - Nouvel ordre (tri par distance)
     * 
     * @param newList : nouvelle liste de médecins
     */
    public void updateList(List<Doctor> newList) {
        this.doctors = newList;
        // Notifie le RecyclerView que toutes les données ont changé
        notifyDataSetChanged();
    }

    /**
     * ViewHolder - Classe interne contenant les références aux vues
     * 
     * Stocke les références à tous les TextViews et Buttons de item_doctor.xml
     * Le RecyclerView réutilise ces ViewHolder quand l'utilisateur scroll
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        
        // ─── RÉFÉRENCES AUX TEXTVIEWS ──────────────────────
        TextView tvAvatar; // Avatar (première lettre du nom)
        TextView tvNom; // Nom du médecin
        TextView tvSpecialite; // Spécialité (Cardiologue, Dentiste, etc.)
        TextView tvDisponible; // "Disponible" ou "Occupe"
        TextView tvDistance; // Distance en km
        TextView tvNote; // Note/évaluation
        Button btnReserver; // Bouton pour réserver un rendez-vous

        /**
         * Constructeur - Récupère les références aux vues
         * 
         * @param itemView : la vue complète du layout item_doctor.xml
         */
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            
            // Récupère chaque vue par son ID depuis le layout XML
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvNom = itemView.findViewById(R.id.tvNom);
            tvSpecialite = itemView.findViewById(R.id.tvSpecialite);
            tvDisponible = itemView.findViewById(R.id.tvDisponible);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvNote = itemView.findViewById(R.id.tvNote);
            btnReserver = itemView.findViewById(R.id.btnReserver);
        }
    }
}

