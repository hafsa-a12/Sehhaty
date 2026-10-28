package com.sante.app.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/** Médecin : entité Room (table "doctors") et modèle JSON (Gson) à la fois. */
@Entity(tableName = "doctors")
public class Doctor {

    @PrimaryKey
    private int id;
    private String nom;
    private String specialite;
    private String telephone;
    private String adresse;
    private double latitude;
    private double longitude;
    private boolean disponible;
    private float note;

    /** Distance à l'utilisateur en km : calculée à l'exécution, ni stockée ni sérialisée. */
    @Ignore
    private transient double distance;

    /** Constructeur utilisé par Room et Gson. */
    public Doctor() {}

    @Ignore
    public Doctor(int id, String nom, String specialite, String telephone,
                  String adresse, double latitude, double longitude,
                  boolean disponible, float note) {
        this.id = id;
        this.nom = nom;
        this.specialite = specialite;
        this.telephone = telephone;
        this.adresse = adresse;
        this.latitude = latitude;
        this.longitude = longitude;
        this.disponible = disponible;
        this.note = note;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getSpecialite() { return specialite; }
    public String getTelephone() { return telephone; }
    public String getAdresse() { return adresse; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean isDisponible() { return disponible; }
    public float getNote() { return note; }
    public double getDistance() { return distance; }

    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public void setNote(float note) { this.note = note; }
    public void setDistance(double distance) { this.distance = distance; }
}
