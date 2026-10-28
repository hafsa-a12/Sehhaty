package com.sehhaty.model;

import jakarta.persistence.*;

@Entity
@Table(name = "conseils")
public class Conseil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false, length = 1000)
    private String contenu;

    private String categorie;
    private String icone;

    protected Conseil() {}

    public Conseil(String titre, String contenu, String categorie, String icone) {
        this.titre = titre;
        this.contenu = contenu;
        this.categorie = categorie;
        this.icone = icone;
    }

    public Long getId() { return id; }
    public String getTitre() { return titre; }
    public String getContenu() { return contenu; }
    public String getCategorie() { return categorie; }
    public String getIcone() { return icone; }
}
