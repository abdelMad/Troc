package fr.dsc.demo.models;

import fr.dsc.demo.utilities.Util;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Fichier {
    @Id
    @GeneratedValue
    private int id;
    private String ficId;

    @OneToMany(mappedBy = "fichier", fetch = FetchType.EAGER)
    private List<Message> messages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emetteur")
    private Utilisateur emetteur;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recepteur")
    private Utilisateur recepteur;

    private Date dateCreation;

    private boolean statusProp;


    public Fichier() {
        ficId = "f" + Util.generateUniqueToken();
        statusProp = false;
    }
}
