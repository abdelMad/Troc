package fr.dsc.demo.models;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Troc {
    @Id @GeneratedValue
    private  int id;
    @Transient
    public static final String OFFRE = "offre";
    @Transient
    public static final String DEMANDE = "demande";
    private String type;

    @ManyToOne
    @JoinColumn(name = "message")
    private Message message;
    @OneToMany(mappedBy = "offre",fetch = FetchType.EAGER)
    private List<Objet> offres;
    @OneToMany(mappedBy = "demande",fetch = FetchType.EAGER)
    private List<Objet> demandes;



}
