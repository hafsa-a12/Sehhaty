package com.sehhaty.model;

import jakarta.persistence.*;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;
    private String specialite;
    private String telephone;
    private String adresse;
    private double latitude;
    private double longitude;
    private boolean disponible;
    private double note;

    protected Doctor() {}

    public Doctor(String nom, String specialite, String telephone, String adresse,
                  double latitude, double longitude, boolean disponible, double note) {
        this.nom = nom;
        this.specialite = specialite;
        this.telephone = telephone;
        this.adresse = adresse;
        this.latitude = latitude;
        this.longitude = longitude;
        this.disponible = disponible;
        this.note = note;
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getSpecialite() { return specialite; }
    public String getTelephone() { return telephone; }
    public String getAdresse() { return adresse; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean isDisponible() { return disponible; }
    public double getNote() { return note; }
}
