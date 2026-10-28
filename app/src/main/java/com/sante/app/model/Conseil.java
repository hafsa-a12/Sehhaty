package com.sante.app.model;

/**
 * Conseil - Classe modèle représentant un conseil santé/bien-être
 * 
 * Exemple de conseil :
 *  - Titre : "Hydratation quotidienne"
 *  - Contenu : "Buvez 1.5 à 2 litres d'eau par jour"
 *  - Catégorie : "nutrition"
 *  - Icône : "water"
 * 
 * Utilisation :
 *  - Affichée dans RecyclerView de la section "Conseils"
 *  - Stocker en base de données SQLite
 */
public class Conseil {
    // ─── ATTRIBUTS ────────────────────────────────────────
    
    private int id; // Identifiant unique du conseil
    private String titre; // Titre du conseil (court, accrocheur)
    private String contenu; // Texte complet du conseil (détails)
    private String categorie; // Catégorie : "nutrition", "sport", "prevention", "bien-etre", etc.
    private String icone; // Nom de l'icône à afficher (ex: "water", "fitness", "sleep")

    // ─── CONSTRUCTEURS ────────────────────────────────────
    
    /**
     * Constructeur vide (utilisé par sérialisation/ORM)
     */
    public Conseil() {}

    /**
     * Constructeur avec paramètres
     * 
     * @param titre : titre du conseil
     * @param contenu : description détaillée
     * @param categorie : domaine du conseil
     * @param icone : icône associée
     */
    public Conseil(String titre, String contenu, String categorie, String icone) {
        this.titre = titre;
        this.contenu = contenu;
        this.categorie = categorie;
        this.icone = icone;
    }

    // ─── GETTERS (Lecture des données) ────────────────────
    public int getId() { return id; }
    public String getTitre() { return titre; }
    public String getContenu() { return contenu; }
    public String getCategorie() { return categorie; }
    public String getIcone() { return icone; }

    // ─── SETTERS (Modification des données) ───────────────
    public void setId(int id) { this.id = id; }
    public void setTitre(String titre) { this.titre = titre; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public void setIcone(String icone) { this.icone = icone; }
}
