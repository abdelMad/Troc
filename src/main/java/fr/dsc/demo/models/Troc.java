package fr.dsc.demo.models;

import javax.persistence.*;

@Entity
public class Troc {
    @Id @GeneratedValue
    private  int id;
    @ManyToOne
    @JoinColumn(name = "emetteur")
    private Utilisateur emetteur;
    @ManyToOne
    @JoinColumn(name = "recepteur")
    private Utilisateur recepteur;

    private String image;
    @Column(columnDefinition = "TEXT")
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Utilisateur getEmetteur() {
        return emetteur;
    }

    public void setEmetteur(Utilisateur emetteur) {
        this.emetteur = emetteur;
    }

    public Utilisateur getRecepteur() {
        return recepteur;
    }

    public void setRecepteur(Utilisateur recepteur) {
        this.recepteur = recepteur;
    }
}
