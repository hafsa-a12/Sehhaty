package com.sante.app.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/** Note médicale personnelle (table "notes"). */
@Entity(tableName = "notes")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String titre;
    private String contenu;
    private String date;
    private String tag; // general, tension, poids, symptome, medicament, allergie, sport

    public Note() {}

    @Ignore
    public Note(String titre, String contenu, String date, String tag) {
        this.titre = titre;
        this.contenu = contenu;
        this.date = date;
        this.tag = tag;
    }

    public int getId() { return id; }
    public String getTitre() { return titre; }
    public String getContenu() { return contenu; }
    public String getDate() { return date; }
    public String getTag() { return tag; }

    public void setId(int id) { this.id = id; }
    public void setTitre(String titre) { this.titre = titre; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public void setDate(String date) { this.date = date; }
    public void setTag(String tag) { this.tag = tag; }
}
