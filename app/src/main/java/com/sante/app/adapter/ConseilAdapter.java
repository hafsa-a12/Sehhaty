package com.sante.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sante.app.R;
import com.sante.app.model.Conseil;

import java.util.List;

/**
 * ConseilAdapter - Adaptateur pour afficher les conseils dans un RecyclerView
 * 
 * Un RecyclerView est une liste scrollable et efficace qui :
 *  - Réutilise les ViewHolder (économise la mémoire)
 *  - Affiche plusieurs éléments dans une liste
 *  - Supporte le scroll (défilement)
 * 
 * Rôle du ConseilAdapter :
 *  - Créer les vues (item_conseil.xml) pour chaque conseil
 *  - Remplir les données (bind) du conseil dans chaque vue
 *  - Gérer la liste dynamiquement
 * 
 * Processus d'un RecyclerView :
 *  1. onCreateViewHolder() : crée la vue XML
 *  2. onBindViewHolder() : remplit les données dans la vue
 *  3. getItemCount() : retourne le nombre d'éléments
 */
public class ConseilAdapter extends RecyclerView.Adapter<ConseilAdapter.ViewHolder> {

    // ─── ATTRIBUTS ─────────────────────────────────────────
    
    private List<Conseil> conseils; // Liste des conseils à afficher
    private final Context context; // Contexte Android (pour LayoutInflater, etc.)

    /**
     * Constructeur de l'adaptateur
     * 
     * @param context : contexte Android
     * @param conseils : liste des conseils à afficher
     */
    public ConseilAdapter(Context context, List<Conseil> conseils) {
        this.context = context;
        this.conseils = conseils;
    }

    /**
     * onCreateViewHolder() - Crée une nouvelle vue pour chaque élément
     * 
     * Appelé quand le RecyclerView a besoin d'une nouvelle vue
     * 
     * Processus :
     *  1. Récupère le LayoutInflater
     *  2. Charge le fichier XML item_conseil.xml
     *  3. Crée un ViewHolder avec cette vue
     *  4. Retourne le ViewHolder
     * 
     * @param parent : le conteneur parent (RecyclerView)
     * @param viewType : type de vue (utile si plusieurs types de vues)
     * @return : nouveau ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater : transforme un fichier XML en objet View
        LayoutInflater inflater = LayoutInflater.from(context);
        
        // Charge le fichier item_conseil.xml et crée une View
        // parent : conteneur où ajouter la vue
        // false : n'attache pas tout de suite
        View view = inflater.inflate(R.layout.item_conseil, parent, false);
        
        // Crée et retourne un nouveau ViewHolder avec cette vue
        return new ViewHolder(view);
    }

    /**
     * onBindViewHolder() - Remplit les données dans une vue existante
     * 
     * Appelé quand le RecyclerView veut afficher un conseil à une certaine position
     * 
     * Processus :
     *  1. Récupère le Conseil à la position
     *  2. Remplit les TextViews avec les données du Conseil
     * 
     * @param holder : ViewHolder contenant les vues (TextViews)
     * @param position : position du conseil dans la liste (0, 1, 2...)
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Récupère le Conseil à la position actuelle
        Conseil c = conseils.get(position);
        
        // Remplit les TextViews avec les données du Conseil
        holder.tvTitre.setText(c.getTitre()); // Affiche le titre du conseil
        holder.tvContenu.setText(c.getContenu()); // Affiche le contenu (description)
        holder.tvCategorie.setText(c.getCategorie()); // Affiche la catégorie (nutrition, sport, etc.)
    }

    /**
     * getItemCount() - Retourne le nombre total de conseils à afficher
     * 
     * Le RecyclerView appelle onBindViewHolder() une fois pour chaque élément
     * Donc il appelle cette méthode pour savoir combien de fois boucler
     * 
     * @return : nombre de conseils (0 si liste vide)
     */
    @Override
    public int getItemCount() {
        // Si la liste est null, retourne 0, sinon retourne la taille
        return conseils == null ? 0 : conseils.size();
    }

    /**
     * updateList() - Met à jour la liste dynamiquement
     * 
     * Utile quand les données changent après l'initialisation
     * Exemples :
     *  - L'utilisateur rafraîchit la liste (pull-to-refresh)
     *  - Nouveaux conseils reçus de l'API
     * 
     * @param newList : nouvelle liste de conseils
     */
    public void updateList(List<Conseil> newList) {
        // Remplace l'ancienne liste par la nouvelle
        this.conseils = newList;
        
        // Notifie le RecyclerView que les données ont changé
        // Le RecyclerView va redessiner tous les éléments
        notifyDataSetChanged();
    }

    /**
     * ViewHolder - Classe interne qui contient les références aux vues
     * 
     * Un ViewHolder "tient" une vue (item_conseil.xml)
     * Il contient des références à tous les TextViews de cette vue
     * Le RecyclerView réutilise ces ViewHolder quand l'utilisateur scroll
     * 
     * Exemple de réutilisation :
     *  - Écran initial : ViewHolder affiche conseil 1
     *  - Utilisateur scroll vers le bas
     *  - Même ViewHolder affiche conseil 2 (ancien conseil 1 remplacé)
     * 
     * Cela économise la mémoire comparé à créer une nouvelle vue pour chaque élément
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        
        // ─── RÉFÉRENCES AUX VUES ───────────────────────
        TextView tvTitre; // TextView pour afficher le titre du conseil
        TextView tvContenu; // TextView pour afficher le contenu
        TextView tvCategorie; // TextView pour afficher la catégorie

        /**
         * Constructeur du ViewHolder
         * 
         * Récupère les références à toutes les vues dans item_conseil.xml
         * 
         * @param itemView : la vue complète du layout item_conseil.xml
         */
        ViewHolder(View itemView) {
            super(itemView); // Appelle le constructeur parent
            
            // Récupère les TextViews par leur ID depuis le layout XML
            tvTitre = itemView.findViewById(R.id.tvConseilTitre);
            tvContenu = itemView.findViewById(R.id.tvConseilContenu);
            tvCategorie = itemView.findViewById(R.id.tvConseilCategorie);
        }
    }
}

