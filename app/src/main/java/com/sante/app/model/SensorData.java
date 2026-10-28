package com.sante.app.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/** Mesure d'un capteur (table "capteurs") : pas, accéléromètre, lumière. */
@Entity(tableName = "capteurs")
public class SensorData {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String type;   // "pas", "accelerometre", "lumiere"
    private float valeur;
    private String unite;  // "pas", "m/s²", "lux"
    private String date;

    public SensorData() {}

    @Ignore
    public SensorData(String type, float valeur, String unite, String date) {
        this.type = type;
        this.valeur = valeur;
        this.unite = unite;
        this.date = date;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public float getValeur() { return valeur; }
    public String getUnite() { return unite; }
    public String getDate() { return date; }

    public void setId(int id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setValeur(float valeur) { this.valeur = valeur; }
    public void setUnite(String unite) { this.unite = unite; }
    public void setDate(String date) { this.date = date; }
}
