package fr.dsc.demo.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Objet {
    @Id @GeneratedValue
    private  int id;
    private String type;
    private String nom;
    private String valeur;
    @ManyToOne
    @JoinColumn(name = "offre")
    private Troc offre;
    @ManyToOne
    @JoinColumn(name = "demande")
    private Troc demande;

}
